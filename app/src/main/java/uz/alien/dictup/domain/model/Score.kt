package uz.alien.dictup.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Score(
    val id: Int,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val collectionId: Int? = null,
    val partId: Int? = null,
    val unitId: Int? = null,
    val nativeWordId: Int? = null
): Parcelable