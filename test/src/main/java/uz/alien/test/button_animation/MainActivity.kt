package uz.alien.test.button_animation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.os.Handler
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import uz.alien.test.databinding.ActivityMainBinding
import uz.alien.test.payment.MainActivity
import kotlin.random.Random
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable


class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  // Hozirgi ranglar holatini saqlash
  private var currentBackgroundColor = "#FFFF8080".toColorInt()
  private var currentStrokeColor = Color.BLACK
  private var currentTextColor = Color.BLACK

  fun getRandomColor(): Int {
    val rnd = Random.Default
    return rnd.nextInt(-16777216, 0)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setClearEdge()

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    val normalDrawable = GradientDrawable().apply {
      shape = GradientDrawable.RECTANGLE
      cornerRadius = 24f
      setColor(currentBackgroundColor)
      setStroke(4, currentStrokeColor)
    }

    val rippleDrawable = RippleDrawable(
      ColorStateList.valueOf("#80FFFFFF".toColorInt()),
      normalDrawable,
      null
    )

    binding.tvHello.background = rippleDrawable
    val rootDrawable = currentBackgroundColor.toDrawable()
    binding.root.background = rootDrawable

    binding.tvHello.setTextColor(currentTextColor)

    binding.tvHello.setOnClickListener {

//      Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()

      val targetBackgroundColor = getRandomColor()

      // 🔁 Background color animation
      val bgAnim = ValueAnimator.ofObject(ArgbEvaluator(), currentBackgroundColor, targetBackgroundColor)
      bgAnim.duration = 274
      bgAnim.addUpdateListener { animator ->
        val color = animator.animatedValue as Int
        normalDrawable.setColor(color)
        rootDrawable.color = color
        currentBackgroundColor = color
      }
      bgAnim.start()

      // 🌓 Text color (black or white) depending on luminance

      val targetTextColor = if (ColorUtils.calculateLuminance(targetBackgroundColor) > 0.5) {
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = true
        Color.BLACK
      } else {
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = false
        Color.WHITE
      }
      val textAnim = ValueAnimator.ofObject(ArgbEvaluator(), currentTextColor, targetTextColor)
      textAnim.duration = 274
      textAnim.addUpdateListener { animator ->
        val color = animator.animatedValue as Int
        binding.tvHello.setTextColor(color)
        currentTextColor = color
      }
      textAnim.start()

      // 🖌 Border color animation
      val strokeAnim = ValueAnimator.ofObject(ArgbEvaluator(), currentStrokeColor, targetTextColor)
      strokeAnim.duration = 274
      strokeAnim.addUpdateListener { animator ->
        val color = animator.animatedValue as Int
        normalDrawable.setStroke(4, color)
        currentStrokeColor = color
      }
      strokeAnim.start()

      Handler(mainLooper).postDelayed(3000L) {
        val paymentIntent = Intent(this, MainActivity::class.java)
        startActivity(paymentIntent)
      }
    }
  }

  fun setClearEdge() {
    if (isNight()) {
      enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(
          scrim = Color.TRANSPARENT
        ),
        navigationBarStyle = SystemBarStyle.dark(
          scrim = Color.TRANSPARENT
        )
      )
    } else {
      enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(
          darkScrim = Color.TRANSPARENT,
          scrim = Color.TRANSPARENT
        ),
        navigationBarStyle = SystemBarStyle.light(
          darkScrim = Color.TRANSPARENT,
          scrim = Color.TRANSPARENT
        )
      )
    }
  }

  fun isNight(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
  }
}