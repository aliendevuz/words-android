package uz.alien.dictup.presentation.features.lesson.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import uz.alien.dictup.domain.model.WordCollection

@Parcelize
data class WordUIState(
    val id: Int,
    val word: String,
    val transcription: String,
    val type: String,
    val definition: String,
    val sentence: String,
    val nativeWord: String,
    val nativeTranscription: String,
    val nativeType: String,
    val nativeDefinition: String,
    val nativeSentence: String,
    val score: Int,
    val imageSource: String = "",
    val collection: String = ""
): Parcelable