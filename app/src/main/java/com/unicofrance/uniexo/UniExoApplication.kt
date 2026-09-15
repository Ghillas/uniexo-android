package com.unicofrance.uniexo

import android.app.Application
import androidx.room.Room
import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import com.unicofrance.uniexo.data.local.database.AppDatabase
import com.unicofrance.uniexo.data.local.database.CSVData
import com.unicofrance.uniexo.data.remote.Api
import com.unicofrance.uniexo.data.repositories.ContainerRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class UniExoApplication : Application() {

    private val api: Api by lazy {
        Retrofit.Builder()
            .baseUrl("https://placeholder-url.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResultCallAdapterFactory.create())
            .client(
                OkHttpClient().newBuilder()
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    .writeTimeout(10000L, TimeUnit.MILLISECONDS).build()
            )
            .build()
            .create(Api::class.java)
    }


    private val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java, "database"
        ).build()
    }

    val containerRepository by lazy {
        ContainerRepository(
            containerDao = database.containerDao(),
            csvData = CSVData(),
            context = applicationContext
        )
    }
}