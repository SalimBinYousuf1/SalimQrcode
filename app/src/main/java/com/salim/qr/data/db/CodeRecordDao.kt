package com.salim.qr.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CodeRecordDao {

    @Query("SELECT * FROM code_records ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<CodeRecord>>

    @Query("SELECT * FROM code_records WHERE origin = :origin ORDER BY timestamp DESC")
    fun getByOriginFlow(origin: String): Flow<List<CodeRecord>>

    @Query("SELECT * FROM code_records WHERE parsedType = :type ORDER BY timestamp DESC")
    fun getByTypeFlow(type: String): Flow<List<CodeRecord>>

    @Query("SELECT * FROM code_records WHERE payload LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchFlow(query: String): Flow<List<CodeRecord>>

    @Query("SELECT * FROM code_records WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CodeRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: CodeRecord): Long

    @Update
    suspend fun update(record: CodeRecord)

    @Delete
    suspend fun delete(record: CodeRecord)

    @Query("DELETE FROM code_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM code_records")
    suspend fun clearAll()

    @Query("DELETE FROM code_records WHERE timestamp < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)
}
