package uz.alien.dictup.presentation.features.result

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import uz.alien.dictup.databinding.ResultActivityBinding
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.interstitialAd
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.model.Attempt
import uz.alien.dictup.presentation.features.base.BaseActivity
import java.util.concurrent.TimeUnit


class ResultActivity : BaseActivity() {

    private lateinit var binding: ResultActivityBinding
    private val viewModel: ResultViewModel by viewModels()

    private var restart = false

    // Drawable'lar
    private lateinit var innerCircleDrawable: DynamicCircleDrawable
    private lateinit var outerCircleDrawable: DynamicCircleDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ResultActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        setClearEdge()
        hideAppBar()

        // Dinamik drawable'lar yaratish
        innerCircleDrawable = DynamicCircleDrawable(
            radius = 500f,
            startColor = 0xFFE57373.toInt(), // Qizil
            endColor = 0xFF8CE573.toInt()    // Yashil
        )

        outerCircleDrawable = DynamicCircleDrawable(
            radius = 500f,
            startColor = 0x80E57373.toInt(),
            endColor = 0x808CE573.toInt()
        )

        // Parenti (outer circle) background qilish
//        binding.llResult.background = outerCircleDrawable

        // TextView (inner circle) background qilish
//        binding.tvResult.background = innerCircleDrawable

        initViews()
    }

    private fun initViews() {

        if (viewModel.shouldShowAd()) {
            if (application.interstitialAd != null) {
                application.interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent()
                        viewModel.countAdd()
                        application.interstitialAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        application.interstitialAd = null
                    }
                }
                application.interstitialAd?.show(this)
            }
        } else {
            viewModel.setLastShownAdTime()
        }

        val attempt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("attempt", Attempt::class.java)
        } else {
            intent.getParcelableArrayListExtra("attempt")
        }

        binding.bRetry.setOnClickListener {
            restart = true
            finish()
        }

        binding.bExit.setOnClickListener {
            finish()
        }

        viewModel.resolveAttempt(attempt ?: emptyList())

        collectResult()
    }

    private fun collectResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect { result ->
                    animateResult(result)
                }
            }
        }
    }

    private fun animateResult(targetValue: Float) {
        val animator = ValueAnimator.ofFloat(0f, targetValue)
        animator.duration = 1500L
        animator.interpolator = AccelerateDecelerateInterpolator()

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
//            val percentageProgress = animatedValue / 100f // 0f to 1f oralig'ida

            val displayValue = String.format("%.1f%%", animatedValue)
            binding.tvResult.text = displayValue

            // Drawable'lar uchun progress o'rnatish
//            innerCircleDrawable.setProgress(percentageProgress)
//            outerCircleDrawable.setProgress(percentageProgress)
            binding.circular.progress = animatedValue.toInt()

            if (animatedValue == 100f) {
                binding.konfetti.start(KonfettiPartyConfig.getQuickParty())
            }
        }

        animator.start()
    }

    override fun finish() {
        setResult(RESULT_OK, Intent().apply {
            putExtra("restart_quiz", restart)
        })
        super.finish()
        applyExitZoomTransition()
    }
}