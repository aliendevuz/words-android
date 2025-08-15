package uz.alien.dictup.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Score(
    val id: Int,
    val userId: Int,
    val wordId: Int,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0
): Parcelable