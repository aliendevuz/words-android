package uz.alien.dictup.presentation.features.result

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.animation.ValueAnimator
import androidx.activity.viewModels
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import uz.alien.dictup.databinding.ResultActivityBinding
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.model.Attempt
import uz.alien.dictup.presentation.features.base.BaseActivity
import kotlin.math.pow

class ResultActivity : BaseActivity() {

    private lateinit var binding: ResultActivityBinding
    private val viewModel: ResultViewModel by viewModels()

    private var restart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ResultActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        setClearEdge()

        hideAppBar()


        initViews()
    }

    private fun initViews() {

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
            viewModel.result.collect { result ->
                animateResult(result)
            }
        }
    }

    private fun animateResult(targetValue: Float) {
        val animator = ValueAnimator.ofFloat(0f, targetValue)
        animator.duration = 1500L  // 1.5 soniya
        animator.interpolator = AccelerateDecelerateInterpolator() // tabiiy silliqlik

        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            val displayValue = String.format("%.1f%%", animatedValue)
            binding.tvResult.text = "Sizning natijangiz: $displayValue"
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