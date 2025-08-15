package uz.alien.dictup.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Word(
    val id: Int? = null,
    val word: String,
    val transcription: String,
    val type: String,
    val definition: String,
    val sentence: String,
    val collectionId: Int? = null,
    val partId: Int? = null,
    val unitId: Int? = null
): Parcelable