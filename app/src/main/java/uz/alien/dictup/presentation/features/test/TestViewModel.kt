package uz.alien.dictup.presentation.features.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.usecase.home.MainUseCases
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(
    private val mainUseCases: MainUseCases
): ViewModel() {

    private val _text = MutableStateFlow("")
    val text: MutableStateFlow<String> = _text

    fun getData() {

        viewModelScope.launch {

//            val words = mainUseCases.getAllWordsUseCase()
//
//            if (words.isEmpty()) {
//                _text.value += "No data for words"
//            } else {
//                _text.value += "${words.size}\n"
//                _text.value += "${words.first()}\n"
//                _text.value += "${words.last()}\n"
//            }
//            _text.value += "\n"
//
//            val stories = mainUseCases.getAllStoryUseCase()
//
//            if (stories.isEmpty()) {
//                _text.value += "No data for stories"
//            } else {
//                _text.value += "${stories.size}\n"
//                _text.value += "${stories.first()}\n"
//                _text.value += "${stories.last()}\n"
//            }
//            _text.value += "\n"
//
//            val nativeWords = mainUseCases.getAllNativeWordUseCase()
//
//            if (nativeWords.isEmpty()) {
//                _text.value += "No data for native words"
//            } else {
//                _text.value += "${nativeWords.size}\n"
//                _text.value += "${nativeWords.first()}\n"
//                _text.value += "${nativeWords.last()}\n"
//            }
//            _text.value += "\n"
//
//            val nativeStories = mainUseCases.getAllNativeStoryUseCase()
//
//            if (nativeStories.isEmpty()) {
//                _text.value += "No data for native stories"
//            } else {
//                _text.value += "${nativeStories.size}\n"
//                _text.value += "${nativeStories.first()}\n"
//                _text.value += "${nativeStories.last()}\n"
//            }
//            _text.value += "\n"

            val scores = mainUseCases.getScoreOfBeginnerUseCase()

            if (scores.isEmpty()) {
                _text.value += "No data for scores"
            } else {
//                scores.forEach { score ->
//                    _text.value += "${score.correctCount} ${score.incorrectCount}\n"
//                }
            }
        }
    }
}