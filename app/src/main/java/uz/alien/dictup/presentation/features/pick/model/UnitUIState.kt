package uz.alien.dictup.presentation.features.pick.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnitUIState(
    val id: Int,
    val name: String,
    val progress: Int,
    val partId: Int
) : Parcelable