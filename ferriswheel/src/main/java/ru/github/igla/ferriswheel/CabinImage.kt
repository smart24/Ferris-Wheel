package ru.github.igla.ferriswheel

import android.content.Context
import android.graphics.*

/**
 * Created by igor-lashkov on 17/01/2018.
 */

internal class CabinImage(context: Context, private val imageNumber: Int, private val startAngle: Double, colorFill: String) {

    val wheelPos: PointF = PointF()

    var tiltAngle = 0f

    private val dstRect = RectF()

    private val bottomLineOffset by lazyNonSafe { context.dpF(14f) }
    private val cabinLineHeight by lazyNonSafe { context.dpF(8f) }
    private val cabinWindowHeight by lazyNonSafe { context.dpF(24f) }
    private val cabinStrokeWidth by lazyNonSafe { context.dpF(6f) }
    private val cabinCornerRadius by lazyNonSafe { context.dpF(8f) }
    private val arcRadius by lazyNonSafe { context.dpF(8f) }

    private val cabinLinePaint by lazyNonSafe {
        smoothPaint(Color.BLACK).apply {
            style = Paint.Style.FILL
        }
    }

    private lateinit var cabinPaint: Paint
    private lateinit var cabinPaintFill: Paint

    init {
        Color.parseColor(colorFill).apply {
            cabinPaint = smoothPaint(this).apply {
                strokeWidth = cabinStrokeWidth
                style = Paint.Style.STROKE
            }
            cabinPaintFill = smoothPaint(this).apply {
                style = Paint.Style.FILL
            }
        }
    }

    fun getAngleOffset(rotateAngle: Float): Double = (startAngle + rotateAngle) % 360.0

    fun drawCabin(canvas: Canvas, position: PointF, size: Int, scaleRatio: Double) {
        canvas.apply {
            save()

            val scale = scaleRatio.toFloat()
            val arcRadiusScale = arcRadius * scale
            val cabinWindowHeightScale = cabinWindowHeight * scale
            val cabinCornerRadiusScale = cabinCornerRadius * scale
            val cabinStrokeWidthScale = cabinStrokeWidth * scale

            val angle = if (imageNumber % 2 == 0) tiltAngle else -tiltAngle
            rotate(angle, position.x, position.y)

            val halfSize = size / 2.0f
            val leftBorder = position.x - halfSize
            val topBorder = position.y
            val rightBorder = position.x + halfSize
            val bottomBorder = position.y + size

            val cabinStrokeHalf = cabinStrokeWidthScale / 2f
            val cabinTop = topBorder + cabinStrokeHalf
            val cabinBottom = bottomBorder - cabinStrokeHalf
            dstRect.set(
                    leftBorder + cabinStrokeHalf,
                    cabinTop,
                    rightBorder - cabinStrokeHalf,
                    cabinBottom)
            drawRoundRect(dstRect, cabinCornerRadiusScale, cabinCornerRadiusScale, cabinPaint) //cabin

            val centerArc = leftBorder + halfSize
            dstRect.set(centerArc - arcRadiusScale * 0.8f,
                    cabinTop - arcRadiusScale,
                    centerArc + arcRadiusScale * 0.8f,
                    topBorder + arcRadiusScale)
            drawArc(dstRect, 180f, 180f, true, cabinPaintFill) //top arc

            dstRect.set(
                    leftBorder,
                    cabinTop + cabinWindowHeightScale,
                    rightBorder,
                    cabinBottom)
            drawBottomRoundRect(canvas, dstRect, cabinPaintFill, cabinCornerRadiusScale) //cabin fill


            val blackLineTop = bottomBorder - (bottomLineOffset + cabinLineHeight) * scaleRatio.toFloat()
            val blackLineBottom = bottomBorder - bottomLineOffset * scaleRatio.toFloat()
            drawRect(
                    leftBorder,
                    blackLineTop,
                    rightBorder,
                    blackLineBottom,
                    cabinLinePaint
            )
            restore()
        }
    }

    private fun drawBottomRoundRect(canvas: Canvas, rect: RectF, paint: Paint, radius: Float) {
        canvas.drawRoundRect(rect, radius, radius, paint)
        canvas.drawRect(
                rect.left,
                rect.top,
                rect.right,
                rect.bottom - radius,
                paint
        )
    }
}