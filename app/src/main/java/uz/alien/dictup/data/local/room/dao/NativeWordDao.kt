package uz.alien.dictup.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import uz.alien.dictup.data.local.room.entity.NativeWordEntity

@Dao
interface NativeWordDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNativeWord(nativeWord: NativeWordEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNativeWords(nativeWords: List<NativeWordEntity>)

    @Update
    suspend fun updateNativeWord(nativeWord: NativeWordEntity)

    @Delete
    suspend fun deleteNativeWord(nativeWord: NativeWordEntity)

    @Query("SELECT * FROM native_words WHERE id = :id")
    suspend fun getNativeWordById(id: Int): NativeWordEntity?

    @Query("SELECT * FROM native_words WHERE wordId = :wordId")
    suspend fun getNativeWordsByWordId(wordId: Int): List<NativeWordEntity>

    @Query("SELECT * FROM native_words")
    suspend fun getAllNativeWords(): List<NativeWordEntity>

    @Query("DELETE FROM native_words")
    suspend fun clearAllNativeWords()
}