package uz.alien.dictup.presentation.features.base

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import uz.alien.dictup.databinding.BaseActivityBinding
import uz.alien.dictup.databinding.BaseNavigationBinding
import uz.alien.dictup.presentation.common.extention.interstitialAd
import uz.alien.dictup.presentation.common.extention.loadInterstitialAd
import uz.alien.dictup.presentation.common.extention.setSystemExclusion
import androidx.core.graphics.Insets
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.alien.dictup.presentation.common.extention.getSystemStatusPadding
import uz.alien.dictup.presentation.common.extention.startActivityWithAlphaAnimation
import uz.alien.dictup.presentation.common.extention.startActivityWithSlideAnimation
import uz.alien.dictup.presentation.common.extention.startActivityWithZoomAnimation
import uz.alien.dictup.presentation.common.model.AnimationType
import uz.alien.dictup.presentation.features.setting.SettingActivity
import uz.alien.dictup.utils.Logger

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    private lateinit var binding: BaseActivityBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationBinding: BaseNavigationBinding
    val baseViewModel: BaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = BaseActivityBinding.inflate(layoutInflater)
        navigationBinding = BaseNavigationBinding.inflate(layoutInflater)
        drawerLayout = binding.root
        binding.navigationView.addView(navigationBinding.root)

        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                baseViewModel.openDrawer()
            }

            override fun onDrawerClosed(drawerView: View) {
                baseViewModel.closeDrawer()
            }
        })

        getSystemStatusPadding(binding.root) { insets ->
            if (setStatusPadding(insets)) {
                val paddings = getStatusPadding()
                binding.statusBarPadding.setPadding(
                    paddings.left,
                    paddings.top,
                    paddings.right,
                    0
                )
                binding.contentFrame.setPadding(
                    paddings.left,
                    0,
                    paddings.right,
                    paddings.bottom
                )
            }
        }

        val paddings = getStatusPadding()
        binding.statusBarPadding.setPadding(
            paddings.left,
            paddings.top,
            paddings.right,
            0
        )
        binding.contentFrame.setPadding(
            paddings.left,
            0,
            paddings.right,
            paddings.bottom
        )

        initViews()

        loadInterstitialAd()

        setSystemExclusion(binding.root)

        setContentView(binding.root)

        observeDrawer()
        observeSearchView()
        observeActivityOpening()
    }

    override fun onRestart() {
        super.onRestart()
        baseViewModel.clearIntent()
    }

    override fun onResume() {
        super.onResume()
        handleBackPress()
    }

    protected open fun onCustomBackPressed(): Boolean = false

    protected fun handleBackPress() {

        onBackPressedDispatcher.addCallback(this) {

            when {

                isDrawerVisible() || baseViewModel.isDrawerOpen.value -> {
                    baseViewModel.closeDrawer()
                }

                baseViewModel.isSearchVisible.value -> {
                    baseViewModel.hideSearch()
                }

                onCustomBackPressed() -> {
                    // subclass o‘ziga xos case’ni handle qildi
                }

                else -> {
                    if (isEnabled) {
                        remove()
                        onBackPressedDispatcher.onBackPressed()
                    }
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
            startActivity(Intent.createChooser(shareIntent, "Do'stlaringiz bilan ulashing"))
        }

        navigationBinding.bShowAd.setOnClickListener {
            interstitialAd?.show(this)
        }

        navigationBinding.bOpenSetting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            baseViewModel.startActivityWithAnimation(intent, AnimationType.SLIDE)
        }

        binding.drawerButton.setOnClickListener {
            baseViewModel.openDrawer()
        }
    }

    private fun observeDrawer() {

        lifecycleScope.launch {
            baseViewModel.isDrawerOpen.collectLatest {
                if (it) {
                    drawerLayout.openDrawer(GravityCompat.START)
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
        }

        lifecycleScope.launch {
            baseViewModel.isDrawerLocked.collectLatest {
                if (it) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                } else {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
        }
    }

    private fun observeSearchView() {

        lifecycleScope.launch {
            baseViewModel.isSearchVisible.collectLatest {
                if (it) {
//                    binding.searchView.visibility = View.VISIBLE
                } else {
//                    binding.searchView.visibility = View.GONE
                }
            }
        }
    }

    private fun observeActivityOpening() {

        lifecycleScope.launch {

            baseViewModel.openActivity.collectLatest { intent ->

                if (intent != null) {

                    when (baseViewModel.animationType.value) {

                        AnimationType.SLIDE -> {
                            startActivityWithSlideAnimation(intent)
                        }

                        AnimationType.ZOOM -> {
                            startActivityWithZoomAnimation(intent)
                        }

                        AnimationType.FADE -> {
                            startActivityWithAlphaAnimation(intent)
                        }

                        AnimationType.NONE -> {
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun isDrawerVisible(): Boolean {
        return drawerLayout.isDrawerVisible(GravityCompat.START)
    }

    private fun getStatusPadding(): Rect {
        return baseViewModel.getSystemPaddings()
    }

    private fun setStatusPadding(insets: Insets): Boolean {
        return if (insets.top != 0) {
            baseViewModel.saveSystemPaddings(insets)
            true
        } else {
            Logger.w("Status padding is 0")
            false
        }
    }

    protected fun setContentLayout(inflate: (LayoutInflater) -> View) {
        val contentView = inflate(layoutInflater)
        binding.contentFrame.addView(contentView)
    }

    protected fun hideAppBar() {
        binding.statusBarPadding.visibility = View.GONE
    }

    protected fun showAppBar() {
        binding.statusBarPadding.visibility = View.VISIBLE
    }

    protected fun setCaption(text: String) {
        binding.tvAppBarCaption.text = text
    }

    protected fun setCaptionOnLongClickListener(listener: View.OnLongClickListener) {
        binding.tvAppBarCaption.setOnLongClickListener(listener)
    }
}