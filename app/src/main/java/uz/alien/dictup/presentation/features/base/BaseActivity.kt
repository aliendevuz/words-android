package uz.alien.dictup.presentation.features.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import uz.alien.dictup.databinding.BaseActivityBinding
import uz.alien.dictup.databinding.BaseNavigationBinding
import uz.alien.dictup.databinding.BaseDialogBinding
import uz.alien.dictup.presentation.common.extention.initDialog
import uz.alien.dictup.presentation.common.extention.interstitialAd
import uz.alien.dictup.presentation.common.extention.loadInterstitialAd
import uz.alien.dictup.presentation.common.extention.setSystemExclusion
import androidx.core.content.edit

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var binding: BaseActivityBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationBinding: BaseNavigationBinding

    private val prefs by lazy {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = BaseActivityBinding.inflate(layoutInflater)
        navigationBinding = BaseNavigationBinding.inflate(layoutInflater)
        drawerLayout = binding.root
        binding.navigationView.addView(navigationBinding.root)

        initViews()

        loadInterstitialAd()

        onBackPressedDispatcher.addCallback(this@BaseActivity) {
            if (isDrawerOpen()) {
                closeDrawer()
            } else {
                if (isEnabled) {
                    remove()
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        setSystemExclusion(binding.root)

        setContentView(binding.root)
    }

    private fun initViews() {

        navigationBinding.bShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val appLink = "https://play.google.com/store/apps/details?id=$packageName"
            shareIntent.putExtra(Intent.EXTRA_TEXT, appLink)
            startActivity(Intent.createChooser(shareIntent, "Share DictUp via"))
        }

        navigationBinding.bShowAd.setOnClickListener {
            interstitialAd?.show(this)
        }

        navigationBinding.seekPitch.progress = (getPitch() * 100).toInt()
        navigationBinding.seekSpeed.progress = (getSpeed() * 100).toInt()

        navigationBinding.tvPitchValue.text = getPitch().toString()
        navigationBinding.tvSpeedValue.text = getSpeed().toString()

        navigationBinding.seekPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val pitch = progress / 100f
                navigationBinding.tvPitchValue.text = pitch.toString()
                savePitch(pitch)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        navigationBinding.seekSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val speed = progress / 100f
                navigationBinding.tvSpeedValue.text = speed.toString()
                saveSpeed(speed)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun getPitch(): Float {
        return prefs.getFloat("pitch", 1.0f)
    }

    private fun getSpeed(): Float {
        return prefs.getFloat("speed", 1.0f)
    }

    private fun savePitch(pitch: Float) {
        prefs.edit {
            putFloat("pitch", pitch)
        }
    }

    private fun saveSpeed(speed: Float) {
        prefs.edit {
            putFloat("speed", speed)
        }
    }

    protected fun setContentLayout(inflate: (LayoutInflater) -> View) {
        val contentView = inflate(layoutInflater)
        binding.contentFrame.addView(contentView)
    }

    protected fun isDrawerOpen(): Boolean {
        return drawerLayout.isDrawerOpen(GravityCompat.START)
    }

    protected fun closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    protected fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    protected fun lockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    protected fun unlockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }
}