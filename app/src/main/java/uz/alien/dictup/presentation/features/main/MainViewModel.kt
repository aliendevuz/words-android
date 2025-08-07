package uz.alien.dictup.presentation.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import uz.alien.dictup.core.utils.Logger
import uz.alien.dictup.domain.usecase.main.MainUseCases

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainUseCases: MainUseCases
) : ViewModel() {

    fun printWords() {
        viewModelScope.launch {

            val words = mainUseCases.getAllWordsUseCase()
            Logger.d("Words: ${words.size}")
            Logger.d("Words: ${words.first()}")
            Logger.d("Words: ${words.last()}")

            val stories = mainUseCases.getAllStoryUseCase()
            Logger.d("Stories: ${stories.size}")
            Logger.d("Stories: ${stories.first()}")
            Logger.d("Stories: ${stories.last()}")

            val nativeWords = mainUseCases.getAllNativeWordUseCase()
            Logger.d("Native Words: ${nativeWords.size}")
            Logger.d("Native Words: ${nativeWords.first()}")
            Logger.d("Native Words: ${nativeWords.last()}")

            val nativeStories = mainUseCases.getAllNativeStoryUseCase()
            Logger.d("Native Stories: ${nativeStories.size}")
            Logger.d("Native Stories: ${nativeStories.first()}")
            Logger.d("Native Stories: ${nativeStories.last()}")
        }
    }
}