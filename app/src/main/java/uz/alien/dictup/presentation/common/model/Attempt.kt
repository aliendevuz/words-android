package uz.alien.dictup.presentation.common.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Attempt(
    val quizId: Int,
    val isCorrect: Boolean,
    val timestamp: Long
) : Parcelable
