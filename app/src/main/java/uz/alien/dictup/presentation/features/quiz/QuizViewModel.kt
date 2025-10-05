package uz.alien.dictup.presentation.features.quiz

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.model.Quiz
import uz.alien.dictup.domain.model.SelectedUnit
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.WordRepository
import uz.alien.dictup.domain.usecase.PrepareQuizzesUseCase
import uz.alien.dictup.presentation.common.model.Attempt
import uz.alien.dictup.utils.Logger
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val prepareQuizzesUseCase: PrepareQuizzesUseCase,
    private val wordRepository: WordRepository,
    private val nativeWordRepository: NativeWordRepository
) : ViewModel() {

    private var quizCount = 20
    private val currentIndex = mutableIntStateOf(-1)

    private val _question = MutableStateFlow("")
    val question = _question.asStateFlow()

    private val _options = MutableStateFlow<List<String>>(emptyList())
    val options = _options.asStateFlow()

    private val selectedUnits = mutableListOf<SelectedUnit>()
    val quizzes = MutableStateFlow<List<Quiz>>(emptyList())

    val attempt = ArrayList<Attempt>()

    fun setQuizCount(count: Int) {
        quizCount = count
    }

    fun setSelectedUnits(units: List<SelectedUnit>) {
        selectedUnits.addAll(units)
    }

    fun prepareQuizzes() {
        if (selectedUnits.isNotEmpty()) {
            viewModelScope.launch {
                quizzes.value = prepareQuizzesUseCase(selectedUnits, quizCount, 7)
            }
        }
    }
}