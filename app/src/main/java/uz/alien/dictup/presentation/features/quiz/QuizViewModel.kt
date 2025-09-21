package uz.alien.dictup.presentation.features.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.model.SelectedUnit
import uz.alien.dictup.domain.usecase.PrepareQuizzesUseCase
import uz.alien.dictup.utils.Logger
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val prepareQuizzesUseCase: PrepareQuizzesUseCase
) : ViewModel() {

    private var quizCount = 20
    private val selectedUnits = mutableListOf<SelectedUnit>()

    fun setQuizCount(count: Int) {
        quizCount = count
    }

    fun setSelectedUnits(units: List<SelectedUnit>) {
        selectedUnits.addAll(units)
    }

    fun prepareQuizzes() {
        val currentTime = System.currentTimeMillis()
        if (selectedUnits.isNotEmpty()) {
            viewModelScope.launch {
//                val quizzes = prepareQuizzesUseCase(0, 7, quizCount, selectedUnits)
//                quizzes.forEach { quiz ->
//                    Logger.d(QuizViewModel::class.java.simpleName, "$quiz")
//                }
                Logger.d(QuizViewModel::class.java.simpleName, "Elapsed time: ${System.currentTimeMillis() - currentTime}")
            }
        }
    }
}