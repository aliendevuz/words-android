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
import uz.alien.dictup.presentation.common.extention.startActivityWithSlideAnimation
import uz.alien.dictup.presentation.features.setting.SettingActivity

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var binding: BaseActivityBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationBinding: BaseNavigationBinding

    var isDrawerFullyOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = BaseActivityBinding.inflate(layoutInflater)
        navigationBinding = BaseNavigationBinding.inflate(layoutInflater)
        drawerLayout = binding.root
        binding.navigationView.addView(navigationBinding.root)

        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                isDrawerFullyOpen = true
            }

            override fun onDrawerClosed(drawerView: View) {
                isDrawerFullyOpen = false
            }
        })


        initViews()

        loadInterstitialAd()

        setSystemExclusion(binding.root)

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        handleBackPress()
    }

    private fun handleBackPress() {

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

        navigationBinding.bOpenSetting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivityWithSlideAnimation(intent)
        }
    }

    protected fun setContentLayout(inflate: (LayoutInflater) -> View) {
        val contentView = inflate(layoutInflater)
        binding.contentFrame.addView(contentView)
    }

    protected fun isDrawerOpen(): Boolean {
        return drawerLayout.isDrawerOpen(GravityCompat.START)
    }

    protected fun isDrawerVisible(): Boolean {
        return drawerLayout.isDrawerVisible(GravityCompat.START)
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