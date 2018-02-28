package com.martemyanova.brainmap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.util.TypedValue
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import java.util.*


class RadarChart : View {

    private val outerCircleRadius = 100F.dpToPx()
    private val numberOfCircles = 10
    private val numberOfAxis = 6
    private val axisHeight = outerCircleRadius + 9F.dpToPx()
    private val axisCircleRadius = (11F / 2).dpToPx()
    private val paint = Paint()
    private val textPaint = TextPaint()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorGrey))

        paint.isAntiAlias = true
        textPaint.color = Color.WHITE
        textPaint.textSize = 50F
    }

    override fun onDraw(canvas: Canvas?) {
        val c = canvas ?: return

        c.translate(width/2f, height/2f);
        drawCircles(c)
        drawAxis(c)


        //c.drawLine(0F, 0F, x, y, paint);
        //c.drawLine(x, 0F, 0F, y, paint);

    }

    private fun drawCircles(canvas: Canvas) {
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE

        val h = outerCircleRadius / numberOfCircles

        for (i in 1..numberOfCircles) {
            if (i % 2 == 0) paint.color = Color.WHITE
            else paint.color = ContextCompat.getColor(context, R.color.colorLightGrey)
            canvas.drawCircle(0F, 0F, i * h, paint);
        }
    }

    private fun drawAxis(c: Canvas) {
        paint.color = Color.WHITE
        val r = axisHeight

        for (i in 0 until numberOfAxis) {
            val alfa = Math.toRadians((i * 360 / numberOfAxis - 90).toDouble())
            var x = Math.cos(alfa).toFloat()
            var y = Math.sin(alfa).toFloat()
            c.drawLine(0F, 0F, r * x, r * y, paint);

            val circleH = r + axisCircleRadius
            c.drawCircle(circleH * x, circleH * y, axisCircleRadius, paint);

            drawText(c, "Text $i", circleH * x, circleH * y)
        }
    }

    private fun drawText(c: Canvas, text: String, x: Float, y: Float) {
        c.save()

        var textY =
        if (y <= 0) y - 4*10F.dpToPx()
        else y

        c.translate(x - c.width / 2, textY);
        val sl = StaticLayout(text, textPaint, c.width,
                Layout.Alignment.ALIGN_CENTER, 1f, 1f, true)

        val am = context.applicationContext.assets
        val typeface = Typeface.createFromAsset(am, String.format(Locale.US,
                "fonts/%s", "GothamSSm-Light.ttf"))
        textPaint.typeface = typeface
        sl.draw(c)
        c.restore()
    }

    fun Float.dpToPx(): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, metrics)
    }

}