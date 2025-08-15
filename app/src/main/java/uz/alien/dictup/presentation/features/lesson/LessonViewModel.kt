package uz.alien.dictup.presentation.features.lesson

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uz.alien.dictup.domain.model.NativeWord
import uz.alien.dictup.domain.model.Score
import uz.alien.dictup.domain.model.Word
import uz.alien.dictup.presentation.features.lesson.model.WordUIState
import uz.alien.dictup.shared.WordCollection
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor() : ViewModel() {

    private val emptyWordList = emptyList<WordUIState>()

    private val _words = MutableStateFlow(emptyWordList)
    val words = _words.asStateFlow()

    fun getWords(words: ArrayList<Word>, nativeWords: ArrayList<NativeWord>, scores: ArrayList<Score>) {

        val updatedList = _words.value.toMutableList()

        words.forEachIndexed { index, word ->
            val nativeWord = nativeWords[index]
            val score = scores[index].correctCount - scores[index].incorrectCount

            updatedList += WordUIState(
                id = index,
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
}