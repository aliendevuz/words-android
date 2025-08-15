package uz.alien.dictup.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val id: Int? = null,
    val title: String,
    val content: String,
    val collectionId: Int? = null,
    val partId: Int? = null,
    val unitId: Int? = null
): Parcelable