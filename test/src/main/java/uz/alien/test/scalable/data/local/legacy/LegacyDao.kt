package uz.alien.test.scalable.data.local.legacy

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LegacyDao {

    @Query("SELECT id, level FROM word_table")
    suspend fun getLevels(): List<LegacyScore>

    @Query("DELETE FROM word_table")
    suspend fun clearLevels()
}