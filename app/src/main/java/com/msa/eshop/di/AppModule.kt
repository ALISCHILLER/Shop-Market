package com.msa.eshop.di

import android.content.Context
import com.msa.eshop.utils.map.location.PiLocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun providePiLocationManager(@ApplicationContext context: Context): PiLocationManager {
        return PiLocationManager(context)
    }
}