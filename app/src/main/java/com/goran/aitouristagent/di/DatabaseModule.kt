package com.goran.aitouristagent.di

import android.content.Context
import androidx.room.Room
import com.goran.aitouristagent.data.local.AppDatabase
import com.goran.aitouristagent.data.local.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "ai-tourist-agent.db").build()

    @Provides
    fun provideTripDao(database: AppDatabase): TripDao = database.tripDao()
}
