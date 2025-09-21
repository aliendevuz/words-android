package uz.alien.dictup.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val collectionId: Int,
    val partId: Int,
    val unitId: Int,
    val nativeWordId: Int,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0
)