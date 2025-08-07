package uz.alien.dictup.data.local.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import uz.alien.dictup.data.local.room.entity.UserEntity
import uz.alien.dictup.data.local.room.entity.WordEntity

@Entity(
    tableName = "attempts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("userId"), Index("wordId")]
)
data class AttemptEntity(
    @PrimaryKey(autoGenerate = false)
    val timestamp: Long,
    val userId: Int,
    val wordId: Int,
    val isCorrect: Boolean
)