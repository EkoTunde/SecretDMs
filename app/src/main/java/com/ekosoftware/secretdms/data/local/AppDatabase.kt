package com.ekosoftware.secretdms.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ekosoftware.secretdms.data.model.Chat
import com.ekosoftware.secretdms.data.model.Message

@Database(entities = [Message::class, Chat::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}