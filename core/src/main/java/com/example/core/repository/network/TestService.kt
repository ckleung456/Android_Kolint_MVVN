package com.example.core.repository.network

import com.example.core.BuildConfig
import com.example.core.BuildConfig.SERVER_ENDPOINT
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestService @Inject constructor(
    private val flowErrorHandlingCallAdapterFactory: FlowErrorHandlingCallAdapterFactory
) {
    fun providedService(): TestAPIs = Retrofit.Builder()
        .client(
            OkHttpClient.Builder().apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                }
            }
                .build()
        )
        .baseUrl("$SERVER_ENDPOINT/")
        .addCallAdapterFactory(flowErrorHandlingCallAdapterFactory)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()
        .create(TestAPIs::class.java)
}