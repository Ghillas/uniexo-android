package com.unicofrance.uniexo.data.local.database.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContainerDao {
    @Query("SELECT * FROM container")
    fun getAll(): Flow<List<Container>>

    @Query("SELECT COUNT(*) FROM container")
    suspend fun getTotalContainerNumber(): Int

    @Insert
    suspend fun insert(container: Container)

    @Query("""DELETE FROM container""")
    suspend fun deleteAll()
}