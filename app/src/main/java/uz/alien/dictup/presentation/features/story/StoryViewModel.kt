package uz.alien.dictup.presentation.features.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.alien.dictup.utils.Logger
import uz.alien.dictup.utils.Logger.isCalled
import uz.alien.dictup.infrastructure.manager.TTSManager
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val ttsManager: TTSManager
) : ViewModel() {

    private val _sentences = MutableStateFlow<List<String>>(emptyList())
    val sentences = _sentences.asStateFlow()

    val currentIndex = MutableStateFlow(-1)
    val isPaused = MutableStateFlow(true)

    private val _speakRequest = MutableSharedFlow<String>()
    val speakRequest = _speakRequest.asSharedFlow()

    private fun pause() {
        ttsManager.stop()
        isPaused.value = true
    }

    fun setContent(content: String): String {
        _sentences.value = splitAndCleanContent(content)
        return content.replace("<br>", " ").trim()
    }

    fun toggle() {
        if (isPaused.value) resume() else pause()
    }

    private fun resume() {
        if (isPaused.value) {
            isPaused.value = false
            if (currentIndex.value == -1) {
                currentIndex.value++
            }
            cont()
        }
    }

    fun next() {
        if (currentIndex.value < _sentences.value.size - 1) {
            currentIndex.value++
            if (!isPaused.value) {
                requestSpeak()
            }
        } else {
            currentIndex.value = -1
            pause()
        }
    }

    fun cont() {
        if (!isPaused.value) {
            if (currentIndex.value < _sentences.value.size) {
                requestSpeak()
            } else {
                pause()
                currentIndex.value = -1
            }
        }
    }

    fun prev() {
        if (currentIndex.value <= 0) {
            pause()
        }
        if (currentIndex.value > -1) {
            currentIndex.value--
        } else {
            currentIndex.value = sentences.value.lastIndex
        }
        if (!isPaused.value) {
            cont()
        }
    }

    private fun requestSpeak() {
        val sentence = _sentences.value.getOrNull(currentIndex.value) ?: return
        viewModelScope.launch {
            _speakRequest.emit(sentence)
        }
    }

    fun speakAloud(text: String, onDone: (() -> Unit)? = null) {
        ttsManager.speak(text.replace(".", ""), onDone)
    }

    fun splitAndCleanContent(content: String): List<String> {
        val paragraphs = content.split("\n\n")

        val sentences = paragraphs.flatMap { paragraph ->
            when {
                "<br>" in paragraph -> paragraph.split("<br>")
                else -> paragraph.split(Regex("(?<=[.!?])\\s+"))
            }
        }

        return sentences
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filter { it.any { ch -> ch.isLetterOrDigit() } }
            .map { it.replace(Regex("\\s+"), " ") }
    }

    fun addTabsAfterParagraphs(content: String): String {
        return content
            .replace(Regex("(\n\n)(?! )"), "\n\n        ")
            .let { if (!it.startsWith("        ")) "        $it" else it }
    }
}