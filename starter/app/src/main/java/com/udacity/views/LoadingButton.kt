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
    private val accentColor = ResourcesCompat.getColor(resources, R.color.colorAccent, null)

    private var animatedLoadingBarValue: Float = 0.0f
    private var finalWidth: Float = 0.0f
    private var finalCircleDegrees = 360f
    private var animatedLoadingCircleValue = 0.0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        //typeface = Typeface.create( "", Typeface.BOLD)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        if (new == ButtonState.Clicked) {
            var buttonValueAnimator = createOfFloatAnimator(finalWidth)
            buttonState = ButtonState.Loading

            buttonValueAnimator?.addUpdateListener {
                animatedLoadingBarValue = it.animatedValue as Float
                invalidate()

                if (animatedLoadingBarValue.equals(finalWidth)) {
                    buttonState = ButtonState.Completed
                }
            }
            buttonValueAnimator?.start()

            var circleValueAnimator = createOfFloatAnimator(finalCircleDegrees)
            circleValueAnimator?.addUpdateListener {
                animatedLoadingCircleValue = it.animatedValue as Float
            }
            circleValueAnimator?.start()

        } else if (new == ButtonState.Completed){
            invalidate()
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

        // calculate size of button canvas
        var left = canvas.getClipBounds().left.toFloat()
        var top = canvas.getClipBounds().top.toFloat()
        var bottom = canvas.getClipBounds().bottom.toFloat()
        finalWidth = canvas.getClipBounds().right.toFloat()

        var circleDiameter = 100f

        //calculate circle position
        var leftCirclePos = finalWidth-circleDiameter-100
        var bottomCirclePos = (bottom/2) + (circleDiameter/2)
        var rightCirclePos = finalWidth-100
        var topCircelPos = (bottom/2) - (circleDiameter/2)

        if (buttonState.equals(ButtonState.Completed)) {
            this.setBackgroundColor(backgroundColor)
            paint.color = textColor
            canvas.drawText(context.getString(R.string.download_text), x, y, paint)
        } else if (buttonState.equals(ButtonState.Loading)){
            //draw loading text
            paint.color = loadingColor
            canvas.drawRect(RectF(left, top, animatedLoadingBarValue, bottom), paint)
            //draw loading animation
            paint.color = textColor
            canvas.drawText(context.getString(R.string.button_loading), x,y, paint)
            //draw circle
            paint.color = accentColor
            canvas.drawArc(RectF(leftCirclePos,topCircelPos,rightCirclePos,bottomCirclePos),0f, animatedLoadingCircleValue,true,paint)
        }
    }


    fun createOfFloatAnimator(finalWidth: Float): ValueAnimator? {
        var startWidth = 0F
        var valueAnimator = ValueAnimator.ofFloat(startWidth, finalWidth)
        valueAnimator.duration = 4000


        valueAnimator.interpolator = AccelerateInterpolator(2F)
        return valueAnimator

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