package com.datangic.smartlock.widgets


import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import com.google.android.material.button.MaterialButton


@SuppressLint("ResourceType")
class LoadingMaterialButton : MaterialButton {

    private var startAngle = 0

    private val task: Runnable = Runnable {
        stopAnim()
        text = _text
    }

    var delayTime: Long = 7 * 1000
    private var _text: CharSequence = ""

    private val paint: Paint by lazy {
        Paint().apply {
            strokeWidth = 8F
            style = Paint.Style.STROKE
        }
    }

    private var arcValueAnimator: ValueAnimator? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    init {
        paint.color = currentTextColor
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isClickable) {
            @SuppressLint("DrawAllocation")
            val rectF = RectF((width / 2 - 35).toFloat(), (height / 2 - 35).toFloat(), (width / 2 + 35).toFloat(), (height / 2 + 35).toFloat())
            canvas.drawArc(rectF, startAngle.toFloat(), startAngle.toFloat() % 360, false, paint)
        }
    }

    fun startAnim() {
        if (isClickable) {
            _text = text
            text = ""
            isClickable = false
            showArc()
            handler.postDelayed(task, delayTime)
        }
    }


    private fun showArc() {
        if (arcValueAnimator == null) {
            arcValueAnimator = ValueAnimator.ofInt(0, 720)
            arcValueAnimator?.addUpdateListener { animation ->
                startAngle = animation.animatedValue as Int
                invalidate()
            }
            arcValueAnimator?.interpolator = LinearInterpolator()
            arcValueAnimator?.repeatCount = ValueAnimator.INFINITE
            arcValueAnimator?.repeatMode = ValueAnimator.RESTART
            arcValueAnimator?.duration = 2000
        }
        arcValueAnimator?.start()
    }

    fun stopAnim() {
        if (!isClickable) {
            isClickable = true
            arcValueAnimator?.pause()
            handler.removeCallbacks(task)
        }
    }

    override fun onDetachedFromWindow() {
        handler.removeCallbacks(task)
        super.onDetachedFromWindow()
    }
}