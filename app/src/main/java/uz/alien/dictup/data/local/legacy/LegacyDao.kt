package uz.alien.dictup.data.local.legacy

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LegacyDao {

    @Query("SELECT id, level FROM word_table")
    suspend fun getLevels(): List<LegacyScore>

    @Query("SELECT * FROM word_table")
    suspend fun getWords(): List<Word>

    @Query("DELETE FROM word_table")
    suspend fun clear()
}