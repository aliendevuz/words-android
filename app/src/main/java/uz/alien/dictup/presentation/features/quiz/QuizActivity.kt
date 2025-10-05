package uz.alien.dictup.presentation.features.quiz

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.alien.dictup.databinding.QuizActivityBinding
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.domain.model.SelectedUnit
import uz.alien.dictup.presentation.common.extention.hideSystemUI
import uz.alien.dictup.presentation.common.model.AnimationType
import uz.alien.dictup.presentation.features.result.ResultActivity

@AndroidEntryPoint
class QuizActivity : BaseActivity() {

    private lateinit var binding: QuizActivityBinding
    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = QuizActivityBinding.inflate(layoutInflater)
        setContentLayout {
            binding.root
        }

        hideAppBar()

        setClearEdge()
        setSystemPadding(binding.clTop)

        initViews()
    }

    private fun initViews() {

        val selectedUnits = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("selected_units", SelectedUnit::class.java)
        } else {
            intent.getParcelableArrayListExtra("selected_units")
        }

        val quizCount = (intent.getFloatExtra("quiz_count", 20f)).toInt()

        binding.root.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putParcelableArrayListExtra("attempt", viewModel.attempt)
            baseViewModel.startActivityWithAnimation(intent, AnimationType.ZOOM)
        }

        viewModel.setQuizCount(quizCount)
        viewModel.setSelectedUnits(selectedUnits ?: emptyList())

        viewModel.prepareQuizzes()

        lifecycleScope.launch {
            viewModel.quizzes.collect { quizzes ->
                quizzes.forEach {
//                    binding.etQuizzes.setText("${binding.etQuizzes.text}\n$it")
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}