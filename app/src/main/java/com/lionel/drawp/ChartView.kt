package com.lionel.drawp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/**
 * have to use match_parent or exact dp value for layout_width and layout_height
 *
 * data should use mutableListOf(Int?), to present minutes each hour
 */

class ChartView(context: Context, attr: AttributeSet?) : View(context, attr) {
    companion object {
        const val WIDTH_PAINT_IN_DP = 1
        const val TEXT_SIZE_IN_DP = 12
        const val MARGIN_CHART_TOP = 24
        const val MARGIN_CHART_LEFT = 24
        const val MARGIN_CHART_BOTTOM = 40
        const val MAX_MINUTE_VALUE = 30
        const val MIN_MINUTE_VALUE = 0
        const val INTERVAL_MINUTE_VALUE = 5
        const val MAX_HOUR_VALUE = 23
        const val MIN_HOUR_VALUE = 0
        const val INTERVAL_HOUR_VALUE = 1
        const val INTERVAL_HOUR_VALUE_SHOWED = 2
    }

    private var data: MutableList<Int?> = mutableListOf()
    private val defaultWidthSize: Int = 0
    private val defaultHeightSize: Int = 0

    private val grayPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorGray)
        isAntiAlias = true
        textSize = DimensionUtil.turnDpToPx(context, TEXT_SIZE_IN_DP)
    }

    private val deepGrayPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorDeepGray)
        isAntiAlias = true
        textSize = DimensionUtil.turnDpToPx(context, TEXT_SIZE_IN_DP)
    }

    private val bluePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorBlue)
        isAntiAlias = true
        strokeWidth = DimensionUtil.turnDpToPx(context, WIDTH_PAINT_IN_DP)
    }

    fun setData(data: MutableList<Int?>) {
        this.data = data
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize            // match_parent or exact dp value
            MeasureSpec.AT_MOST -> defaultWidthSize     // wrap_content
            else -> defaultWidthSize
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> defaultHeightSize
            else -> defaultHeightSize
        }

        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas) {
        drawChart(canvas)
    }

    private fun drawChart(canvas: Canvas) {
        //畫X軸標題
        canvas.drawText("分鐘", 0F, grayPaint.textSize, grayPaint)

        //畫X軸座標
        var bottomMinute = height - DimensionUtil.turnDpToPx(context, MARGIN_CHART_BOTTOM)
        val leftMinute = deepGrayPaint.textSize * 0.5F
        val marginMinute = (height - DimensionUtil.turnDpToPx(context, MARGIN_CHART_BOTTOM + MARGIN_CHART_TOP)) / ((MAX_MINUTE_VALUE - MIN_MINUTE_VALUE) / INTERVAL_MINUTE_VALUE)
        val leftHorizontalLine = DimensionUtil.turnDpToPx(context, MARGIN_CHART_LEFT)

        for (i in MIN_MINUTE_VALUE..MAX_MINUTE_VALUE step INTERVAL_MINUTE_VALUE) {
            bottomMinute -= when (i) {
                MIN_MINUTE_VALUE -> 0F
                else -> marginMinute
            }
            canvas.drawText("$i", leftMinute, bottomMinute + deepGrayPaint.textSize * 0.5F, deepGrayPaint)  //+0.5F是使字體垂直置中
            canvas.drawLine(leftHorizontalLine, bottomMinute, width.toFloat(), bottomMinute, grayPaint)
        }

        //畫Y軸標題
        canvas.drawText("時間", width.toFloat() - grayPaint.textSize * 2, height.toFloat() - grayPaint.textSize * 0.5F, grayPaint)

        //畫Y軸座標
        val bottomHour = height - DimensionUtil.turnDpToPx(context, MARGIN_CHART_BOTTOM) + deepGrayPaint.textSize * 1.5F
        var leftHour = DimensionUtil.turnDpToPx(context, MARGIN_CHART_LEFT)
        val marginHour = (width - DimensionUtil.turnDpToPx(context, MARGIN_CHART_LEFT) - deepGrayPaint.textSize * 2) / ((MAX_HOUR_VALUE - MIN_HOUR_VALUE) / INTERVAL_HOUR_VALUE)

        for (j in MIN_HOUR_VALUE..MAX_HOUR_VALUE step INTERVAL_HOUR_VALUE) {
            leftHour += when (j) {
                MIN_HOUR_VALUE -> deepGrayPaint.textSize * 0.25F      //小於二位數時, 字體要加1/4字體寬, 置中
                10 -> marginHour - deepGrayPaint.textSize * 0.25F     //大於二位數後, 要扣回來
                else -> marginHour
            }

            if (j % INTERVAL_HOUR_VALUE_SHOWED != 0) canvas.drawText("$j", leftHour, bottomHour, deepGrayPaint)
        }

        //畫chart條, 同一個字體寬
        val heightChart = height - DimensionUtil.turnDpToPx(context, MARGIN_CHART_BOTTOM + MARGIN_CHART_TOP)
        val bottomChart = height - DimensionUtil.turnDpToPx(context, MARGIN_CHART_BOTTOM)
        var leftChart = DimensionUtil.turnDpToPx(context, MARGIN_CHART_LEFT)
        val radius = DimensionUtil.turnDpToPx(context, 10)
        for (j in MIN_HOUR_VALUE..MAX_HOUR_VALUE step INTERVAL_HOUR_VALUE) {
            leftChart += when (j) {
                MIN_HOUR_VALUE -> 0F
                else -> marginHour
            }

            Path().apply {
                val chartData = data.getOrNull(j) ?: 0
                val topChartBar = bottomChart - (heightChart * chartData / MAX_MINUTE_VALUE)
                val rightChartBar = leftChart + deepGrayPaint.textSize
                val radii = floatArrayOf(radius, radius, radius, radius, 0F, 0F, 0F, 0F)
                addRoundRect(leftChart, topChartBar, rightChartBar, bottomChart, radii, Path.Direction.CW)
                canvas.drawPath(this, bluePaint)
            }
        }
    }
}
