package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var brush = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pen = Paint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var valueAnimator : ValueAnimator
    private var backgroundFill = 0
    private var processFill = 0
    private var circleFill = 0
    private var textColor = 0
    private var textSize = 0f

    private var radiusShape = 0f
    private var fillShape = 0f

    private var buttonText = context.getString(R.string.button_name)
    // Save text strings in order not to have to search for them constantly.

    private lateinit var rectArc : RectF


    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when(new){
            ButtonState.Loading -> {
                buttonText = context.getString(R.string.button_loading)
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                buttonText = context.getString(R.string.button_name)
                radiusShape = 0f
                fillShape = 0f
                valueAnimator.cancel()
                invalidate()
            }
        }

        valueAnimator.addUpdateListener { dynamicValue ->
            val dynamicValueFloat = dynamicValue.animatedValue as Float

            fillShape = dynamicValueFloat
            radiusShape = (dynamicValueFloat * 360) / widthSize

            invalidate()
        }
    }

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            defStyleAttr,
            0
        )

        with(typedArray) {
            backgroundFill = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            processFill = getColor(R.styleable.LoadingButton_processColor, 0)
            circleFill = getColor(R.styleable.LoadingButton_circleColor, 0)
            textColor =getColor(R.styleable.LoadingButton_textColor, 0)
            textSize = getDimension(R.styleable.LoadingButton_textSize, 0f)
        }

        typedArray.recycle()

    }

    override fun onDraw(canvas: Canvas?) {
        //For Background
        canvas?.drawColor(backgroundFill)

        //For Fill
        brush.color = processFill
        canvas?.drawRect(0f, 0f, fillShape, measuredHeight.toFloat(), brush)

        //For Text
        pen.color = textColor
        pen.textSize = textSize
        pen.textAlign = Paint.Align.CENTER
        canvas?.drawText(buttonText, measuredWidth/2f,
            ((measuredHeight/2f) - ((pen.descent() + pen.ascent()) / 2)),
            pen)

        //For Circle
        brush.color = circleFill
        canvas?.drawArc(rectArc, 2f, fillShape, true, brush)

        canvas?.save()
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        widthSize = w
        heightSize = h

        rectArc = RectF(widthSize/4f * 3, heightSize/2f - 50,
            100 + (widthSize/4f * 3), heightSize/2f + 50)

        valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply{
            duration = 1500
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
        }
    }
}
