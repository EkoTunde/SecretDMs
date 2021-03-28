package com.ekosoftware.secretdms.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ekosoftware.secretdms.base.BaseDao
import com.ekosoftware.secretdms.data.model.Chat

@Dao
interface ChatDao : BaseDao<Chat> {

    @Query("DELETE FROM chats")
    suspend fun clearDatabase()

    @Query("SELECT * FROM chats WHERE chatId = :chatId")
    suspend fun chatExists(chatId: String): Chat?
}