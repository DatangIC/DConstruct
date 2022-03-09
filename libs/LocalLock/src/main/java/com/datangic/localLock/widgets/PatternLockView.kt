package com.datangic.localLock.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.os.*
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.IntDef
import com.datangic.localLock.PatternLockViewListener
import com.datangic.localLock.R
import com.datangic.localLock.utils.PatternLockUtils
import com.datangic.localLock.widgets.PatternLockView.AspectRatio.Companion.ASPECT_RATIO_HEIGHT_BIAS
import com.datangic.localLock.widgets.PatternLockView.AspectRatio.Companion.ASPECT_RATIO_SQUARE
import com.datangic.localLock.widgets.PatternLockView.AspectRatio.Companion.ASPECT_RATIO_WIDTH_BIAS
import com.datangic.localLock.widgets.PatternLockView.PatternViewMode.Companion.AUTO_DRAW
import com.datangic.localLock.widgets.PatternLockView.PatternViewMode.Companion.CORRECT
import com.datangic.localLock.widgets.PatternLockView.PatternViewMode.Companion.WRONG
import kotlin.math.sqrt
import kotlin.properties.Delegates

@SuppressLint("Recycle", "CustomViewStyleable")
class PatternLockView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int = 0) : View(context, attributeSet, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    /**
     * Represents the aspect ratio for the View
     */
    @IntDef(value = [ASPECT_RATIO_SQUARE, ASPECT_RATIO_WIDTH_BIAS, ASPECT_RATIO_HEIGHT_BIAS])
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class AspectRatio {
        companion object {
            // Width and height will be same. Minimum of width and height
            const val ASPECT_RATIO_SQUARE = 0

            // Width will be fixed. The height will be the minimum of width and height
            const val ASPECT_RATIO_WIDTH_BIAS = 1

            // Height will be fixed. The width will be the minimum of width and height
            const val ASPECT_RATIO_HEIGHT_BIAS = 2
        }
    }

    /**
     * Represents the different modes in which this view can be represented
     */
    @IntDef(value = [CORRECT, AUTO_DRAW, WRONG])
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class PatternViewMode {
        companion object {
            /**
             * This state represents a correctly drawn pattern by the user. The color of the path and
             * the dots both would be changed to this color.
             *
             *
             * (NOTE - Consider showing this state in a friendly color)
             */
            const val CORRECT = 0

            /**
             * Automatically draw the pattern for demo or tutorial purposes.
             */
            const val AUTO_DRAW = 1

            /**
             * This state represents a wrongly drawn pattern by the user. The color of the path and
             * the dots both would be changed to this color.
             *
             *
             * (NOTE - Consider showing this state in an attention-seeking color)
             */
            const val WRONG: Int = 2
        }
    }

    companion object {
        private val DEFAULT_PATTERN_DOT_COUNT = 3
        private val PROFILE_DRAWING = false
    }

    private val mHandler by lazy { Handler(Looper.getMainLooper()) }

    /**
     * The time (in millis) spend in animating each circle of a lock pattern if
     * the animating mode is set. The entire animation should take this constant
     * the length of the pattern to complete.
     */
    private val MILLIS_PER_CIRCLE_ANIMATING = 700

    // Amount of time (in millis) spent to animate a dot
    private val DEFAULT_DOT_ANIMATION_DURATION = 190

    // Amount of time (in millis) spent to animate a path ends
    private val DEFAULT_PATH_END_ANIMATION_DURATION = 100

    // This can be used to avoid updating the display for very small motions or noisy panels
    private val DEFAULT_DRAG_THRESHOLD = 0.0f

    var mDotCount by Delegates.notNull<Int>()
    private var mAspectRatioEnabled = true
    private var mAspectRatio = 0
    private var mNormalStateColor = 0
    private var mWrongStateColor = 0
    private var mCorrectStateColor = 0
    private var mPathWidth = 0
    private var mDotNormalSize = 0
    private var mDotSelectedSize = 0
    private var mDotAnimationDuration = 0
    private var mPathEndAnimationDuration = 0

    private var mPattern: ArrayList<Dot>
    private var mPatternSize: Int = 0
    private val mPatternDrawLookup: Array<BooleanArray>
    private val mDotStates: Array<Array<DotState?>>
    private val mPatternListeners: MutableList<PatternLockViewListener> = ArrayList()

    private var mDrawingProfilingStarted = false

    private val mDotPaint: Paint = Paint()
    private val mPathPaint: Paint = Paint()

    private val mCurrentPath = Path()
    private val mInvalidate = Rect()
    private val mTempInvalidateRect = Rect()

    private var mViewWidth = 0f
    private var mViewHeight = 0f

    private var mInProgressX = -1f
    private var mInProgressY = -1f

    private lateinit var mFastOutSlowInInterpolator: Interpolator
    private lateinit var mLinearOutSlowInInterpolator: Interpolator

    private var mPatternViewMode = CORRECT
    private var mAnimatingPeriodStart: Long = 0
    private var mInputEnabled = true
    private var mInStealthMode = false
    private var mEnableHapticFeedback = true
    private var mPatternInProgress = false
    private val mHitFactor = 0.6f

    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PatternLockView)
        try {
            mDotCount = typedArray.getInt(R.styleable.PatternLockView_dotCount, DEFAULT_PATTERN_DOT_COUNT)
            Dot.mDotCount = mDotCount
            mAspectRatioEnabled = typedArray.getBoolean(R.styleable.PatternLockView_aspectRatioEnabled, true)
            mAspectRatio = typedArray.getInt(R.styleable.PatternLockView_aspectRatio, ASPECT_RATIO_SQUARE)
            mPathWidth = typedArray.getDimension(R.styleable.PatternLockView_pathWidth, context.resources.getDimension(R.dimen.width_3dp)).toInt()
            mNormalStateColor = typedArray.getColor(R.styleable.PatternLockView_normalStateColor, context.resources.getColor(android.R.color.white))
            mCorrectStateColor = typedArray.getColor(R.styleable.PatternLockView_correctStateColor, context.resources.getColor(android.R.color.white))
            mWrongStateColor = typedArray.getColor(R.styleable.PatternLockView_wrongStateColor, context.resources.getColor(android.R.color.holo_red_dark))
            mDotNormalSize = typedArray.getDimension(R.styleable.PatternLockView_dotNormalSize, context.resources.getDimension(R.dimen.size_10dp)).toInt()
            mDotSelectedSize = typedArray.getDimension(R.styleable.PatternLockView_dotSelectedSize, context.resources.getDimension(R.dimen.size_24dp)).toInt()
            mDotAnimationDuration = typedArray.getInt(R.styleable.PatternLockView_dotAnimationDuration, DEFAULT_DOT_ANIMATION_DURATION)
            mPathEndAnimationDuration = typedArray.getInt(R.styleable.PatternLockView_pathEndAnimationDuration,
                    DEFAULT_PATH_END_ANIMATION_DURATION)

        } catch (e: Exception) {
        } finally {
            typedArray.recycle()
        }

        // The pattern will always be symmetrical
        mPatternSize = mDotCount * mDotCount
        mPattern = ArrayList(mPatternSize)
        mPatternDrawLookup = Array(mDotCount) { BooleanArray(mDotCount) }
        mDotStates = Array(mDotCount) { arrayOfNulls(mDotCount) }


        for (i in 0 until mDotCount) {
            for (j in 0 until mDotCount) {
                mDotStates[i][j] = DotState()
                mDotStates[i][j]?.mSize = mDotNormalSize.toFloat()
            }
        }

        initView()

    }

    private fun initView() {
        isClickable = true

        mPathPaint.apply {
            isAntiAlias = true
            isDither = true
            color = mNormalStateColor
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mPathWidth.toFloat()
        }
        mDotPaint.apply {
            isAntiAlias = true
            isDither = true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !isInEditMode) {
            mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(
                    context, android.R.interpolator.fast_out_slow_in)
            mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(
                    context, android.R.interpolator.linear_out_slow_in)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        if (!mAspectRatioEnabled) {
//            return
//        }
        val oldWidth: Int = resolveMeasured(widthMeasureSpec, suggestedMinimumWidth)
        val oldHeight: Int = resolveMeasured(heightMeasureSpec, suggestedMinimumHeight)
        var newWidth: Int
        var newHeight: Int
        when (mAspectRatio) {
            ASPECT_RATIO_SQUARE -> {
                newWidth = oldWidth.coerceAtMost(oldHeight).also { newHeight = it }
            }
            ASPECT_RATIO_WIDTH_BIAS -> {
                newWidth = oldWidth
                newHeight = oldWidth.coerceAtMost(oldHeight)
            }
            ASPECT_RATIO_HEIGHT_BIAS -> {
                newWidth = oldWidth.coerceAtMost(oldHeight)
                newHeight = oldHeight
            }
            else ->
                throw IllegalStateException("Unknown aspect ratio")
        }
        setMeasuredDimension(newWidth, newWidth)
    }

    private fun resolveMeasured(measureSpec: Int, desired: Int): Int {
        val result: Int
        val specSize = MeasureSpec.getSize(measureSpec)
        result = when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.UNSPECIFIED -> desired
            MeasureSpec.AT_MOST -> specSize.coerceAtLeast(desired)
            MeasureSpec.EXACTLY -> specSize
            else -> specSize
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        val pattern: ArrayList<Dot> = mPattern
        val patternSize = pattern.size
        val drawLookupTable = mPatternDrawLookup

        if (mPatternViewMode == AUTO_DRAW) {
            val oneCycle = (patternSize + 1) * MILLIS_PER_CIRCLE_ANIMATING
            val spotInCycle = (SystemClock.elapsedRealtime() - mAnimatingPeriodStart) % oneCycle
            val numCircles = spotInCycle / MILLIS_PER_CIRCLE_ANIMATING
            clearPatternDrawLookup()
            for (i in 0 until numCircles) {
                val dot: Dot = pattern[i.toInt()]
                drawLookupTable[dot.mRow][dot.mColumn] = true
            }
            val needToUpdateInProgressPoint = (numCircles in 1 until patternSize)
            if (needToUpdateInProgressPoint) {
                val percentageOfNextCircle: Float = (spotInCycle % MILLIS_PER_CIRCLE_ANIMATING).toFloat() / MILLIS_PER_CIRCLE_ANIMATING
                val currentDot: Dot = pattern[(numCircles - 1).toInt()]
                val centerX: Float = getCenterXForColumn(currentDot.mColumn)
                val centerY: Float = getCenterYForRow(currentDot.mRow)
                val nextDot: Dot = pattern[numCircles.toInt()]
                val dx: Float = (percentageOfNextCircle
                        * (getCenterXForColumn(nextDot.mColumn) - centerX))
                val dy: Float = (percentageOfNextCircle
                        * (getCenterYForRow(nextDot.mRow) - centerY))
                mInProgressX = centerX + dx
                mInProgressY = centerY + dy
            }
            invalidate()
        }
        val currentPath = mCurrentPath
        currentPath.rewind()

        // Draw the dots
        for (i in 0 until mDotCount) {
            val centerY = getCenterYForRow(i)
            for (j in 0 until mDotCount) {
                val dotState = mDotStates[i][j]
                dotState?.let {
                    val centerX = getCenterXForColumn(j)
                    val size = dotState.mSize * dotState.mScale
                    val translationY = dotState.mTranslateY
                    drawCircle(canvas, centerX, centerY.toInt() + translationY,
                            size, drawLookupTable[i][j], dotState.mAlpha)
                }
            }
        }

        // Draw the path of the pattern (unless we are in stealth mode)

        // Draw the path of the pattern (unless we are in stealth mode)
        val drawPath = !mInStealthMode
        if (drawPath) {
            mPathPaint.color = getCurrentColor(true)
            var anyCircles = false
            var lastX = 0f
            var lastY = 0f
            for (i in 0 until patternSize) {
                val dot: Dot = pattern[i]

                // Only draw the part of the pattern stored in
                // the lookup table (this is only different in case
                // of animation)
                if (!drawLookupTable[dot.mRow][dot.mColumn]) {
                    break
                }
                anyCircles = true
                val centerX = getCenterXForColumn(dot.mColumn)
                val centerY = getCenterYForRow(dot.mRow)
                if (i != 0) {
                    val state = mDotStates[dot.mRow][dot.mColumn]!!
                    currentPath.rewind()
                    currentPath.moveTo(lastX, lastY)
                    if (state.mLineEndX != Float.MIN_VALUE
                            && state.mLineEndY != Float.MIN_VALUE) {
                        currentPath.lineTo(state.mLineEndX, state.mLineEndY)
                    } else {
                        currentPath.lineTo(centerX, centerY)
                    }
                    canvas.drawPath(currentPath, mPathPaint)
                }
                lastX = centerX
                lastY = centerY
            }

            // Draw last in progress section
            if ((mPatternInProgress || mPatternViewMode == AUTO_DRAW)
                    && anyCircles) {
                currentPath.rewind()
                currentPath.moveTo(lastX, lastY)
                currentPath.lineTo(mInProgressX, mInProgressY)
                mPathPaint.alpha = (calculateLastSegmentAlpha(
                        mInProgressX, mInProgressY, lastX, lastY) * 255f).toInt()
                canvas.drawPath(currentPath, mPathPaint)
            }
        }

    }

    fun clearPattern() {
        mPattern.clear()
        clearPatternDrawLookup()
        invalidate()
    }

    private fun clearPatternDrawLookup() {
        for (i in 0 until mDotCount) {
            for (j in 0 until mDotCount) {
                mPatternDrawLookup[i][j] = false
            }
        }
    }

    private fun getCenterXForColumn(column: Int): Float {
        return paddingLeft + column * mViewWidth + mViewWidth / 2f
    }

    private fun getCenterYForRow(row: Int): Float {
        return paddingTop + row * mViewHeight + mViewHeight / 2f
    }


    private fun drawCircle(canvas: Canvas, centerX: Float, centerY: Float,
                           size: Float, partOfPattern: Boolean, alpha: Float) {
        mDotPaint.color = getCurrentColor(partOfPattern)
        mDotPaint.alpha = (alpha * 255).toInt()
        canvas.drawCircle(centerX, centerY, size / 2, mDotPaint)
    }

    private fun getCurrentColor(partOfPattern: Boolean): Int {
        return if (!partOfPattern || mInStealthMode || mPatternInProgress) {
            mNormalStateColor
        } else if (mPatternViewMode == WRONG) {
            mWrongStateColor
        } else if (mPatternViewMode == CORRECT
                || mPatternViewMode == AUTO_DRAW) {
            mCorrectStateColor
        } else {
            throw java.lang.IllegalStateException("Unknown view mode $mPatternViewMode")
        }
    }

    private fun calculateLastSegmentAlpha(x: Float, y: Float, lastX: Float,
                                          lastY: Float): Float {
        val diffX = x - lastX
        val diffY = y - lastY
        val dist = sqrt((diffX * diffX + diffY * diffY).toDouble()).toFloat()
        val fraction = dist / mViewWidth
        return 1f.coerceAtMost(0f.coerceAtLeast((fraction - 0.3f) * 4f))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val adjustedWidth = width - paddingLeft - paddingRight
        mViewWidth = adjustedWidth / mDotCount.toFloat()

        val adjustedHeight = height - paddingTop - paddingBottom
        mViewHeight = adjustedHeight / mDotCount.toFloat()
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(superState,
                PatternLockUtils.patternToString(this, mPattern),
                mPatternViewMode, mInputEnabled, mInStealthMode,
                mEnableHapticFeedback)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState: SavedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        setPattern(CORRECT,
                PatternLockUtils.stringToPattern(this, savedState.mSerializedPattern))
        mPatternViewMode = savedState.mDisplayMode
        mInputEnabled = savedState.mInputEnabled
        mInStealthMode = savedState.mInStealthMode
        mEnableHapticFeedback = savedState.mTactileFeedbackEnabled
    }

    override fun onHoverEvent(event: MotionEvent): Boolean {
        if ((context.getSystemService(
                        Context.ACCESSIBILITY_SERVICE) as AccessibilityManager).isTouchExplorationEnabled) {
            val action = event.action
            when (action) {
                MotionEvent.ACTION_HOVER_ENTER -> event.action = MotionEvent.ACTION_DOWN
                MotionEvent.ACTION_HOVER_MOVE -> event.action = MotionEvent.ACTION_MOVE
                MotionEvent.ACTION_HOVER_EXIT -> event.action = MotionEvent.ACTION_UP
            }
            onTouchEvent(event)
            event.action = action
        }
        return super.onHoverEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mInputEnabled || !isEnabled) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleActionDown(event)
                return true
            }
            MotionEvent.ACTION_UP -> {
                handleActionUp(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                handleActionMove(event)
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                mPatternInProgress = false
                resetPattern()
                notifyPatternCleared()
                if (PROFILE_DRAWING) {
                    if (mDrawingProfilingStarted) {
                        Debug.stopMethodTracing()
                        mDrawingProfilingStarted = false
                    }
                }
                return true
            }
        }
        return false
    }

    /**
     * Set the pattern explicitly rather than waiting for the user to input a
     * pattern. You can use this for help or demo purposes
     *
     * @param patternViewMode The mode in which the pattern should be displayed
     * @param pattern         The pattern
     */
    fun setPattern(@PatternViewMode patternViewMode: Int, pattern: List<Dot>) {
        mPattern.clear()
        mPattern.addAll(pattern)
        clearPatternDrawLookup()
        for (dot in pattern) {
            mPatternDrawLookup[dot.mRow][dot.mColumn] = true
        }
        setViewMode(patternViewMode)
    }

    /**
     * Set the display mode of the current pattern. This can be useful, for
     * instance, after detecting a pattern to tell this view whether change the
     * in progress result to correct or wrong.
     */
    fun setViewMode(@PatternViewMode patternViewMode: Int) {
        mPatternViewMode = patternViewMode
        if (patternViewMode == AUTO_DRAW) {
            check(mPattern.size != 0) {
                ("you must have a pattern to "
                        + "animate if you want to set the display mode to animate")
            }
            mAnimatingPeriodStart = SystemClock.elapsedRealtime()
            val first: Dot = mPattern[0]
            mInProgressX = getCenterXForColumn(first.mColumn)
            mInProgressY = getCenterYForRow(first.mRow)
            clearPatternDrawLookup()
        } else if (patternViewMode == WRONG) {
            mHandler.postDelayed({
                clearPattern()
            }, 500)
        } else if (patternViewMode == CORRECT) {
            mHandler.postDelayed({
                clearPattern()
            }, 200)
        }
        invalidate()
    }

    private fun handleActionDown(event: MotionEvent) {
        resetPattern()
        val x = event.x
        val y = event.y
        val hitDot: Dot? = detectAndAddHit(x, y)
        if (hitDot != null) {
            mPatternInProgress = true
            mPatternViewMode = CORRECT
            notifyPatternStarted()
        } else {
            mPatternInProgress = false
            notifyPatternCleared()
        }
        if (hitDot != null) {
            val startX = getCenterXForColumn(hitDot.mColumn)
            val startY = getCenterYForRow(hitDot.mRow)
            val widthOffset = mViewWidth / 2f
            val heightOffset = mViewHeight / 2f
            invalidate((startX - widthOffset).toInt(),
                    (startY - heightOffset).toInt(),
                    (startX + widthOffset).toInt(), (startY + heightOffset).toInt())
        }
        mInProgressX = x
        mInProgressY = y
        if (PROFILE_DRAWING) {
            if (!mDrawingProfilingStarted) {
                Debug.startMethodTracing("PatternLockDrawing")
                mDrawingProfilingStarted = true
            }
        }
    }

    /**
     * Determines whether the point x, y will add a new point to the current
     * pattern (in addition to finding the dot, also makes heuristic choices
     * such as filling in gaps based on current pattern).
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    private fun detectAndAddHit(x: Float, y: Float): Dot? {
        val dot: Dot? = checkForNewHit(x, y)
        if (dot != null) {
            // Check for gaps in existing pattern
            var fillInGapDot: Dot? = null
            val pattern: java.util.ArrayList<Dot> = mPattern
            if (pattern.isNotEmpty()) {
                val lastDot: Dot = pattern[pattern.size - 1]
                val dRow: Int = dot.mRow - lastDot.mRow
                val dColumn: Int = dot.mColumn - lastDot.mColumn
                var fillInRow: Int = lastDot.mRow
                var fillInColumn: Int = lastDot.mColumn
                if (Math.abs(dRow) == 2 && Math.abs(dColumn) != 1) {
                    fillInRow = lastDot.mRow + if (dRow > 0) 1 else -1
                }
                if (Math.abs(dColumn) == 2 && Math.abs(dRow) != 1) {
                    fillInColumn = lastDot.mColumn + if (dColumn > 0) 1 else -1
                }
                fillInGapDot = Dot.of(fillInRow, fillInColumn)
            }
            if (fillInGapDot != null
                    && !mPatternDrawLookup[fillInGapDot.mRow][fillInGapDot.mColumn]) {
                addCellToPattern(fillInGapDot)
            }
            addCellToPattern(dot)
            if (mEnableHapticFeedback) {
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
                        or HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
            }
            return dot
        }
        return null
    }

    /**
     * Helper method to map a given x, y to its corresponding cell
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return
     */
    private fun checkForNewHit(x: Float, y: Float): Dot? {
        val rowHit: Int = getRowHit(y)
        if (rowHit < 0) {
            return null
        }
        val columnHit: Int = getColumnHit(x)
        if (columnHit < 0) {
            return null
        }
        return if (mPatternDrawLookup[rowHit][columnHit]) {
            null
        } else Dot.of(rowHit, columnHit)
    }

    /**
     * Helper method to find the row that y coordinate falls into
     *
     * @param y The y coordinate
     * @return The mRow that y falls in, or -1 if it falls in no mRow
     */
    private fun getRowHit(y: Float): Int {
        val squareHeight = mViewHeight
        val hitSize: Float = squareHeight * mHitFactor
        val offset = paddingTop + (squareHeight - hitSize) / 2f
        for (i in 0 until mDotCount) {
            val hitTop = offset + squareHeight * i
            if (y >= hitTop && y <= hitTop + hitSize) {
                return i
            }
        }
        return -1
    }

    /**
     * Helper method to find the column x falls into
     *
     * @param x The x coordinate
     * @return The mColumn that x falls in, or -1 if it falls in no mColumn
     */
    private fun getColumnHit(x: Float): Int {
        val squareWidth = mViewWidth
        val hitSize: Float = squareWidth * mHitFactor
        val offset = paddingLeft + (squareWidth - hitSize) / 2f
        for (i in 0 until mDotCount) {
            val hitLeft = offset + squareWidth * i
            if (x >= hitLeft && x <= hitLeft + hitSize) {
                return i
            }
        }
        return -1
    }


    private fun resetPattern() {
        mPattern.clear()
        clearPatternDrawLookup()
        mPatternViewMode = CORRECT
        invalidate()
    }

    private fun handleActionUp(event: MotionEvent) {
        // Report pattern detected
        if (mPattern.isNotEmpty()) {
            mPatternInProgress = false
            cancelLineAnimations()
            notifyPatternDetected()
            invalidate()
        }
        if (PROFILE_DRAWING) {
            if (mDrawingProfilingStarted) {
                Debug.stopMethodTracing()
                mDrawingProfilingStarted = false
            }
        }
    }

    private fun cancelLineAnimations() {
        for (i in 0 until mDotCount) {
            for (j in 0 until mDotCount) {
                val state = mDotStates[i][j]!!
                if (state.mLineAnimator != null) {
                    state.mLineAnimator!!.cancel()
                    state.mLineEndX = Float.MIN_VALUE
                    state.mLineEndY = Float.MIN_VALUE
                }
            }
        }
    }

    private fun notifyPatternDetected() {
        sendAccessEvent(R.string.message_pattern_detected)
        notifyListenersComplete(mPattern)
    }

    private fun notifyPatternCleared() {
        sendAccessEvent(R.string.message_pattern_cleared)
        notifyListenersCleared()
    }

    private fun addCellToPattern(newDot: Dot) {
        mPatternDrawLookup[newDot.mRow][newDot.mColumn] = true
        mPattern.add(newDot)
        if (!mInStealthMode) {
            startDotSelectedAnimation(newDot)
        }
        notifyPatternProgress()
    }

    private fun startDotSelectedAnimation(dot: Dot) {
        val dotState = mDotStates[dot.mRow][dot.mColumn]!!
        startSizeAnimation(mDotNormalSize.toFloat(), mDotSelectedSize.toFloat(), mDotAnimationDuration.toLong(),
                mLinearOutSlowInInterpolator, dotState, {
            startSizeAnimation(mDotSelectedSize.toFloat(), mDotNormalSize.toFloat(), mDotAnimationDuration.toLong(),
                    mFastOutSlowInInterpolator, dotState, null)
        })
        startLineEndAnimation(dotState, mInProgressX, mInProgressY,
                getCenterXForColumn(dot.mColumn), getCenterYForRow(dot.mRow))
    }

    private fun startLineEndAnimation(state: DotState,
                                      startX: Float, startY: Float, targetX: Float,
                                      targetY: Float) {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener { animation ->
            val t = animation.animatedValue as Float
            state.mLineEndX = (1 - t) * startX + t * targetX
            state.mLineEndY = (1 - t) * startY + t * targetY
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                state.mLineAnimator = null
            }
        })
        valueAnimator.interpolator = mFastOutSlowInInterpolator
        valueAnimator.duration = mPathEndAnimationDuration.toLong()
        valueAnimator.start()
        state.mLineAnimator = valueAnimator
    }

    private fun startSizeAnimation(start: Float, end: Float, duration: Long,
                                   interpolator: Interpolator, state: DotState,
                                   endRunnable: Runnable?) {
        val valueAnimator = ValueAnimator.ofFloat(start, end)
        valueAnimator.addUpdateListener { animation ->
            state.mSize = (animation.animatedValue as Float)
            invalidate()
        }
        if (endRunnable != null) {
            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    endRunnable.run()
                }
            })
        }
        valueAnimator.interpolator = interpolator
        valueAnimator.duration = duration
        valueAnimator.start()
    }

    private fun notifyPatternProgress() {
        sendAccessEvent(R.string.message_pattern_dot_added)
        notifyListenersProgress(mPattern)
    }

    private fun notifyPatternStarted() {
        sendAccessEvent(R.string.message_pattern_started)
        notifyListenersStarted()
    }


    private fun sendAccessEvent(resId: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            contentDescription = context.getString(resId)
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
            contentDescription = null
        } else {
            announceForAccessibility(context.getString(resId))
        }
    }

    private fun notifyListenersProgress(pattern: List<Dot>) {
        for (patternListener in mPatternListeners) {
            patternListener.onProgress(pattern)
        }
    }

    private fun notifyListenersStarted() {
        for (patternListener in mPatternListeners) {
            patternListener.onStarted()
        }
    }

    private fun notifyListenersComplete(pattern: List<Dot>) {
        for (patternListener in mPatternListeners) {
            patternListener.onComplete(pattern)
        }
    }

    private fun notifyListenersCleared() {
        for (patternListener in mPatternListeners) {
            patternListener.onCleared()
        }
    }

    private fun handleActionMove(event: MotionEvent) {
        val radius = mPathWidth.toFloat()
        val historySize = event.historySize
        mTempInvalidateRect.setEmpty()
        var invalidateNow = false
        for (i in 0 until historySize + 1) {
            val x = if (i < historySize) event.getHistoricalX(i) else event
                    .x
            val y = if (i < historySize) event.getHistoricalY(i) else event
                    .y
            val hitDot: Dot? = detectAndAddHit(x, y)
            val patternSize = mPattern.size
            if (hitDot != null && patternSize == 1) {
                mPatternInProgress = true
                notifyPatternStarted()
            }
            // Note current x and y for rubber banding of in progress patterns
            val dx = Math.abs(x - mInProgressX)
            val dy = Math.abs(y - mInProgressY)
            if (dx > DEFAULT_DRAG_THRESHOLD || dy > DEFAULT_DRAG_THRESHOLD) {
                invalidateNow = true
            }
            if (mPatternInProgress && patternSize > 0) {
                val pattern: java.util.ArrayList<Dot> = mPattern
                val lastDot: Dot = pattern[patternSize - 1]
                val lastCellCenterX = getCenterXForColumn(lastDot.mColumn)
                val lastCellCenterY = getCenterYForRow(lastDot.mRow)

                // Adjust for drawn segment from last cell to (x,y). Radius
                // accounts for line width.
                var left = Math.min(lastCellCenterX, x) - radius
                var right = Math.max(lastCellCenterX, x) + radius
                var top = Math.min(lastCellCenterY, y) - radius
                var bottom = Math.max(lastCellCenterY, y) + radius

                // Invalidate between the pattern's new cell and the pattern's
                // previous cell
                if (hitDot != null) {
                    val width = mViewWidth * 0.5f
                    val height = mViewHeight * 0.5f
                    val hitCellCenterX = getCenterXForColumn(hitDot.mColumn)
                    val hitCellCenterY = getCenterYForRow(hitDot.mRow)
                    left = Math.min(hitCellCenterX - width, left)
                    right = Math.max(hitCellCenterX + width, right)
                    top = Math.min(hitCellCenterY - height, top)
                    bottom = Math.max(hitCellCenterY + height, bottom)
                }

                // Invalidate between the pattern's last cell and the previous
                // location
                mTempInvalidateRect.union(Math.round(left), Math.round(top),
                        Math.round(right), Math.round(bottom))
            }
        }
        mInProgressX = event.x
        mInProgressY = event.y

        // To save updates, we only invalidate if the user moved beyond a
        // certain amount.
        if (invalidateNow) {
            mInvalidate.union(mTempInvalidateRect)
            invalidate(mInvalidate)
            mInvalidate.set(mTempInvalidateRect)
        }
    }

    class Dot : Parcelable {
        var mRow = 0
        var mColumn = 0
        var mCount = 0


        constructor(parcel: Parcel) {
            mRow = parcel.readInt()
            mColumn = parcel.readInt()
            mDotCount = parcel.readInt()
            mCount = mRow * 3 + mColumn + 1
        }

        constructor(row: Int, column: Int, count: Int) {
            this.mRow = row
            this.mColumn = column
            mDotCount = count
            mCount = mRow * 3 + mColumn + 1
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            dest?.writeInt(mRow)
            dest?.writeInt(mColumn)
        }

        override fun hashCode(): Int {
            return 31 * mRow + mColumn
        }

        override fun equals(other: Any?): Boolean {
            if (other is Dot) {
                return mColumn == other.mColumn && mRow == other.mRow
            }
            return super.equals(other)
        }

        companion object CREATOR : Parcelable.Creator<Dot> {

            var mDotCount = DEFAULT_PATTERN_DOT_COUNT

            override fun createFromParcel(parcel: Parcel): Dot {
                return Dot(parcel)
            }

            override fun newArray(size: Int): Array<Dot?> {
                return arrayOfNulls(size)
            }

            private fun checkRange(row: Int, column: Int) {
                require(!(row < 0 || row > mDotCount - 1)) {
                    ("mRow must be in range 0-"
                            + (mDotCount - 1))
                }
                require(!(column < 0 || column > mDotCount - 1)) {
                    ("mColumn must be in range 0-"
                            + (mDotCount - 1))
                }
            }

            /**
             * @param row    The mRow of the cell.
             * @param column The mColumn of the cell.
             */
            @Synchronized
            fun of(row: Int, column: Int): Dot {
                checkRange(row, column)
                return Dot(row, column, mDotCount)
            }

            /**
             * Gets a cell from its identifier
             */
            @Synchronized
            fun of(id: Int): Dot {
                return of(id / mDotCount, id % mDotCount)
            }
        }
    }

    class SavedState : BaseSavedState {
        var mSerializedPattern: String
        var mDisplayMode by Delegates.notNull<Int>()
        var mInputEnabled: Boolean = false
        var mInStealthMode: Boolean = false
        var mTactileFeedbackEnabled: Boolean = false

        constructor(superState: Parcelable?, serializedPattern: String,
                    displayMode: Int, inputEnabled: Boolean, inStealthMode: Boolean,
                    tactileFeedbackEnabled: Boolean) : super(superState) {
            this.mSerializedPattern = serializedPattern
            this.mDisplayMode = displayMode
            this.mInStealthMode = inStealthMode
            this.mInputEnabled = inputEnabled
            this.mTactileFeedbackEnabled = tactileFeedbackEnabled
        }

        constructor(parcel: Parcel) : super(parcel) {
            this.mSerializedPattern = parcel.readString() ?: ""
            this.mDisplayMode = parcel.readInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this.mInStealthMode = parcel.readBoolean()
                this.mInputEnabled = parcel.readBoolean()
                this.mTactileFeedbackEnabled = parcel.readBoolean()
            } else {
                this.mInStealthMode = parcel.readByte() == 1.toByte()
                this.mInputEnabled = parcel.readByte() == 1.toByte()
                this.mTactileFeedbackEnabled = parcel.readByte() == 1.toByte()
            }
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(mSerializedPattern)
            out.writeInt(mDisplayMode)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                out.writeBoolean(mInStealthMode)
                out.writeBoolean(mInputEnabled)
                out.writeBoolean(mTactileFeedbackEnabled)
            } else {
                out.writeByte(if (mInStealthMode) 1 else 0)
                out.writeByte(if (mInputEnabled) 1 else 0)
                out.writeByte(if (mTactileFeedbackEnabled) 1 else 0)
            }

        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel): SavedState {
                return SavedState(source)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls<SavedState>(size)
            }

        }
    }

    class DotState {
        var mScale = 1.0f
        var mTranslateY = 0.0f
        var mAlpha = 1.0f
        var mSize = 0f
        var mLineEndX = Float.MIN_VALUE
        var mLineEndY = Float.MIN_VALUE
        var mLineAnimator: ValueAnimator? = null
    }

    fun addPatternLockListener(patternListener: PatternLockViewListener) {
        mPatternListeners.add(patternListener)
    }
}