package uz.alien.dictup.activity

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import uz.alien.dictup.R
import uz.alien.dictup.databinding.ActivityBaseBinding
import uz.alien.dictup.databinding.NavigationBinding
import uz.alien.dictup.manager.ReferralManager

abstract class BaseActivity : AppCompatActivity() {

  private lateinit var binding: ActivityBaseBinding
  private lateinit var navigationBinding: NavigationBinding
  lateinit var handler: Handler

  private var interstitialAd: InterstitialAd? = null

  val duration = 274L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    handler = Handler(mainLooper)

    installSplashScreen()

    binding = ActivityBaseBinding.inflate(layoutInflater)
    navigationBinding = NavigationBinding.inflate(layoutInflater)

    binding.navigationView.addView(navigationBinding.root)

    navigationBinding.bShare.setOnClickListener {
      ReferralManager.shareReferralLink(this)
    }

    navigationBinding.bShowAd.setOnClickListener {
      interstitialAd?.show(this)
    }

//    binding.navigationView.setNavigationItemSelectedListener { item ->
//      when (item.itemId) {
//        R.id.nav_home -> Toast.makeText(this@BaseActivity, "Home clicked", Toast.LENGTH_SHORT).show()
//        R.id.nav_settings -> Toast.makeText(this@BaseActivity, "Settings clicked", Toast.LENGTH_SHORT).show()
//      }
//      binding.root.closeDrawers()
//      true
//    }

    // TODO: Admob Adds loading

    InterstitialAd.load(
      this,
      "ca-app-pub-7031957988362944/5271395557",
      AdRequest.Builder().build(),
      object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(ad: InterstitialAd) {
          Log.d("Admob ads", "Ad was loaded.")
          interstitialAd = ad

          interstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
              override fun onAdDismissedFullScreenContent() {
                // Called when fullscreen content is dismissed.
                Log.d("Admob ads", "Ad was dismissed.")
                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
                interstitialAd = null
              }

              override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when fullscreen content failed to show.
                Log.d("Admob ads", "Ad failed to show.")
                // Don't forget to set the ad reference to null so you
                // don't show the ad a second time.
                interstitialAd = null
              }

              override fun onAdShowedFullScreenContent() {
                // Called when fullscreen content is shown.
                Log.d("Admob ads", "Ad showed fullscreen content.")
              }

              override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d("Admob ads", "Ad recorded an impression.")
              }

              override fun onAdClicked() {
                // Called when ad is clicked.
                Log.d("Admob ads", "Ad was clicked.")
              }
            }
        }

        override fun onAdFailedToLoad(adError: LoadAdError) {
          Log.d("Admob ads", adError.message)
          interstitialAd = null
        }
      },
    )

    onBackPressedDispatcher.addCallback(this@BaseActivity) {
      if (binding.root.isDrawerOpen(GravityCompat.START)) {
        binding.root.closeDrawer(GravityCompat.START)
      } else {
        if (isEnabled) {
          remove()
          onBackPressedDispatcher.onBackPressed()
        }
      }
    }

//    clearPadding(binding.navigationView)

    setSystemExclusion()

    setContentView(binding.root)

    onReady(savedInstanceState)
  }

  protected abstract fun onReady(savedInstanceState: Bundle?)

  protected fun setContentLayout(inflate: (LayoutInflater) -> View) {
    val contentView = inflate(layoutInflater)
    binding.contentFrame.addView(contentView)
  }

  protected fun getDrawerLayout(): DrawerLayout = binding.root

  protected fun getNavigationView(): NavigationView = binding.navigationView

  protected fun handleIntent(intent: Intent) {
    val action = intent.action
    val data = intent.data

    when {
      (Intent.ACTION_VIEW == action && data != null &&
              (data.scheme == "file" || data.scheme == "content")) -> {

        val text = readTextFromUri(data)
        Log.d("@@@@", "File content:\n$text")
      }

      (Intent.ACTION_VIEW == action && data != null &&
              (data.scheme == "http" || data.scheme == "https" || data.scheme == "app")) -> {

        val path = data.path ?: ""
        val queryParams = data.queryParameterNames.joinToString(", ") {
          "$it = ${data.getQueryParameter(it)}"
        }
        Log.d("@@@@", "Path: $path\nQuery: $queryParams")
      }

      (Intent.ACTION_MAIN == action) -> {
        Log.d("@@@@", "By launcher")
      }

      else -> {
        Log.d("@@@@", "Unknown launch type")
      }
    }
  }

  private fun setSystemExclusion() {
    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
      ViewCompat.setSystemGestureExclusionRects(v, listOf(Rect(0, 0, 100, v.height)))
      insets
    }
  }

  fun setAlphaAnimation(intent: Intent? = null) {
    intent?.let { startActivity(it) }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.fade_in, R.anim.fade_out)
    } else {
      overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
  }

  fun setOpenSwipeAnimation(intent: Intent? = null) {
    intent?.let { startActivity(it) }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right, R.anim.slide_out_left)
    } else {
      overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
  }

  fun setExitSwipeAnimation() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_left, R.anim.slide_out_right)
    } else {
      overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
  }

  fun setOpenZoomAnimation(intent: Intent? = null) {
    intent?.let { startActivity(it) }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.zoom_in_out, R.anim.zoom_out_in)
    } else {
      overridePendingTransition(R.anim.zoom_in_out, R.anim.zoom_out_in)
    }
  }

  fun setExitZoomAnimation() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.zoom_in_in, R.anim.zoom_out_out)
    } else {
      overridePendingTransition(R.anim.zoom_in_in, R.anim.zoom_out_out)
    }
  }

  protected fun setSystemPadding(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(0, systemBars.top, 0, 0)
      insets
    }
  }

  protected fun clearPadding(view: View) {
    ViewCompat.setOnApplyWindowInsetsListener(view, null)
    view.setPadding(0, 0, 0, 0)
  }

  protected fun setHomeEdge() {
    if (isNight()) {
      enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.dark(
          scrim = getColor(R.color.books_background)
        ),
        navigationBarStyle = SystemBarStyle.dark(
          scrim = Color.TRANSPARENT
        )
      )
    } else {
      enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(
          darkScrim = getColor(R.color.books_background),
          scrim = getColor(R.color.books_background)
        ),
        navigationBarStyle = SystemBarStyle.light(
          darkScrim = Color.TRANSPARENT,
          scrim = Color.TRANSPARENT
        )
      )
    }
  }

  protected fun setClearEdge() {
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

  protected fun lockDrawer() {
    getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
  }

  protected fun unlockDrawer() {
    getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
  }

  protected fun isNight(): Boolean {
    return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
  }

  protected fun dp() = Resources.getSystem().displayMetrics.density

  private fun readTextFromUri(uri: Uri): String {
    return try {
      contentResolver.openInputStream(uri)?.use { input ->
        input.bufferedReader().use { it.readText() }
      } ?: "Empty file"
    } catch (e: Exception) {
      "Error reading file: ${e.message}"
    }
  }
}