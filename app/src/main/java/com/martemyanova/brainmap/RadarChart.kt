package com.martemyanova.brainmap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.util.*


class RadarChart : View {

    private val outerCircleRadius = 100F.dpToPx()
    private val numberOfCircles = 10
    private val numberOfAxis = 6
    private val axisHeight = outerCircleRadius + 9F.dpToPx()
    private val axisCircleRadius = (11F / 2).dpToPx()
    private val paint = Paint()
    private val textPaint = TextPaint()
    private val textSize = 14F.spToPx()
    private val textHeight = 10F.dpToPx()
    private val sideBorder = 10F.dpToPx()

    private val backgroundColor by lazy { ContextCompat.getColor(context, R.color.colorGrey) }
    private val greyColor by lazy { ContextCompat.getColor(context, R.color.colorLightGrey) }
    private val whiteColor by lazy { ContextCompat.getColor(context, R.color.colorWhite) }
    private val blueColor by lazy { ContextCompat.getColor(context, R.color.colorBlue) }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        setBackgroundColor(backgroundColor)

        paint.isAntiAlias = true
        textPaint.color = whiteColor
        textPaint.textSize = textSize
    }

    override fun onDraw(canvas: Canvas?) {
        val c = canvas ?: return

        c.translate(width/2f, height/2f);
        drawCircles(c)
        drawAxis(c)
    }

    private fun drawCircles(canvas: Canvas) {
        paint.color = whiteColor
        paint.style = Paint.Style.STROKE

        val h = outerCircleRadius / numberOfCircles

        for (i in 1..numberOfCircles) {
            if (i % 2 == 0) paint.color = whiteColor
            else paint.color = greyColor
            canvas.drawCircle(0F, 0F, i * h, paint);
        }
    }

    private fun drawAxis(c: Canvas) {
        val r = axisHeight
        val circleH = r + axisCircleRadius

        for (i in 0 until numberOfAxis) {
            val x = getXUnitVector(i)
            val y = getYUnitVector(i)
            paint.color = whiteColor
            c.drawLine(0F, 0F, r * x, r * y, paint);

            paint.color = blueColor
            c.drawCircle(circleH * x, circleH * y, axisCircleRadius, paint);

            drawText(c, "Peak Brain Score", "${i}00", circleH * x, circleH * y)
        }
    }

    private fun drawText(c: Canvas, text: String, value: String, x: Float, y: Float) {
        val typeface = Typeface.createFromAsset(context.applicationContext.assets,
                String.format(Locale.US,"fonts/%s", "GothamSSm-Light.ttf"))
        val boldTypeface = Typeface.create(typeface, Typeface.BOLD)

        val labelWidth: Int = (((c.width - sideBorder)/2 - Math.abs(x)) * 2).toInt()

        val labelLayout = StaticLayout(text, textPaint, labelWidth,
                Layout.Alignment.ALIGN_CENTER, 1f, 1f, false)
        val valueLayout = StaticLayout(value, textPaint, labelWidth,
                Layout.Alignment.ALIGN_CENTER, 1f, 1f, false)

        val xBias: Float = (labelWidth/2).toFloat()
        val yBias: Float =
                if (y <= 0) {
                    - axisCircleRadius - textHeight - valueLayout.height
                }
                else {
                    textHeight + labelLayout.height
                }

        //draw value
        c.save()
        c.translate(x - xBias, y + yBias)
        textPaint.typeface = boldTypeface
        valueLayout.draw(c)
        c.restore()

        //draw label
        c.save()
        c.translate(x - xBias, y + yBias - labelLayout.height)
        textPaint.typeface = typeface
        labelLayout.draw(c)
        c.restore()
    }

    private fun getAngle(position: Int): Double =
        Math.toRadians((position * 360 / numberOfAxis - 90).toDouble())

    private fun getXUnitVector(position: Int): Float =
        Math.cos(getAngle(position)).toFloat()

    private fun getYUnitVector(position: Int): Float =
        Math.sin(getAngle(position)).toFloat()

    private fun Float.dpToPx(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this,
                context.resources.displayMetrics)
    }

    private fun Float.spToPx(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this,
                context.resources.displayMetrics)
    }
}