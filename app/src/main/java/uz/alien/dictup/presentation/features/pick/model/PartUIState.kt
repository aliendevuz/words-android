package uz.alien.dictup.presentation.features.pick.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PartUIState(
    val id: Int,
    val unitCount: Int,
    val isCurrent: Boolean = false
) : Parcelable