package com.datangic.zxing.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.datangic.zxing.R
import com.google.android.material.internal.ThemeEnforcement
import kotlin.properties.Delegates

@SuppressLint("RestrictedApi", "ResourceType")
class SurFinderView(context: Context, attrs: AttributeSet?, defStyleRes: Int) : View(context, attrs, defStyleRes) {
    companion object {
        private const val CURRENT_POINT_OPACITY = 0xA0
        private const val MAX_RESULT_POINTS = 20
        private const val POINT_SIZE = 30
    }

    private val TAG = SurFinderView::class.simpleName


    //   扫码框外这招颜色
    private var maskColor by Delegates.notNull<Int>()

    //    扫描区边框颜色
    private var frameColor by Delegates.notNull<Int>()

    //  扫描线颜色
    private var laserColor by Delegates.notNull<Int>()

    //    扫码框四角颜色
    private var cornerColor by Delegates.notNull<Int>()

    private var labelText: String? = null
    private var labelTextColor by Delegates.notNull<Int>()
    private var labelTextSize: Float
    private var labelTextPadding: Float
    private var labelTextLocation: TextLocation

    /**
     * 扫码框宽
     */
    private var frameWidth: Int = 0

    /**
     * 扫码框高
     */
    private var frameHeight: Int

    /**
     * 扫描激光线风格
     */
    private var laserStyle: LaserStyle

    /**
     * 网格列数
     */
    private var gridColumn: Int

    var frame: Rect? = null

    /**
     * 扫描线开始位置
     */
    var scannerStart = 0

    /**
     * 扫描线结束位置
     */
    var scannerEnd = 0

    /**
     * 扫描区边角的宽
     */
    private var cornerRectWidth: Int

    /**
     * 扫描区边角的高
     */
    private var cornerRectHeight: Int

    /**
     * 扫描线每次移动距离
     */
    private var scannerLineMoveDistance: Int

    /**
     * 扫描线高度
     */
    private var scannerLineHeight: Int

    /**
     * 边框线宽度
     */
    private var frameLineWidth: Int

    /**
     * 扫描线延时
     */
    private var scannerAnimationDelay: Int

    /**
     * 扫码框占比
     */
    private var frameRatio: Float = 0.0f

    /**
     * 扫码框内间距
     */
    private var framePaddingLeft: Float
    private var framePaddingTop: Float
    private var framePaddingRight: Float
    private var framePaddingBottom: Float

    /**
     * 扫码框对齐方式
     */
    private lateinit var frameGravity: FrameGravity

