package uz.alien.dictup.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import uz.alien.dictup.data.local.room.entity.WordEntity

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Update
    suspend fun updateWord(word: WordEntity)

    @Delete
    suspend fun deleteWord(word: WordEntity)

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: Int): WordEntity?

    @Query("SELECT * FROM words")
    suspend fun getAllWords(): List<WordEntity>

    @Query("SELECT * FROM words WHERE collectionId = :collectionId AND partId = :partId AND unitId = :unitId")
    suspend fun getWordByFullPath(collectionId: Int, partId: Int, unitId: Int): List<WordEntity>

    @Query("DELETE FROM words")
    suspend fun clearAllWords()
}