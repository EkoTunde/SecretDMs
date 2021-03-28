package com.ekosoftware.secretdms.injection

import android.content.Context
import androidx.room.Room
import com.ekosoftware.secretdms.app.Constants.DATABASE_NAME
import com.ekosoftware.secretdms.data.local.AppDatabase
import com.ekosoftware.secretdms.data.local.ChatDao
import com.ekosoftware.secretdms.data.local.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRoomInstance(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao = database.messageDao()

    @Singleton
    @Provides
    fun provideChatDao(database: AppDatabase): ChatDao = database.chatDao()

}