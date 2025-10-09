package uz.alien.dictup.presentation.features.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.domain.repository.DataStoreRepository
import uz.alien.dictup.presentation.features.lesson.model.WordUIState
import uz.alien.dictup.domain.model.WordCollection
import uz.alien.dictup.domain.repository.room.ScoreRepository
import uz.alien.dictup.infrastructure.manager.TTSManager
import uz.alien.dictup.utils.Logger
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val ttsManager: TTSManager,
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val emptyWordList = emptyList<WordUIState>()

    private val _words = MutableStateFlow(emptyWordList)
    val words = _words.asStateFlow()

    fun speakAloud(text: String, onDone: (() -> Unit)? = null) {
        ttsManager.speak(text.replace(".", " "), onDone)
    }

    fun getWords(words: ArrayList<Word>, nativeWords: ArrayList<NativeWord>, scores: ArrayList<Score>) {

        val updatedList = _words.value.toMutableList()

        words.forEachIndexed { index, word ->
            val nativeWord = nativeWords[index]
            val score = scores[index].correctCount - scores[index].incorrectCount

            updatedList += WordUIState(
                id = index,
                wordId = word.id,
                word = word.word,
                transcription = word.transcription,
                type = word.type,
                definition = word.definition,
                sentence = word.sentence,
                nativeWord = nativeWord.word,
                nativeTranscription = nativeWord.transcription,
                nativeType = nativeWord.type,
                nativeDefinition = nativeWord.definition,
                nativeSentence = nativeWord.sentence,
                score = score,
                imageSource = "${word.partId}/${word.unitId}/$index",
                collection = WordCollection.fromId(word.collectionId ?: -1)?.key ?: ""
            )
        }

        _words.value = updatedList
    }

    fun setContent(content: String): String {
        return content.replace("<br>", " ").trim()
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

    fun updateLessonProgress() {
        viewModelScope.launch {
            _words.update { list ->
                list.mapIndexed { index, wordUi ->
                    val score = scoreRepository.getScoreById(wordUi.wordId ?: return@launch)
                    val newScore = score?.let { it.correctCount - it.incorrectCount } ?: wordUi.score

                    wordUi.copy(score = newScore)
                }
            }
        }
    }
}