package uz.alien.dictup.presentation.common.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.withRotation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withTranslation
import kotlin.math.cos
import kotlin.math.sin

class CongratsFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var isAnimating = false
    private val confetti = mutableListOf<Confetti>()
    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private val colors = intArrayOf(
        "#FF0000".toColorInt(),
        "#00AA00".toColorInt(),
        "#0000FF".toColorInt(),
        "#000000".toColorInt(),
        "#FF5500".toColorInt(),
        "#1100AA".toColorInt(),
        "#AA0055".toColorInt(),
        "#006600".toColorInt()
    )

    init {
        setWillNotDraw(false) // Canvas draw chaqirishi uchun zarur
    }

    fun congrats() {
        if (isAnimating) return
        if (width == 0 || height == 0) {
            post { congrats() }
            return
        }
        isAnimating = true
        confetti.clear()

        // Chap chekkadan konfetti
        repeat(40) {
            confetti.add(Confetti(
                x = 0f,
                y = height / 2f,
                vx = Random.nextFloat() * 8 + 3,  // O'ngga
                vy = Random.nextFloat() * 10 - 5, // Yuqoridan pastga
                size = Random.nextFloat() * 12 + 8,
                color = colors[Random.nextInt(colors.size)],
                rotation = Random.nextFloat() * 360,
                shape = Random.nextInt(3) // 0: Star, 1: Rectangle, 2: Diamond
            ))
        }

        // O'ng chekkadan konfetti
        repeat(40) {
            confetti.add(Confetti(
                x = width.toFloat(),
                y = height / 2f,
                vx = -(Random.nextFloat() * 8 + 3), // Chapga
                vy = Random.nextFloat() * 10 - 5,   // Yuqoridan pastga
                size = Random.nextFloat() * 12 + 8,
                color = colors[Random.nextInt(colors.size)],
                rotation = Random.nextFloat() * 360,
                shape = Random.nextInt(3)
            ))
        }

        startAnimation()
    }

    private fun startAnimation() {
        val startTime = System.currentTimeMillis()
        val duration = 3000L // 3 soniya

        val animator = object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = elapsed.toFloat() / duration

                if (progress < 1f) {
                    updateConfetti()
                    invalidate()
                    post(this)
                } else {
                    isAnimating = false
                    confetti.clear()
                    invalidate()
                }
            }
        }
        post(animator)
    }

    private fun updateConfetti() {
        for (conf in confetti) {
            conf.x += conf.vx
            conf.y += conf.vy
            conf.vy += 0.25f // Gravity
            conf.rotation += 12f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isAnimating) {
            for (conf in confetti) {
                // Konfetti ko'rinmasini olish
                if (conf.y < height && conf.x in 0f..width.toFloat()) {
                    canvas.save()
                    canvas.translate(conf.x, conf.y)
                    canvas.rotate(conf.rotation)

                    paint.color = conf.color
                    paint.style = Paint.Style.FILL

                    when (conf.shape) {
                        0 -> drawStar(canvas, conf.size)        // Yulduz
                        1 -> drawRectangle(canvas, conf.size)   // To'rtburchak
                        2 -> drawDiamond(canvas, conf.size)     // Romb
                    }

                    canvas.restore()
                }
            }
        }
    }

    private fun drawStar(canvas: Canvas, size: Float) {
        val path = Path()
        val radius = size / 2
        val innerRadius = radius / 2.4f

        for (i in 0..9) {
            val angle = (i * 36 - 90) * Math.PI / 180
            val r = if (i % 2 == 0) radius else innerRadius
            val x = (r * cos(angle)).toFloat()
            val y = (r * sin(angle)).toFloat()

            if (i == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawRectangle(canvas: Canvas, size: Float) {
        canvas.drawRect(-size / 2, -size / 3, size / 2, size / 3, paint)
    }

    private fun drawDiamond(canvas: Canvas, size: Float) {
        val path = Path()
        path.moveTo(0f, -size / 2)           // Yuqori
        path.lineTo(size / 2, 0f)            // O'ng
        path.lineTo(0f, size / 2)            // Quyi
        path.lineTo(-size / 2, 0f)           // Chap
        path.close()
        canvas.drawPath(path, paint)
    }

    private data class Confetti(
        var x: Float,
        var y: Float,
        val vx: Float,
        var vy: Float,
        val size: Float,
        val color: Int,
        var rotation: Float,
        val shape: Int
    )
}