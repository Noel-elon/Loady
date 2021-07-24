package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var animColor = 0
    private var ovalObjectColor = 0
    private var bgColour = 0
    private var currentText = ""
    private var arcProgress = 0.0F
    private val textRect = Rect(0,0,0,0)
    private val animRect = Rect(0, 0, 0, 0)
    private val animPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val btnTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        textSize = 40F
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private var valueAnimator: ValueAnimator? = null

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonState = ButtonState.Loading
            }
            ButtonState.Loading -> {
                startAnimation()
            }
            ButtonState.Completed -> {
                animRect.right = 0
                arcProgress = 0f
                currentText = "Download"
                invalidate()
            }
        }
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            animColor = getColor(R.styleable.LoadingButton_animColor, Color.DKGRAY)
            ovalObjectColor = getColor(R.styleable.LoadingButton_arcColor, Color.YELLOW)
            bgColour = getColor(R.styleable.LoadingButton_btnBgColor, Color.BLACK)
        }
        currentText = "Download"
    }


    @SuppressLint("ResourceAsColor")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {

            //rectangle
            drawColor(bgColour)
            animPaint.color = animColor
            drawRect(0f, 0f, arcProgress, measuredHeight.toFloat(), animPaint)

            //text
            btnTextPaint.getTextBounds(currentText, 0, currentText.length, textRect)
            drawText(
                currentText, (widthSize / 2).toFloat(),
                (heightSize / 2).minus(textRect.exactCenterY()),
                btnTextPaint
            )


            //arc
            animPaint.color = ovalObjectColor
            val left = (widthSize / 2 + textRect.exactCenterX()).plus(textRect.height() / 2)
            val top = (heightSize / 2 - textRect.height() / 2).toFloat()
            val right = left + textRect.height()
            val bottom = (heightSize / 2 + textRect.height() / 2).toFloat()
            drawArc(
                left,
                top,
                right,
                bottom,
                0f,
                arcProgress,
                true,
                animPaint

            )

        }


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

    fun changeButtonState(state: ButtonState) {
        buttonState = state
    }

    private fun startAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0F, measuredWidth.toFloat()).apply {
            duration = 1500
            addUpdateListener {
                arcProgress = it.animatedValue as Float
                currentText = "Downloading.."
                invalidate()
            }

            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                }
            })

            start()
        }
    }

}