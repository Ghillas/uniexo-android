package com.unicofrance.uniexo.data.local.database

import android.content.Context
import com.unicofrance.uniexo.data.local.database.entities.Container
import java.sql.Timestamp

class CSVData {


    /*
    *  parse container data from csv file
    *
    * */
    fun parseCsvFromAssets(context: Context): List<Container> {
        val csvRegex = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex()
        return context.assets.open(FILENAME).bufferedReader().useLines { lines ->
            lines
                .drop(1)
                .filter { it.isNotBlank() }
                .map{ line ->
                    val content = csvRegex.split(line)
                    Container(
                        id = content[0],
                        longitude = content[1].toDouble(),
                        latitude = content[2].toDouble(),
                        label = content[3],
                        producingPlaceLabel = content[4],
                        description = content[5].substring(1,content[5].length-1),
                        streamLabel = content[6],
                        streamColor = content[7],
                        iconUrl = content[8],
                        creationDatetime = Timestamp.valueOf(content[9]).time
                    )
                }.toList()
        }
    }

    companion object {
        const val FILENAME = "containers.csv"

    }
}