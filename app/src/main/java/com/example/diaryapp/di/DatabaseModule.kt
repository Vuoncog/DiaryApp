package com.example.diaryapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.diaryapp.connectivity.NetworkConnectivityObserver
import com.example.diaryapp.data.database.ImageDatabase
import com.example.diaryapp.utility.Constant.IMAGE_DATABASE
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
    fun providesDatabase(
        @ApplicationContext context: Context
    ): ImageDatabase = Room.databaseBuilder(
        context = context,
        klass = ImageDatabase::class.java,
        name = IMAGE_DATABASE
    ).build()

    @Provides
    @Singleton
    fun providesImageToUploadDao(database: ImageDatabase) = database.imageToUploadDao()

    @Provides
    @Singleton
    fun providesImageToDeleteDao(database: ImageDatabase) = database.imageToDeleteDao()

    @Provides
    @Singleton
    fun providesConnectivity(
        @ApplicationContext context: Context
    ) = NetworkConnectivityObserver(context)
}