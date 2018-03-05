package com.martemyanova.brainmap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class RadarChart : View {
    private val numberOfCircles = 10
    private val strokeWidth1 = 3F
    private val strokeWidth2 = 3F
    private val axisCircleRadius = (11F / 2).dpToPx()
    private val paint = Paint()
    private val textPaint = TextPaint()
    private val textSize = 15F.spToPx()
    private val textHeight = 10F.dpToPx()
    private val sideBorder = 50F.dpToPx()
    private val units = 1000

    private val Canvas.areaSize: Int
        get() = this.height / 2
    private val Canvas.outerCircleRadius: Float
        get() = (this.areaSize * 0.60).toFloat()
    private val Canvas.axisHeight: Float
        get() = (this.outerCircleRadius + this.areaSize * 0.06).toFloat()

    private val lightGreyColor by lazy { ContextCompat.getColor(context, R.color.colorLightGrey) }
    private val whiteColor by lazy { ContextCompat.getColor(context, R.color.colorWhite) }
    private val textColor by lazy { ContextCompat.getColor(context, R.color.colorText) }
    private val lineColor by lazy { ContextCompat.getColor(context, R.color.colorLine) }
    private val firstShapeColor by lazy { ContextCompat.getColor(context, R.color.colorFirstShape) }
    private val secondShapeColor by lazy { ContextCompat.getColor(context, R.color.colorSecondShape) }
    private val peakBlueColor by lazy { ContextCompat.getColor(context, R.color.colorPeakBlue) }
    private val pinkColor by lazy { ContextCompat.getColor(context, R.color.colorPink) }
    private val greenColor by lazy { ContextCompat.getColor(context, R.color.colorGreen) }
    private val purpleColor by lazy { ContextCompat.getColor(context, R.color.colorPurple) }
    private val orangeColor by lazy { ContextCompat.getColor(context, R.color.colorOrange) }
    private val blueColor by lazy { ContextCompat.getColor(context, R.color.colorBlue) }
    private val palette: Array<Int>

    private var axisLabelIds = arrayOf<Int>()
    private val numberOfAxis
        get() = axisLabelIds.size
    private var data1 = arrayOf<Int>()
    private var data2 = arrayOf<Int>()

    var typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        paint.isAntiAlias = true
        textPaint.color = whiteColor
        textPaint.textSize = textSize

        palette = arrayOf(peakBlueColor, orangeColor, greenColor, purpleColor, blueColor, pinkColor)
    }

    fun setData(axisLabelIds: Array<Int>, data1: Array<Int>, data2: Array<Int>) {
        this.axisLabelIds = axisLabelIds
        this.data1 = data1
        this.data2 = data2
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        val c = canvas ?: return

        c.translate(width/2f, height/2f);
        drawCircles(c)
        drawAxis(c)
        drawData(c)
    }

    private fun drawCircles(canvas: Canvas) {
        paint.color = whiteColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth1

        val h = canvas.outerCircleRadius / numberOfCircles

        for (i in 1..numberOfCircles) {
            if (i % 2 == 0) paint.color = lineColor
            else paint.color = lightGreyColor
            canvas.drawCircle(0F, 0F, i * h, paint);
        }
    }

    private fun drawAxis(c: Canvas) {
        if (axisLabelIds.isEmpty()) return

        val r = c.axisHeight
        val circleH = r + axisCircleRadius

        for (i in 0 until axisLabelIds.size) {
            val x = getXUnitVector(i)
            val y = getYUnitVector(i)
            paint.color = lineColor
            paint.strokeWidth = strokeWidth1
            c.drawLine(0F, 0F, r * x, r * y, paint);

            paint.color = getColor(i)
            paint.strokeWidth = strokeWidth2
            c.drawCircle(circleH * x, circleH * y, axisCircleRadius, paint);

            if (data1.isNotEmpty())
                drawText(c, axisLabelIds[i].idToString(), data1[i], circleH * x, circleH * y)
        }
    }

    private fun Int.idToString(): String = context.getString(this)

    private fun createStaticLayout(text: String, width: Int) =
            StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_CENTER,
                    1f, 1f, false)

    private fun drawText(c: Canvas, text: String, value: Int, x: Float, y: Float) {
        val labelWidth: Int = (((c.width - sideBorder)/2 - Math.abs(x)) * 2).toInt()
        val labelLayout = createStaticLayout(text, labelWidth)
        val valueLayout = createStaticLayout(value.toString(), labelWidth)

        val xBias: Float = (labelWidth/2).toFloat()
        val yBias: Float =
                if (y <= 1) {
                    - axisCircleRadius - textHeight - valueLayout.height
                }
                else {
                    textHeight + labelLayout.height
                }

        //draw value
        c.save()
        c.translate(x - xBias, y + yBias)
        textPaint.typeface = typeface.setBold()
        textPaint.color = whiteColor
        valueLayout.draw(c)
        c.restore()

        //draw label
        c.save()
        c.translate(x - xBias, y + yBias - labelLayout.height)
        textPaint.typeface = typeface
        textPaint.color = textColor
        labelLayout.draw(c)
        c.restore()
    }

    private fun drawData(c: Canvas) {
        if (data1.isEmpty()) return

        if (data2.isNotEmpty()) {
            paint.color = secondShapeColor
            drawPolygon(c, data2)
        }

        paint.color = firstShapeColor
        drawPolygon(c, data1)
    }

    private fun drawPolygon(c: Canvas, data:  Array<Int>) {
        paint.style = Paint.Style.FILL

        val startX = getXUnitVector(0) * data[0].normalize(c.outerCircleRadius)
        val startY = getYUnitVector(0) * data[0].normalize(c.outerCircleRadius)

        val path = Path()
        path.reset()
        path.moveTo(startX, startY)

        for (i in 1 until numberOfAxis) {
            val x = getXUnitVector(i) * data[i].normalize(c.outerCircleRadius)
            val y = getYUnitVector(i) * data[i].normalize(c.outerCircleRadius)
            path.lineTo(x, y)
        }
        path.lineTo(startX, startY)
        c.drawPath(path, paint)
    }

    private fun Int.normalize(outerCircleRadius: Float): Float = outerCircleRadius * this / units

    private fun getAngle(position: Int): Double =
        Math.toRadians((position * 360 / numberOfAxis - 90).toDouble())

    private fun getXUnitVector(position: Int): Float =
        Math.cos(getAngle(position)).toFloat()

    private fun getYUnitVector(position: Int): Float =
        Math.sin(getAngle(position)).toFloat()

    private fun getColor(position: Int) = palette[position % palette.size]

    private fun Float.dpToPx(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this,
                context.resources.displayMetrics)
    }

    private fun Float.spToPx(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this,
                context.resources.displayMetrics)
    }
}