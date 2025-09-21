package uz.alien.dictup.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectedUnit(
    val collectionId: Int,
    val partId: Int,
    val unitId: Int
) : Parcelable