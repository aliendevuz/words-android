package uz.alien.dictup.presentation.features.select.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PartUIState(
    val id: Int,
    val title: String,
    val collectionId: Int,
    val unitCount: Int,
    val isSelected: Boolean = false,
    val isCurrent: Boolean = false,
    val selectedUnitCount: Int = 0
) : Parcelable