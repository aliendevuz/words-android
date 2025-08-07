package uz.alien.dictup.data.local.room.native_word

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import uz.alien.dictup.data.local.room.word.WordEntity

@Entity(
    tableName = "native_words",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("wordId")]
)
data class NativeWordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String,
    val transcription: String = "",
    val type: String = "",
    val definition: String = "",
    val sentence: String = "",
    val wordId: Int,
    val nativeLanguage: String
)