package uz.alien.dictup.presentation.features.select.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnitUIState(
    val id: Int,
    val name: String,
    val progress: Int,
    val collectionId: Int,
    val partId: Int,
    val isSelected: Boolean = false
) : Parcelable