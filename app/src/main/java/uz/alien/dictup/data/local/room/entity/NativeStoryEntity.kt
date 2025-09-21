package uz.alien.dictup.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import uz.alien.dictup.data.local.room.entity.StoryEntity

@Entity(tableName = "native_stories")
data class NativeStoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String = "",
    val collectionId: Int,
    val partId: Int,
    val unitId: Int,
    val nativeLanguage: String
)