package com.unicofrance.uniexo.data.repositories

import android.content.Context
import com.unicofrance.uniexo.data.local.database.CSVData
import com.unicofrance.uniexo.data.local.database.entities.Container
import com.unicofrance.uniexo.data.local.database.entities.ContainerDao
import kotlinx.coroutines.flow.Flow

class ContainerRepository(
    private val containerDao: ContainerDao,
    private val csvData: CSVData,
    private val context: Context
) {

    /*
    * check if the DB is empty, and then fill the DB with the CSV Data
    *
    * */
    suspend fun getAll() : Flow<List<Container>>{
        if (
            getTotalContainerNumber() == 0
        ) {
            val containers = csvData.parseCsvFromAssets(context)
            containers.forEach {
                insert(it)
            }
        }
        return containerDao.getAll()
    }

    suspend fun getTotalContainerNumber() = containerDao.getTotalContainerNumber()

    suspend fun insert(container: Container) = containerDao.insert(container)

    suspend fun deleteAll() = containerDao.deleteAll()
}