package com.datangic.localLock.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import com.datangic.localLock.PasswordLockViewListener
import com.datangic.localLock.R
import com.datangic.localLock.utils.SystemUtils
import com.google.android.material.internal.ThemeEnforcement


@SuppressLint("CustomViewStyleable", "Recycle", "ResourceType")
class NumericKeyboardView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int = 0) : View(context, attributeSet, defStyleAttr) {

    private var firstX = 0F
    private val xs = FloatArray(3) //声明数组保存每一列的圆心横坐标
    private val ys = FloatArray(4) //声明数组保存每一排的圆心纵坐标
    private var circle_x = 0f
    private var circle_y //点击处的圆心坐标
            = 0f
    private var offset_x = 0f
    private var offset_y = 0f //偏移大小
    private var number = -1 //点击的数字
    private var radius = 0f //半径
    private var strokeWidth = 1F //
    private var click_radius = 0f
    private var size = 0f //字体大小
    private var clickColor = Color.WHITE
    var mShader: Shader? = null
    var mPaint: Paint = Paint()
    var tPaint = Paint()
    var cPaint = Paint()
    var nPaint = Paint()
    private var passwordSize: Int = 4
    var interval = 0F

    private var password: MutableList<Int> = ArrayList()

    private val mPasswordListeners: MutableList<PasswordLockViewListener> = ArrayList()

