package com.example.myapplication.di

import android.content.Context
import com.example.core.repository.network.TestAPIs
import com.example.core.repository.network.TestService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModuleProvider {
    companion object {
        private const val MY_APP = "MY_APP"
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context) =
        context.getSharedPreferences(MY_APP, Context.MODE_PRIVATE)

    @Provides
    fun provideService(
        service: TestService
    ): TestAPIs = service.providedService()
}