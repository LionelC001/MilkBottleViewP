package com.lionel.drawp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/**
 * have to use match_parent or exact dp value for layout_width and layout_height
 */
class MilkVolumeView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    companion object {
        const val VOLUME_PER_BOTTLE_IN_ML: Int = 1000
        const val WIDTH_BOTTLE_IN_DP = 65
        const val WIDTH_BOTTLE_COVER_IN_DP = 53
        const val WIDTH_PAINT_STROKE_IN_DP = 6
        const val MAX_NUM_BOTTLE = 4
        const val MAX_NUM_BOTTLE_SCALE = 4
    }

    private var defaultWidth: Int = 0
    private var defaultHeight: Int = 0
    private var milkVolume: Int = 0


    private val redThickPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorRed)
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = DimensionUtil.turnDpToPx(context, WIDTH_PAINT_STROKE_IN_DP)
        strokeCap = Paint.Cap.ROUND
    }

    private val pinkPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPink)
    }

    fun setVolume(milkVolume: Int) {
        this.milkVolume = milkVolume
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize      // match_parent or exact dp value
            MeasureSpec.AT_MOST -> defaultWidth   // wrap_content
            else -> defaultWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> defaultHeight
            else -> defaultHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        drawBottle(canvas)
    }

    private fun drawBottle(canvas: Canvas) {
        val numBottle: Int = when {
            milkVolume < VOLUME_PER_BOTTLE_IN_ML -> 1
            milkVolume % VOLUME_PER_BOTTLE_IN_ML == 0 -> milkVolume / VOLUME_PER_BOTTLE_IN_ML
            else -> milkVolume / VOLUME_PER_BOTTLE_IN_ML + 1
        }


        //瓶身參數
        val widthBottle = DimensionUtil.turnDpToPx(context, WIDTH_BOTTLE_IN_DP)
        var left = redThickPaint.strokeWidth
        var right = widthBottle
        val top = height * 1 / 3F
        val bottom = height.toFloat() - redThickPaint.strokeWidth

        val heightBottle = bottom - top
        val marginBottle = (width - (DimensionUtil.turnDpToPx(context, (WIDTH_BOTTLE_IN_DP) * MAX_NUM_BOTTLE) + WIDTH_PAINT_STROKE_IN_DP * 2)) / (MAX_NUM_BOTTLE - 1)   //佔用n個奶瓶和兩條線(左右各一條), 之間的空隙均分
        val marginStart = DimensionUtil.turnDpToPx(context, WIDTH_BOTTLE_IN_DP) + marginBottle
        val marginBottleScale = heightBottle / (MAX_NUM_BOTTLE_SCALE + 1)
        val radius = DimensionUtil.turnDpToPx(context, 10)

        //瓶蓋參數
        val widthBottleCover = DimensionUtil.turnDpToPx(context, WIDTH_BOTTLE_COVER_IN_DP)
        val marginBottleCover = (widthBottle - widthBottleCover) / 2
        val bitmapCover = (ContextCompat.getDrawable(context, R.drawable.ic_cover) as BitmapDrawable).bitmap

        //畫出奶瓶數量
        for (i in 1..numBottle) {
            left += when (i) {
                1 -> 0F
                else -> marginStart
            }

            right += when (i) {
                1 -> 0F
                else -> marginStart
            }

            //畫瓶蓋
            val rectCover = Rect((left + marginBottleCover).toInt(), 0, (right - marginBottleCover).toInt(), (top - marginBottleCover).toInt())
            canvas.drawBitmap(bitmapCover, null, rectCover, null)

            //畫奶量
            if (i == numBottle && milkVolume % VOLUME_PER_BOTTLE_IN_ML != 0) {            //沒滿
                Path().apply {
                    val topMilk = bottom - (heightBottle * (milkVolume % VOLUME_PER_BOTTLE_IN_ML) / VOLUME_PER_BOTTLE_IN_ML)
                    val radii = floatArrayOf(0F, 0F, 0F, 0F, radius, radius, radius, radius)
                    addRoundRect(left, topMilk, right, bottom, radii, Path.Direction.CW)
                    canvas.drawPath(this, pinkPaint)
                }
            } else {
                canvas.drawRoundRect(left, top, right, bottom, radius, radius, pinkPaint)   //滿瓶
            }

            //畫瓶身
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, redThickPaint)

            //畫刻線
            for (j in 1..MAX_NUM_BOTTLE_SCALE) {
                Path().apply {
                    moveTo(left, bottom - (marginBottleScale * j))
                    lineTo(right - (widthBottle / 2), bottom - (marginBottleScale * j))
                    canvas.drawPath(this, redThickPaint)
                }
            }
        }
    }
}