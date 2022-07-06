package com.udacity.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import com.udacity.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 100
    private var heightSize = 40
    private lateinit var loadingBarRect: RectF

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private val loadingColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private val textColor = ResourcesCompat.getColor(resources, R.color.white, null)

    private var valueAnimator = ValueAnimator()
    private var animatedValue: Float = 0.0f
    private var finalWidth: Float = 0.0F

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        //typeface = Typeface.create( "", Typeface.BOLD)
    }


    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> startValueAnimator(finalWidth)
            ButtonState.Completed -> invalidate()
        }
        if (new == ButtonState.Clicked) {
            startValueAnimator(finalWidth)
        }
    }


    init {
        isClickable = true
    }

    override fun performClick(): Boolean {
        buttonState = ButtonState.Clicked
        if (super.performClick()) return true
        return true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var x = (widthSize / 2).toFloat()
        var y = (heightSize - (paint.descent() + paint.ascent())) / 2

        var left = canvas.getClipBounds().left.toFloat()
        var top = canvas.getClipBounds().top.toFloat()
        var bottom = canvas.getClipBounds().bottom.toFloat()
        finalWidth = canvas.getClipBounds().right.toFloat()

        if (buttonState.equals(ButtonState.Completed)) {
            this.setBackgroundColor(backgroundColor)
            paint.color = textColor
            canvas.drawText(context.getString(R.string.download_text), x, y, paint)
        } else if (buttonState.equals(ButtonState.Loading)){
            paint.color = loadingColor
            canvas.drawRect(RectF(left, top, animatedValue, bottom), paint)
            paint.color = textColor
            canvas.drawText(context.getString(R.string.button_loading), x,y, paint)
        }
    }


    fun startValueAnimator(finalWidth: Float) {
        var startWidth = 0F
        valueAnimator = ValueAnimator.ofFloat(startWidth, finalWidth)
        valueAnimator.duration = 5000

        buttonState = ButtonState.Loading

        valueAnimator.addUpdateListener {
            animatedValue = it.animatedValue as Float
            invalidate()

            if (animatedValue.equals(finalWidth)) {
                buttonState = ButtonState.Completed
            }
        }
        valueAnimator.interpolator = AccelerateInterpolator(1F)
        valueAnimator.start()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}