package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var buttonBackgroundColor = Int.MIN_VALUE
    private var textColor = Int.MIN_VALUE

    private var progressWidth = 0
    private var angle = 0

    private val downloadRect = Rect()

    private val valueAnimator = ValueAnimator()
    private var rectangleAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()
    private var isLoading = false

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Loading -> {
                isLoading = true
                Log.i("loading", "loading")
                rectangleAnimator = ValueAnimator.ofInt(0, widthSize).apply {
                    duration = 4000
                    addUpdateListener { valueAnimator ->
                        progressWidth = animatedValue as Int
                        valueAnimator.repeatCount = ValueAnimator.INFINITE
                        valueAnimator.repeatMode = ValueAnimator.REVERSE
                        invalidate()
                    }
                    start()
                }

                circleAnimator = ValueAnimator.ofInt(0, 360).apply {
                    duration  = 2000
                    addUpdateListener { valueAnimator ->
                        angle = valueAnimator.animatedValue as Int
                        valueAnimator.repeatCount = ValueAnimator.INFINITE
                        invalidate()
                    }
                    start()
                }

            }
            ButtonState.Completed -> {
                isLoading = false
                rectangleAnimator.end()
                progressWidth = 0
                invalidate()
            }
        }
    }


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0).apply {
            try {
                buttonBackgroundColor = getColor(
                                            R.styleable.LoadingButton_buttonBackgroundColor,
                                        context.getColor(R.color.colorAccent))

                textColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)

            } finally {
                recycle()
            }
        }
    }

    private var paint = Paint()
    private var arcX = 0f
    private var arcY = 0f
    private val animationPaint = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    }
    private val arcColor = Paint().apply {
        color = ResourcesCompat.getColor(resources, R.color.white, null)
    }
    private val loadginArc = RectF()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = buttonBackgroundColor
        canvas?.drawRect(
            0f,
            0f,
            widthSize.toFloat(),
            heightSize.toFloat(), paint)

        paint.color = textColor
        paint.textSize = 60f
        paint.textAlign = Paint.Align.CENTER

        val buttonLabel = when(buttonState){
            ButtonState.Clicked -> "Clicked"
            ButtonState.Loading -> "We are loading"
            ButtonState.Completed -> "Download"
        }

        if(isLoading) {
            downloadRect.set(0, heightSize, progressWidth, 0)
            canvas?.drawRect(downloadRect, animationPaint)

            // arc animation
            loadginArc.set(arcX, arcY, arcX + 80, arcY + 80)
            canvas?.drawArc(
                loadginArc,
                0f,
                ((progressWidth * 360) / widthSize).toFloat(),
                true,
                arcColor
            )
        }

        canvas?.drawText(buttonLabel,
                        (widthSize / 2).toFloat() ,(heightSize/2).toFloat(), paint)
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