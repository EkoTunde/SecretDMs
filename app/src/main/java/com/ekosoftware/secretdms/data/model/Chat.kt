package com.ekosoftware.secretdms.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "chatId")
    val friendId: String,
)
