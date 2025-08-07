package uz.alien.dictup.presentation.features.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.core.view.GravityCompat
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import dagger.hilt.android.AndroidEntryPoint
import uz.alien.dictup.R
import uz.alien.dictup.databinding.ActivityMainBinding
import uz.alien.dictup.databinding.ScreenHomeBinding
import uz.alien.dictup.databinding.ScreenWelcomeBinding
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.presentation.features.select.SelectActivity
import uz.alien.dictup.presentation.common.AutoLayoutManager
import uz.alien.dictup.presentation.common.MarginItemDecoration
import uz.alien.dictup.presentation.features.test.TestActivity

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainBinding: ScreenHomeBinding

    private var welcomeBinding: ScreenWelcomeBinding? = null
    private var isSearchBarVisible = false

    override fun onReady(savedInstanceState: Bundle?) {
        lockDrawer()

        binding = ActivityMainBinding.inflate(layoutInflater)
        mainBinding = ScreenHomeBinding.inflate(layoutInflater)

        handleIntent(intent)

        setClearEdge()

        setContentLayout {
            binding.root
        }

        onBackPressedDispatcher.addCallback(this) {
            if (getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
                getDrawerLayout().closeDrawer(GravityCompat.START)
            } else if (isSearchBarVisible) {
                hideSearchBar()
            } else {
                if (isEnabled) {
                    remove()
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        // TODO: We can use notification to make our app more useful. How?
        //  Everyday we send notification for more practicing

        setSystemPadding(binding.root)


        // boshqa narsa bu
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) Log.d("FCM", "Token: ${task.result}")
        }

        requestNotificationPermission(this)

        // TODO: app update API ni ham ulab qo'yishim kerak

        welcomeBinding = ScreenWelcomeBinding.inflate(layoutInflater)

        binding.root.addView(welcomeBinding?.root)

        startWelcome()
    }

    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("@@@@", "notificationga ruxsat berildi")
            } else {
                Log.d("@@@@", "notificationga ruxsat rad etildi")
            }
        }
    }

    private fun hideSearchBar() {
        // Animatsiya bilan yo’q qilish, holatni o‘zgartirish va hokazo
        isSearchBarVisible = false
        // masalan:
        // binding.searchBar.visibility = View.GONE
    }

    fun setHomeScreen() {

        unlockDrawer()

        clearPadding(binding.root)

        binding.root.removeAllViews()

        binding.root.addView(mainBinding.root)

        setSystemPadding(mainBinding.statusBarPadding)

        mainBinding.root.startAnimation(AnimationSet(false).apply {
            addAnimation(AlphaAnimation(0.0f, 1.0f))
            duration = this@MainActivity.duration * 3
        })

        val beginnerBooks = arrayListOf(
            AdapterBookBeginner.Book(R.color.blue2, R.drawable.v_image),
            AdapterBookBeginner.Book(R.color.purple2, R.drawable.v_image),
            AdapterBookBeginner.Book(R.color.red_pink, R.drawable.v_image),
            AdapterBookBeginner.Book(R.color.pink, R.drawable.v_image)
        )

        val essentialBooks = arrayListOf(
            AdapterBookBeginner.Book(R.color.book1, R.drawable.book_0),
            AdapterBookBeginner.Book(R.color.book2, R.drawable.book_1),
            AdapterBookBeginner.Book(R.color.book3, R.drawable.book_2),
            AdapterBookBeginner.Book(R.color.book4, R.drawable.book_3),
            AdapterBookBeginner.Book(R.color.book5, R.drawable.book_4),
            AdapterBookBeginner.Book(R.color.book6, R.drawable.book_5)
        )

        mainBinding.rvBeginner.layoutManager = AutoLayoutManager(this, 4)
        mainBinding.rvBeginner.addItemDecoration(MarginItemDecoration((4 * dp()).toInt(), 4))
        mainBinding.rvBeginner.adapter = AdapterBookBeginner(beginnerBooks, this)

        mainBinding.rvEssential.layoutManager = AutoLayoutManager(this, 3)
        mainBinding.rvEssential.addItemDecoration(MarginItemDecoration((4 * dp()).toInt(), 3))
        mainBinding.rvEssential.adapter = AdapterBookEssential(essentialBooks, this)

        mainBinding.bOpenSelector.setOnClickListener {
//            val intent = Intent(this, SelectActivity::class.java)
//            intent.putExtra("picked_book", 2)
//            intent.putExtra("amount_of_book", 6)
//            setOpenSwipeAnimation(intent)
//            viewModel.printWords()
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }

        mainBinding.drawerButton.setOnClickListener {
            getDrawerLayout().openDrawer(GravityCompat.START)
        }
    }

    // TODO: Refactor this animation flow with Coroutine or MotionLayout
    private fun startWelcome() {

        welcomeBinding?.let {

            it.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    it.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    var margin = 0.0f

                    it.lWelcomeLogoBackground.y += margin

                    val layoutParams =
                        it.lWelcomeLogoBackground.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.setMargins(0, 0, 0, 0)
                    it.lWelcomeLogoBackground.layoutParams = layoutParams

                    it.ivWelcomeLogo1.alpha = 0.0f
                    it.ivWelcomeLogo2.alpha = 0.0f
                    it.ivWelcomeLogo3.alpha = 0.0f

                    it.tvWelcomeBrand.alpha = 0.0f
                    it.tvWelcomeProduct.alpha = 0.0f

                    handler.postDelayed(duration) {

                        it.lWelcomeLogoBackground.measure(
                            View.MeasureSpec.UNSPECIFIED,
                            View.MeasureSpec.UNSPECIFIED
                        )

                        margin = it.root.height.toFloat() / 6


                        handler.postDelayed(duration * 2) {

                            it.ivWelcomeLogo1.alpha = 1.0f
                            it.ivWelcomeLogo1.startAnimation(AnimationSet(true).apply {
                                addAnimation(AlphaAnimation(0f, 1f))
                                this.duration = 300L
                                this.interpolator = LinearInterpolator()
                            })

                            handler.postDelayed(1000L) {
                                it.ivWelcomeLogo2.alpha = 1.0f
                                it.ivWelcomeLogo2.startAnimation(AnimationSet(true).apply {
                                    addAnimation(AlphaAnimation(0f, 1f))
                                    this.duration = 300L
                                    this.interpolator = LinearInterpolator()
                                })

                                handler.postDelayed(1000L) {
                                    it.ivWelcomeLogo3.alpha = 1.0f
                                    it.ivWelcomeLogo3.startAnimation(AnimationSet(true).apply {
                                        addAnimation(AlphaAnimation(0f, 1f))
                                        this.duration = 300L
                                        this.interpolator = LinearInterpolator()
                                    })

                                    handler.postDelayed(1000L) {
                                        it.lWelcomeLogoBackground.y -= margin
                                        it.lWelcomeLogoBackground.startAnimation(AnimationSet(true).apply {
                                            addAnimation(
                                                TranslateAnimation(
                                                    0.0f,
                                                    0.0f,
                                                    margin,
                                                    0.0f
                                                )
                                            )
                                            this.duration = 600L
                                            this.interpolator = AccelerateDecelerateInterpolator()
                                        })

                                        handler.postDelayed(1600L) {
                                            it.tvWelcomeBrand.alpha = 1.0f
                                            it.tvWelcomeBrand.startAnimation(AnimationSet(true).apply {
                                                addAnimation(AlphaAnimation(0f, 1f))
                                                this.duration = 300L
                                                this.interpolator = LinearInterpolator()
                                            })

                                            handler.postDelayed(1000L) {
                                                it.tvWelcomeProduct.alpha = 1.0f
                                                it.tvWelcomeProduct.startAnimation(AnimationSet(true).apply {
                                                    addAnimation(AlphaAnimation(0f, 1f))
                                                    this.duration = 300L
                                                    this.interpolator = LinearInterpolator()
                                                })

                                                handler.postDelayed(2000L) {

                                                    it.root.startAnimation(AnimationSet(false).apply {
                                                        addAnimation(AlphaAnimation(1.0f, 0.0f))
                                                        duration = this@MainActivity.duration / 2
                                                    })

                                                    handler.postDelayed(duration / 2) {

                                                        setHomeScreen()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }
    }
}