    /**
     * 网格高度
     */
    private var gridHeight: Int


    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {

        val style: TypedArray = ThemeEnforcement.obtainStyledAttributes(context, attrs, R.styleable.SurFinderView, defStyleRes, R.style.Widget_SurFindView)

        maskColor = style.getColor(R.styleable.SurFinderView_colorMask, getColor(R.color.color_mask))
        frameColor = style.getColor(R.styleable.SurFinderView_colorPrimary, getColor(R.color.color_primary))
        laserColor = style.getColor(R.styleable.SurFinderView_colorLaser, getColor(R.color.color_primary))
        cornerColor = style.getColor(R.styleable.SurFinderView_colorLaser, getColor(R.color.color_primary))
        labelText = style.getString(R.styleable.SurFinderView_labelText)
        labelTextColor = style.getColor(R.styleable.SurFinderView_labelTextColor, getColor(R.color.color_text))
        labelTextSize = style.getDimension(R.styleable.SurFinderView_labelTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics))
        labelTextPadding = style.getDimension(R.styleable.SurFinderView_labelTextPadding, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics))
        labelTextLocation = TextLocation.getFromInt(style.getInt(R.styleable.SurFinderView_labelTextLocation, 0))
        style.getDimensionPixelSize(R.styleable.SurFinderView_frameWith, 0)
        frameHeight = style.getDimensionPixelSize(R.styleable.SurFinderView_frameHeight, 0)
        laserStyle = LaserStyle.getFromInt(style.getInt(R.styleable.SurFinderView_laserStyle, LaserStyle.LINE.mValue))
        gridColumn = style.getInt(R.styleable.SurFinderView_gridColumn, 20)
        cornerRectWidth = style.getDimension(R.styleable.SurFinderView_cornerRectWidth, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)).toInt()
        cornerRectHeight = style.getDimension(R.styleable.SurFinderView_cornerRectHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)).toInt()
        scannerLineMoveDistance = style.getDimension(R.styleable.SurFinderView_scannerLineMoveDistance, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics)).toInt()
        scannerLineHeight = style.getDimension(R.styleable.SurFinderView_scannerLineHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)).toInt()
        frameLineWidth = style.getDimension(R.styleable.SurFinderView_frameLineWidth, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)).toInt()
        scannerAnimationDelay = style.getInteger(R.styleable.SurFinderView_scannerAnimationDelay, 20)
        frameRatio = style.getFloat(R.styleable.SurFinderView_frameRatio, 0.625f)

        framePaddingLeft = style.getDimension(R.styleable.SurFinderView_framePaddingLeft, 0f)
        framePaddingTop = style.getDimension(R.styleable.SurFinderView_framePaddingTop, 0f)
        framePaddingRight = style.getDimension(R.styleable.SurFinderView_framePaddingRight, 0f)
        framePaddingBottom = style.getDimension(R.styleable.SurFinderView_framePaddingBottom, 0f)
        gridHeight = style.getDimension(R.styleable.SurFinderView_gridHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics)).toInt()
        frameGravity = FrameGravity.getFromInt(style.getInt(R.styleable.SurFinderView_frameGravity, FrameGravity.CENTER.mValue))
        style.recycle()
    }

    private fun getColor(@ColorRes color: Int): Int {
        return ContextCompat.getColor(context, color)
    }

    enum class TextLocation(private val mValue: Int) {
        TOP(0), BOTTOM(1);

        companion object {
            fun getFromInt(value: Int): TextLocation {
                for (location in values()) {
                    if (location.mValue == value) {
                        return location
                    }
                }
                return TOP
            }
        }
    }

    enum class LaserStyle(val mValue: Int) {
        NONE(0), LINE(1), GRID(2);

        companion object {
            fun getFromInt(value: Int): LaserStyle {
                for (style in values()) {
                    if (style.mValue == value) {
                        return style
                    }
                }
                return LINE
            }
        }
    }

    enum class FrameGravity(val mValue: Int) {
        CENTER(0), LEFT(1), TOP(2), RIGHT(3), BOTTOM(4);

        companion object {
            fun getFromInt(value: Int): FrameGravity {
                for (gravity in values()) {
                    if (gravity.mValue == value) {
                        return gravity
                    }
                }
                return CENTER
            }
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = (w.coerceAtMost(h) * frameRatio).toInt()
        if (frameWidth <= 0 || frameWidth > w) {
            frameWidth = size
        }
        if (frameHeight <= 0 || frameHeight > h) {
            frameHeight = size
        }
        var leftOffsets = (w - frameWidth) / 2 + framePaddingLeft - framePaddingRight
        var topOffsets = (h - frameHeight) / 2 + framePaddingTop - framePaddingBottom
        when (frameGravity) {
            FrameGravity.LEFT -> leftOffsets = framePaddingLeft
            FrameGravity.TOP -> topOffsets = framePaddingTop
            FrameGravity.RIGHT -> leftOffsets = w - frameWidth + framePaddingRight
            FrameGravity.BOTTOM -> topOffsets = h - frameHeight + framePaddingBottom
            FrameGravity.CENTER -> {

            }
        }

        frame = Rect(leftOffsets.toInt(), topOffsets.toInt(), leftOffsets.toInt() + frameWidth, topOffsets.toInt() + frameHeight)
    }

    override fun onDraw(canvas: Canvas) {
        frame?.let { _frame ->
            if (scannerStart == 0 || scannerEnd == 0) {
                scannerStart = _frame.top
                scannerEnd = _frame.bottom - scannerLineHeight
            }
            drawExterior(canvas, _frame, width, height)

            drawLaserScanner(canvas, _frame)

            drawFrame(canvas, _frame)

            drawCorner(canvas, _frame)

            drawTextInfo(canvas, _frame)

            postInvalidateDelayed(scannerAnimationDelay.toLong(),
                    _frame.left - POINT_SIZE,
                    _frame.top - POINT_SIZE,
                    _frame.right + POINT_SIZE,
                    _frame.bottom + POINT_SIZE)
        }
    }

    /**
     * 绘制模糊区域
     * @param canvas
     * @param frame
     * @param width
     * @param height
     */
    private fun drawExterior(canvas: Canvas, frame: Rect, width: Int, height: Int) {
        paint.color = maskColor
        canvas.drawRect(0f, 0f, width.toFloat(), frame.top.toFloat(), paint)
        canvas.drawRect(0f, frame.top.toFloat(), frame.left.toFloat(), frame.bottom.toFloat(), paint)
        canvas.drawRect(frame.right.toFloat(), frame.top.toFloat(), width.toFloat(), frame.bottom.toFloat(), paint)
        canvas.drawRect(0f, frame.bottom.toFloat(), width.toFloat(), height.toFloat(), paint)

    }

    /**
     * 绘制激光扫描线
     * @param canvas
     * @param frame
     */
    private fun drawLaserScanner(canvas: Canvas, frame: Rect) {
        paint.color = laserColor
        when (laserStyle) {
            LaserStyle.LINE -> drawLineScanner(canvas, frame)
            LaserStyle.GRID -> drawGridScanner(canvas, frame)
            else -> {
                return
            }
        }
        paint.shader = null

    }


    /**
     * 绘制线性式扫描
     * @param canvas
     * @param frame
     */
    private fun drawLineScanner(canvas: Canvas, frame: Rect) {
        //线性渐变
        val linearGradient: LinearGradient = LinearGradient(
                frame.left.toFloat(),
                scannerStart.toFloat(),
                frame.left.toFloat(),
                (scannerStart + scannerLineHeight).toFloat(),
                shadeColor(laserColor),
                laserColor,
                Shader.TileMode.MIRROR)
        paint.shader = linearGradient
        if (scannerStart <= scannerEnd) {
            //椭圆
            val rectF = RectF((frame.left + 2 * scannerLineHeight).toFloat(), scannerStart.toFloat(), (frame.right - 2 * scannerLineHeight).toFloat(), (scannerStart + scannerLineHeight).toFloat())
            canvas.drawOval(rectF, paint)
            scannerStart += scannerLineMoveDistance
        } else {
            scannerStart = frame.top
        }
    }

    /**
     * 处理颜色模糊
     * @param color
     * @return
     */
    private fun shadeColor(color: Int): Int {
        val hax = Integer.toHexString(color)
        val result = "01" + hax.substring(2)
        return Integer.valueOf(result, 16)
    }

    /**
     * 绘制网格式扫描
     * @param canvas
     * @param frame
     */
    private fun drawGridScanner(canvas: Canvas, frame: Rect) {
        val stroke = 2
        paint.strokeWidth = stroke.toFloat()
        //计算Y轴开始位置
        val startY = if (gridHeight > 0 && scannerStart - frame.top > gridHeight) scannerStart - gridHeight else frame.top
        val linearGradient = LinearGradient(
                (frame.left + frame.width() / 2).toFloat(),
                startY.toFloat(),
                (frame.left + frame.width() / 2).toFloat(),
                scannerStart.toFloat(),
                intArrayOf(shadeColor(laserColor), laserColor),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP)
        //给画笔设置着色器
        paint.shader = linearGradient
        val wUnit = frame.width() * 1.0f / gridColumn
        //遍历绘制网格纵线
        for (i in 1 until gridColumn) {
            canvas.drawLine(frame.left + i * wUnit, startY.toFloat(), frame.left + i * wUnit, scannerStart.toFloat(), paint)
        }
        val height = if (gridHeight > 0 && scannerStart - frame.top > gridHeight) gridHeight else scannerStart - frame.top

        //遍历绘制网格横线
        var i = 0
        while (i <= height / wUnit) {
            canvas.drawLine(frame.left.toFloat(), scannerStart - i * wUnit, frame.right.toFloat(), scannerStart - i * wUnit, paint)
            i++
        }
        if (scannerStart < scannerEnd) {
            scannerStart += scannerLineMoveDistance
        } else {
            scannerStart = frame.top
        }
    }

    /**
     * 绘制扫描区边框
     * @param canvas
     * @param frame
     */
    private fun drawFrame(canvas: Canvas, frame: Rect) {
        paint.color = frameColor
        canvas.drawRect(frame.left.toFloat(), frame.top.toFloat(), frame.right.toFloat(), (frame.top + frameLineWidth).toFloat(), paint)
        canvas.drawRect(frame.left.toFloat(), frame.top.toFloat(), (frame.left + frameLineWidth).toFloat(), frame.bottom.toFloat(), paint)
        canvas.drawRect((frame.right - frameLineWidth).toFloat(), frame.top.toFloat(), frame.right.toFloat(), frame.bottom.toFloat(), paint)
        canvas.drawRect(frame.left.toFloat(), (frame.bottom - frameLineWidth).toFloat(), frame.right.toFloat(), frame.bottom.toFloat(), paint)
    }


    /**
     * 绘制边角
     * @param canvas
     * @param frame
     */
    private fun drawCorner(canvas: Canvas, frame: Rect) {
        paint.color = cornerColor
        //左上
        canvas.drawRect(frame.left.toFloat(), frame.top.toFloat(), (frame.left + cornerRectWidth).toFloat(), (frame.top + cornerRectHeight).toFloat(), paint)
        canvas.drawRect(frame.left.toFloat(), frame.top.toFloat(), (frame.left + cornerRectHeight).toFloat(), (frame.top + cornerRectWidth).toFloat(), paint)
        //右上
        canvas.drawRect((frame.right - cornerRectWidth).toFloat(), frame.top.toFloat(), frame.right.toFloat(), (frame.top + cornerRectHeight).toFloat(), paint)
        canvas.drawRect((frame.right - cornerRectHeight).toFloat(), frame.top.toFloat(), frame.right.toFloat(), (frame.top + cornerRectWidth).toFloat(), paint)
        //左下
        canvas.drawRect(frame.left.toFloat(), (frame.bottom - cornerRectWidth).toFloat(), (frame.left + cornerRectHeight).toFloat(), frame.bottom.toFloat(), paint)
        canvas.drawRect(frame.left.toFloat(), (frame.bottom - cornerRectHeight).toFloat(), (frame.left + cornerRectWidth).toFloat(), frame.bottom.toFloat(), paint)
        //右下
        canvas.drawRect((frame.right - cornerRectWidth).toFloat(), (frame.bottom - cornerRectHeight).toFloat(), frame.right.toFloat(), frame.bottom.toFloat(), paint)
        canvas.drawRect((frame.right - cornerRectHeight).toFloat(), (frame.bottom - cornerRectWidth).toFloat(), frame.right.toFloat(), frame.bottom.toFloat(), paint)
    }

    /**
     * 绘制文本
     * @param canvas
     * @param frame
     */
    private fun drawTextInfo(canvas: Canvas, frame: Rect) {
        if (labelText?.isNotEmpty() == true) {
            textPaint.color = labelTextColor
            textPaint.textSize = labelTextSize
            textPaint.textAlign = Paint.Align.CENTER
            val staticLayout = StaticLayout(labelText, textPaint, canvas.width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true)
            if (labelTextLocation == TextLocation.BOTTOM) {
                canvas.translate((frame.left + frame.width() / 2).toFloat(), frame.bottom + labelTextPadding)
            } else {
                canvas.translate((frame.left + frame.width() / 2).toFloat(), frame.top - labelTextPadding - staticLayout.height)
            }
            staticLayout.draw(canvas)
        }
    }
}