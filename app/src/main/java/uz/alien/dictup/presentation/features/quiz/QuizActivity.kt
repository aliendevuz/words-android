package uz.alien.dictup.presentation.features.quiz

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.alien.dictup.databinding.QuizActivityBinding
import uz.alien.dictup.presentation.common.extention.applyExitZoomTransition
import uz.alien.dictup.presentation.common.extention.setClearEdge
import uz.alien.dictup.presentation.common.extention.setSystemPadding
import uz.alien.dictup.presentation.features.base.BaseActivity
import uz.alien.dictup.domain.model.SelectedUnit

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
        setSystemPadding(binding.root)

        initViews()
    }

    private fun initViews() {

        val selectedUnits = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("selected_units", SelectedUnit::class.java)
        } else {
            intent.getParcelableArrayListExtra("selected_units")
        }

        val quizCount = (intent.getFloatExtra("quiz_count", 20f)).toInt()

        viewModel.setQuizCount(quizCount)
        viewModel.setSelectedUnits(selectedUnits ?: emptyList())

        viewModel.prepareQuizzes()
    }

    override fun finish() {
        super.finish()
        applyExitZoomTransition()
    }
}