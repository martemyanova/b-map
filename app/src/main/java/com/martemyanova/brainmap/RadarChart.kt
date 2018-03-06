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
    private val axisMarkerRadius = (11F / 2).dpToPx()
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
    private val Canvas.axisLength: Float
        get() = (this.outerCircleRadius + this.areaSize * 0.06).toFloat()

    private val textColor by lazy { ContextCompat.getColor(context, R.color.colorText) }
    private val brightTextColor by lazy { ContextCompat.getColor(context, R.color.colorBrightText) }
    private val lineColor by lazy { ContextCompat.getColor(context, R.color.colorLine) }
    private val secondLineColor by lazy { ContextCompat.getColor(context, R.color.colorSecondLine) }
    private val firstShapeColor by lazy { ContextCompat.getColor(context, R.color.colorFirstShape) }
    private val secondShapeColor by lazy { ContextCompat.getColor(context, R.color.colorSecondShape) }

    private val markerPalette: Array<Int>
    private val peakBlueColor by lazy { ContextCompat.getColor(context, R.color.colorPeakBlue) }
    private val pinkColor by lazy { ContextCompat.getColor(context, R.color.colorPink) }
    private val greenColor by lazy { ContextCompat.getColor(context, R.color.colorGreen) }
    private val purpleColor by lazy { ContextCompat.getColor(context, R.color.colorPurple) }
    private val orangeColor by lazy { ContextCompat.getColor(context, R.color.colorOrange) }
    private val blueColor by lazy { ContextCompat.getColor(context, R.color.colorBlue) }

    private var axisLabelIds = arrayOf<Int>()
    private var firstShapeData = arrayOf<Int>()
    private var secondShapeData = arrayOf<Int>()

    var typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        paint.isAntiAlias = true
        textPaint.textSize = textSize

        markerPalette = arrayOf(peakBlueColor, orangeColor, greenColor, purpleColor, blueColor, pinkColor)
    }

    fun setData(axisLabelIds: Array<Int>, firstShapeData: Array<Int>, secondShapeData: Array<Int>) {
        this.axisLabelIds = axisLabelIds
        this.firstShapeData = firstShapeData
        this.secondShapeData = secondShapeData
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return

        canvas.translate(width/2f, height/2f);
        drawCircles(canvas)
        drawAxis(canvas)
        drawShapes(canvas)
    }

    private fun drawCircles(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth1

        val h = canvas.outerCircleRadius / numberOfCircles

        for (i in 1..numberOfCircles) {
            paint.color = if (i % 2 == 0) lineColor else secondLineColor
            canvas.drawCircle(0F, 0F, i * h, paint);
        }
    }

    private fun drawAxis(canvas: Canvas) {
        if (axisLabelIds.isEmpty()) return

        for (i in 0 until axisLabelIds.size) {
            drawAxisLine(canvas, position = i)
            drawMarker(canvas, position = i)
            drawText(canvas, position = i)
        }
    }

    private fun drawAxisLine(canvas: Canvas, position: Int) {
        paint.color = lineColor
        paint.strokeWidth = strokeWidth1

        val (x, y) = vectorAt(position, length = canvas.axisLength)
        canvas.drawLine(0F, 0F, x, y, paint);
    }

    private fun drawMarker(canvas: Canvas, position: Int) {
        paint.color = markerColorAt(position)
        paint.strokeWidth = strokeWidth2

        val markerHeight = canvas.axisLength + axisMarkerRadius
        val (x, y) = vectorAt(position, length = markerHeight)
        canvas.drawCircle(x, y, axisMarkerRadius, paint);
    }

    private fun Int.idToString(): String = context.getString(this)

    private fun createStaticLayout(text: String, width: Int) =
            StaticLayout(text, textPaint, width, Layout.Alignment.ALIGN_CENTER,
                    1f, 1f, false)

    private fun drawText(canvas: Canvas, position: Int) {
        if (firstShapeData.isEmpty()) return

        val text = axisLabelIds[position].idToString()
        val value = firstShapeData[position]
        val markerHeight = canvas.axisLength + axisMarkerRadius
        val (x, y) = vectorAt(position, markerHeight)
        val labelWidth: Int = (((canvas.width - sideBorder)/2 - Math.abs(x)) * 2).toInt()
        val labelLayout = createStaticLayout(text, labelWidth)
        val valueLayout = createStaticLayout(value.toString(), labelWidth)

        val xBias: Float = (labelWidth/2).toFloat()
        val yBias: Float =
                if (y <= 1) {
                    - axisMarkerRadius - textHeight - valueLayout.height
                }
                else {
                    textHeight + labelLayout.height
                }

        //draw value
        canvas.save()
        canvas.translate(x - xBias, y + yBias)
        textPaint.typeface = typeface.setBold()
        textPaint.color = brightTextColor
        valueLayout.draw(canvas)
        canvas.restore()

        //draw label
        canvas.save()
        canvas.translate(x - xBias, y + yBias - labelLayout.height)
        textPaint.typeface = typeface
        textPaint.color = textColor
        labelLayout.draw(canvas)
        canvas.restore()
    }

    private fun drawShapes(c: Canvas) {
        if (firstShapeData.isEmpty()) return

        if (secondShapeData.isNotEmpty()) {
            paint.color = secondShapeColor
            drawPolygon(c, secondShapeData)
        }

        paint.color = firstShapeColor
        drawPolygon(c, firstShapeData)
    }

    private fun drawPolygon(canvas: Canvas, data:  Array<Int>) {
        paint.style = Paint.Style.FILL

        val (startX, startY) = canvas.normalizedVectorAt(0, data[0].toFloat())

        val path = Path()
        path.reset()
        path.moveTo(startX, startY)

        for (i in 1 until axisLabelIds.size) {
            val (x, y) = canvas.normalizedVectorAt(i, data[i].toFloat())
            path.lineTo(x, y)
        }
        path.lineTo(startX, startY)
        canvas.drawPath(path, paint)
    }

    private fun Float.normalize(outerCircleRadius: Float): Float = outerCircleRadius * this / units

    private fun angleAt(position: Int): Double =
        Math.toRadians((position * 360 / axisLabelIds.size - 90).toDouble())

    private fun unitVectorAt(position: Int): Pair<Float, Float> = Pair(
            Math.cos(angleAt(position)).toFloat(),
            Math.sin(angleAt(position)).toFloat())

    private fun vectorAt(position: Int, length: Float): Pair<Float, Float> {
        var (x, y) = unitVectorAt(position)
        x *= length
        y *= length
        return Pair(x, y)
    }

    private fun Canvas.normalizedVectorAt(position: Int, length: Float): Pair<Float, Float> =
            vectorAt(position, length.normalize(this.outerCircleRadius))

    private fun markerColorAt(position: Int) = markerPalette[position % markerPalette.size]

    private fun Float.dpToPx(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this,
                context.resources.displayMetrics)
    }

    private fun Float.spToPx(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this,
                context.resources.displayMetrics)
    }
}