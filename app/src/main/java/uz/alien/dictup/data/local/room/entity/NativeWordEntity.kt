package uz.alien.dictup.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import uz.alien.dictup.data.local.room.entity.WordEntity

@Entity(tableName = "native_words")
data class NativeWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String,
    val transcription: String = "",
    val type: String = "",
    val definition: String = "",
    val sentence: String = "",
    val collectionId: Int,
    val partId: Int,
    val unitId: Int,
    val nativeLanguage: String
)