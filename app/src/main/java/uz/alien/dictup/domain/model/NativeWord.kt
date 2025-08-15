package uz.alien.dictup.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NativeWord(
    val id: Int? = null,
    val word: String,
    val transcription: String,
    val type: String,
    val definition: String,
    val sentence: String,
    val wordId: Int? = null,
    val nativeLanguage: String
): Parcelable