    /*
     * 判断刷新数据
     * -1 不进行数据刷新
     * 0  按下刷新
     * 1  弹起刷新
     */
    private var type = -1

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)


    fun addPasswordLockListener(passwordListener: PasswordLockViewListener) {
        mPasswordListeners.add(passwordListener)
    }

    // 初始化数据
    init {
        val typedArray = ThemeEnforcement.obtainStyledAttributes(context, attributeSet, R.styleable.PasswordLockView, defStyleAttr, R.style.defaultPasswordLockView)
        // 获取屏幕的宽度
        val screenWidth: Int = SystemUtils.getSystemDisplay(context)[0]
        // 获取绘制1的x坐标
        // 绘制1的x坐标
        firstX = screenWidth.toFloat() / 11
        // 获取绘制1的y坐标
        // 绘制1的y坐标
        val firstY = (SystemUtils.getSystemDisplay(context)[1] - SystemUtils.getSystemDisplay(context)[1] / 3).toFloat() / 5.toFloat()
        layoutParams = ViewGroup.LayoutParams((firstX * 11).toInt(), (firstX * 12).toInt())
        radius = typedArray.getDimension(R.styleable.PasswordLockView_radius, resources.getDimension(R.dimen.size_30dp))

        size = typedArray.getDimension(R.styleable.PasswordLockView_textSize, resources.getDimension(R.dimen.size_30dp))
        strokeWidth = typedArray.getFloat(R.styleable.PasswordLockView_frameWidth, 2F)

        val startColor = typedArray.getColor(R.styleable.PasswordLockView_startColor, android.R.attr.colorPrimary)
        val endColor = typedArray.getColor(R.styleable.PasswordLockView_endColor, android.R.attr.colorAccent)
        passwordSize = typedArray.getInt(R.styleable.PasswordLockView_passwordSize, 4)

        clickColor = typedArray.getColor(R.styleable.PasswordLockView_clickColor, Color.rgb(105, 174, 254))

        click_radius = firstX * 1.5.toFloat()
        offset_x = -size * 30 / 100
        offset_y = size * 35 / 100
        //添加每一排的横坐标
        xs[0] = firstX * 2.5F
        xs[1] = firstX * 5.5F
        xs[2] = firstX * 8.5F
        interval = (firstX * 5F) / (passwordSize - 1)
        //添加每一列的纵坐标
        ys[0] = firstY
        ys[1] = firstY + firstX * 3
        ys[2] = firstY + firstX * 6
        ys[3] = firstY + firstX * 9
        mShader = LinearGradient(
                firstX,
                firstY,
                firstX * 11,
                firstY + firstX * 9,
                startColor,
                endColor,
                Shader.TileMode.CLAMP)
        cPaint.shader = mShader

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        nPaint.shader = mShader
        nPaint.strokeWidth = this.strokeWidth
        nPaint.isAntiAlias = true //设置抗锯齿
        nPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        for (i in 0 until passwordSize) {
            nPaint.style = if (i >= password.size) Paint.Style.STROKE else Paint.Style.FILL
            canvas.drawCircle(xs[0] + i * interval + xs[0] / 5F, ys[0] - (ys[1] - ys[0]) * .7F, 16F, nPaint)
        }

        // 创建画笔对象
        // 绘制文本,注意是从坐标开始往上绘制
        mPaint = Paint()
        mPaint.shader = mShader
        mPaint.textSize = size // 设置字体大小
        mPaint.strokeWidth = this.strokeWidth
        mPaint.isAntiAlias = true //设置抗锯齿
        mPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        // 这里较难的就是算坐标
        // 绘制第一排1,2,3
        canvas.drawText("1", xs[0] + offset_x, ys[0] + offset_y, mPaint)
        canvas.drawText("2", xs[1] + offset_x, ys[0] + offset_y, mPaint)
        canvas.drawText("3", xs[2] + offset_x, ys[0] + offset_y, mPaint)
        // 绘制第2排4,5,6
        canvas.drawText("4", xs[0] + offset_x, ys[1] + offset_y, mPaint)
        canvas.drawText("5", xs[1] + offset_x, ys[1] + offset_y, mPaint)
        canvas.drawText("6", xs[2] + offset_x, ys[1] + offset_y, mPaint)
        // 绘制第3排7,8,9
        canvas.drawText("7", xs[0] + offset_x, ys[2] + offset_y, mPaint)
        canvas.drawText("8", xs[1] + offset_x, ys[2] + offset_y, mPaint)
        canvas.drawText("9", xs[2] + offset_x, ys[2] + offset_y, mPaint)
        // 绘制第4排0
        canvas.drawText("0", xs[1] + offset_x, ys[3] + offset_y, mPaint)
        //为每一个数字绘制一个圆
        //设置绘制空心圆
//        mPaint.setShader(mShader);
        mPaint.style = Paint.Style.STROKE
        //依次绘制第一排的圆
        canvas.drawCircle(xs[0], ys[0], radius, mPaint)
        canvas.drawCircle(xs[1], ys[0], radius, mPaint)
        canvas.drawCircle(xs[2], ys[0], radius, mPaint)
        //依次绘制第2排的圆
        canvas.drawCircle(xs[0], ys[1], radius, mPaint)
        canvas.drawCircle(xs[1], ys[1], radius, mPaint)
        canvas.drawCircle(xs[2], ys[1], radius, mPaint)
        //依次绘制第3排的圆
        canvas.drawCircle(xs[0], ys[2], radius, mPaint)
        canvas.drawCircle(xs[1], ys[2], radius, mPaint)
        canvas.drawCircle(xs[2], ys[2], radius, mPaint)
        //绘制最后一个圆
        canvas.drawCircle(xs[1], ys[3], radius, mPaint)

        //判断是否点击数字(点击数字产生的渐变效果)
        tPaint.textSize = size // 设置字体大小
        tPaint.strokeWidth = this.strokeWidth
        tPaint.isAntiAlias = true //设置抗锯齿
        tPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        tPaint.color = clickColor
        if (circle_x > 0 && circle_y > 0) {
            if (type == 0) { //按下刷新
                cPaint.style = Paint.Style.FILL_AND_STROKE //按下的时候绘制实心圆
                canvas.drawCircle(circle_x, circle_y, radius, cPaint) //绘制圆
                canvas.drawText(number.toString(), circle_x + offset_x, circle_y + offset_y, tPaint)
            } else if (type == 1) { //弹起刷新
                mPaint.color = Color.WHITE //设置画笔颜色
                mPaint.style = Paint.Style.STROKE //弹起的时候再绘制空心圆
                canvas.drawCircle(circle_x, circle_y, radius, mPaint) //绘制圆
                //绘制完成后,重置
                circle_x = 0f
                circle_y = 0f
            }
        }
    }

    /**
     * 获取触摸点击事件
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //事件判断
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //判断点击的坐标位置
                val x = event.x //按下时的X坐标
                val y = event.y //按下时的Y坐标
                //判断点击的是哪一个数字圆
                handleDown(x, y)
                //                tShader = new LinearGradient(
//                        circle_x - radius,
//                        circle_y - radius,
//                        circle_x + radius,
//                        circle_y + radius,
//                        Color.rgb(194, 164, 255),
//                        Color.rgb(105, 174, 254),
//                        Shader.TileMode.MIRROR);
                return true
            }
            MotionEvent.ACTION_UP -> {
                type = 1 //弹起刷新
                invalidate() //刷新界面
                //返回点击的数字
                if (number != -1) {
                    notifyNumberPassed(number)
                }
                setDefault() //恢复默认
                //发送辅助事件
                sendAccessEvent(R.string.numeric_keyboard_up)
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                //恢复默认值
                setDefault()
                return true
            }
        }
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)

        setMeasuredDimension(width, height)
    }


    private fun measureWidth(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        //wrap_content
        if (specMode == MeasureSpec.AT_MOST) {

            return (SystemUtils.getSystemDisplay(context)[0] * 1.3).toInt()
        }
        //fill_parent或者精确值
        else if (specMode == MeasureSpec.EXACTLY) {

            return specSize
        }
        return (firstX * 11).toInt()
    }

    //根据xml的设定获取高度
    private fun measureHeight(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        //wrap_content
        if (specMode == MeasureSpec.AT_MOST) {
            return (SystemUtils.getSystemDisplay(context)[0] * 1.2).toInt()
        }
        //fill_parent或者精确值
        else if (specMode == MeasureSpec.EXACTLY) {
            return specSize
        }
        return (SystemUtils.getSystemDisplay(context)[0] * 1.2).toInt()
    }

    private fun notifyNumberPassed(number: Int) {
        password.add(number)
        mPasswordListeners.forEach { listener ->
            listener.onNumberClick(number, password.size)
        }
        if (password.size >= passwordSize) {
            mPasswordListeners.forEach { listener ->
                listener.onInputDone(password)
            }
            handler.postDelayed({
                password.clear()
                deleteLastNumber()
            }, 300)
        }
    }

    fun deleteLastNumber() {
        password.removeLastOrNull()
        invalidate()
        mPasswordListeners.forEach { listener ->
            listener.onRemove(password.size)
        }
    }
    fun clearNumber() {
        password.clear()
        invalidate()
        mPasswordListeners.forEach { listener ->
            listener.onRemove(password.size)
        }
    }

    /*
     * 恢复默认值
     */
    private fun setDefault() {
        circle_x = 0f
        circle_y = 0f
        type = -1
        number = -1
        sendAccessEvent(R.string.numeric_keyboard_cancel)
    }

    /*
     * 设置辅助功能描述
     */
    private fun sendAccessEvent(resId: Int) {
        //设置描述
        contentDescription = context.getString(resId)
        //发送辅助事件
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
        contentDescription = null
    }

    /*
     * 判断点击的是哪一个数字圆
     */
    private fun handleDown(x: Float, y: Float) {
        //判断点击的是那一列的数据
        if (xs[0] - click_radius <= x && x <= xs[0] + click_radius) { //第一列
            //获取点击处的圆心横坐标
            circle_x = xs[0]
            //判断点击的是哪一排
            if (ys[0] - click_radius <= y && ys[0] + click_radius >= y) { //第1排
                //获取点击的数字圆的圆心纵坐标
                circle_y = ys[0]
                number = 1 //设置点击的数字
            } else if (ys[1] - click_radius <= y && ys[1] + click_radius >= y) { //第2排
                //获取点击的数字圆的圆心纵坐标
                circle_y = ys[1]
                number = 4 //设置点击的数字
            } else if (ys[2] - click_radius <= y && ys[2] + click_radius >= y) { //第3排
                //获取点击的数字圆的圆心纵坐标
                circle_y = ys[2]
                number = 7 //设置点击的数字
            }
        } else if (xs[1] - click_radius <= x && x <= xs[1] + click_radius) { //第2列
            //获取点击处的圆心横坐标
            circle_x = xs[1]
            //判断点击的是哪一排
            if (ys[0] - click_radius <= y && ys[0] + click_radius >= y) { //第1排
                //获取点击的数字圆的圆心纵坐标
                circle_y = ys[0]
                number = 2 //设置点击的数字
            } else if (ys[1] - click_radius <= y && ys[1] + click_radius >= y) { //第2排
                //获取点击的数字圆的圆心纵坐标
                circle_y = ys[1]
                number = 5 //设置点击的数字
            } else if (ys[2] - click_radius <= y && ys[2] + click_radius >= y) { //第3排
                //获取点击的数字圆的圆心纵坐标
                circle_y = ys[2]
                number = 8 //设置点击的数字
            } else if (ys[3] - click_radius <= y && ys[3] + click_radius >= y) { //第4排
                //获取点击的数字圆的圆心纵坐标
                circle_y = ys[3]
                number = 0 //设置点击的数字
            }
        } else if (xs[2] - click_radius <= x && x <= xs[2] + click_radius) { //第3列
            //获取点击处的圆心横坐标
            circle_x = xs[2]
            //判断点击的是哪一排
            if (ys[0] - click_radius <= y && ys[0] + click_radius >= y) { //第1排
                //获取点击的数字圆的圆心纵坐标
                circle_y = ys[0]
                number = 3 //设置点击的数字
            } else if (ys[1] - click_radius <= y && ys[1] + click_radius >= y) { //第2排
                //获取点击的数字圆的圆心纵坐标
                circle_y = ys[1]
                number = 6 //设置点击的数字
            } else if (ys[2] - click_radius <= y && ys[2] + click_radius >= y) { //第3排
                //获取点击的数字圆的圆心纵坐标
                circle_y = ys[2]
                number = 9 //设置点击的数字
            }
        }
        sendAccessEvent(R.string.numeric_keyboard_down)
        type = 0 //按下刷新
        //绘制点击时的背景圆
        invalidate()
    }
}