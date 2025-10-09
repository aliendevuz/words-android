package uz.alien.dictup.presentation.features.quiz

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.model.Quiz
import uz.alien.dictup.domain.model.SelectedUnit
import uz.alien.dictup.domain.repository.room.NativeWordRepository
import uz.alien.dictup.domain.repository.room.WordRepository
import uz.alien.dictup.domain.usecase.PrepareQuizzesUseCase
import uz.alien.dictup.presentation.common.model.Attempt
import uz.alien.dictup.presentation.features.quiz.model.Option
import uz.alien.dictup.presentation.features.quiz.model.Status
import uz.alien.dictup.utils.Logger
import javax.inject.Inject
import kotlin.random.Random.Default.nextBoolean

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val prepareQuizzesUseCase: PrepareQuizzesUseCase,
    private val wordRepository: WordRepository,
    private val nativeWordRepository: NativeWordRepository
) : ViewModel() {

    enum class Language {
        TARGET,
        NATIVE
    }

    val quizCount = mutableIntStateOf(20)
    val currentIndex = MutableStateFlow(-1)
    private var currentLanguage = Language.TARGET

    val isCorrect = MutableStateFlow(false)


    private val _question = MutableStateFlow("")
    val question = _question.asStateFlow()

    private val _questionId = MutableStateFlow(0)
    val questionId = _questionId.asStateFlow()

    private val _options = MutableStateFlow<List<Option>>(emptyList())
    val options = _options.asStateFlow()

    private val selectedUnits = mutableListOf<SelectedUnit>()
    val quizzes = MutableStateFlow<List<Quiz>>(emptyList())

    val attempt = ArrayList<Attempt>()

    fun setQuizCount(count: Int) {
        quizCount.intValue = count
    }

    fun setSelectedUnits(units: List<SelectedUnit>) {
        selectedUnits.addAll(units)
    }

    fun resetQuiz() {
        currentIndex.value = -1
        attempt.clear()
        isCorrect.value = false
        _options.value = emptyList()
        _question.value = ""
        quizzes.value = emptyList()
        _questionId.value = 0
        currentLanguage = Language.TARGET
    }

    fun prepareQuizzes() {
        if (selectedUnits.isNotEmpty()) {
            viewModelScope.launch {
                quizzes.value = prepareQuizzesUseCase(selectedUnits, quizCount.intValue, 7)
                if (currentIndex.value == -1) nextQuestion()
            }
        }
    }

    fun nextQuestion() {
        viewModelScope.launch {
            currentIndex.value++
            isCorrect.value = false
            currentLanguage = if (nextBoolean()) Language.TARGET else Language.NATIVE
            if (currentIndex.value < quizzes.value.size) {
                val quiz = quizzes.value[currentIndex.value]
                _question.value = getQuestion()
                _questionId.value = quizzes.value[currentIndex.value].quiz
                _options.value = getOptions(quiz.options)
                Logger.d(_options.value.toString())
            } else {
                _question.value = ""
                _options.value = emptyList()
            }
        }
    }

    fun answer(id: Int, wordId: Int): Boolean {
        val correctId = quizzes.value[currentIndex.value].quiz
        val isCorrect = wordId == correctId

        this.isCorrect.value = isCorrect

        _options.update { list ->
            list.mapIndexed { _, option ->
                when {
                    option.id == id && isCorrect -> option.copy(status = Status.CORRECT)
                    option.id == id && !isCorrect -> option.copy(status = Status.WRONG)
                    else -> option
                }
            }
        }

        attempt.add(Attempt(questionId.value, isCorrect, System.currentTimeMillis()))

        return isCorrect
    }

    suspend fun getQuestion(): String {
        if (currentLanguage == Language.TARGET) {
            return wordRepository.getWordById(quizzes.value[currentIndex.value].quiz)?.word ?: ""
        } else {
            return nativeWordRepository.getNativeWordById(quizzes.value[currentIndex.value].quiz)?.word ?: ""
        }
    }

    suspend fun getOptions(ids: List<Int>): List<Option> {
        val options = mutableListOf<Option>()

        if (currentLanguage == Language.TARGET) {
            ids.forEachIndexed { index, id ->
                options.add(
                    Option(index, id, nativeWordRepository.getNativeWordById(id)?.word ?: "")
                )
            }
        } else {
            ids.forEachIndexed { index, id ->
                options.add(
                    Option(index, id, wordRepository.getWordById(id)?.word ?: "")
                )
            }
        }
        return options
    }
}