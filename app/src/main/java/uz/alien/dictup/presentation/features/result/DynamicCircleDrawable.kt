package uz.alien.dictup.presentation.features.result

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.alpha

class DynamicCircleDrawable(
    private val radius: Float = 500f,
    private val startColor: Int = 0xFFE57373.toInt(), // Qizil #E57373
    private val endColor: Int = 0xFF8CE573.toInt()    // Yashil #8CE573
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private var progress: Float = 0f // 0f to 1f

    fun setProgress(value: Float) {
        this.progress = value.coerceIn(0f, 1f)
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        // Rangni interpolate qilish
        val color = interpolateColor(startColor, endColor, progress)
        paint.color = color

        // Doira chizish
        val bounds = bounds
        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()
        val drawRadius = minOf(bounds.width(), bounds.height()) / 2f

        canvas.drawCircle(centerX, centerY, drawRadius, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return android.graphics.PixelFormat.TRANSLUCENT
    }

    /**
     * Ikki rang o'rtasida smooth transition
     * HSV orqali interpolation qilish yaxshi natija beradi
     */
    private fun interpolateColor(
        @ColorInt startColor: Int,
        @ColorInt endColor: Int,
        progress: Float
    ): Int {
        val startHSV = FloatArray(3)
        val endHSV = FloatArray(3)

        Color.colorToHSV(startColor, startHSV)
        Color.colorToHSV(endColor, endHSV)

        // Hue, Saturation, Value interpolate qilish
        val interpolatedHue = startHSV[0] + (endHSV[0] - startHSV[0]) * progress
        val interpolatedSat = startHSV[1] + (endHSV[1] - startHSV[1]) * progress
        val interpolatedValue = startHSV[2] + (endHSV[2] - startHSV[2]) * progress

        val startA = Color.alpha(startColor)
        val endA = Color.alpha(endColor)
        val interpolatedA = (startA + (endA - startA) * progress).toInt().coerceIn(0, 255)

        return Color.HSVToColor(interpolatedA,
            floatArrayOf(interpolatedHue, interpolatedSat, interpolatedValue)
        )
    }
}