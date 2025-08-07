package uz.alien.dictup.data.local.room.word

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String,
    val transcription: String = "",
    val type: String = "",
    val definition: String = "",
    val sentence: String = "",
    val collectionId: Int,
    val partId: Int,
    val unitId: Int
)