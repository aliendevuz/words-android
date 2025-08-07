package uz.alien.dictup.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String = "",
    val collectionId: Int,
    val partId: Int,
    val unitId: Int
)