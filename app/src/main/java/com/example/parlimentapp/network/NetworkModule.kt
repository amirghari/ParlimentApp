package com.example.parlimentapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object NetworkModule {

    // Create the logging interceptor
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // Create OkHttpClient and add the interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Create Retrofit instance with a custom base URL
    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API service for the current base URL (https://users.metropolia.fi/)
    val apiService: ParliamentApiService = createRetrofit("https://users.metropolia.fi/")
        .create(ParliamentApiService::class.java)

    // API service for the new base URL (https://avoindata.eduskunta.fi/)
    val imageApiService: ParliamentApiService = createRetrofit("https://avoindata.eduskunta.fi/")
        .create(ParliamentApiService::class.java)
}


