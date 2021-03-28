package com.ekosoftware.secretdms.domain

import androidx.lifecycle.LiveData
import com.ekosoftware.secretdms.base.Resource
import com.ekosoftware.secretdms.data.model.ChatPreview
import com.ekosoftware.secretdms.data.model.Message
import kotlinx.coroutines.Deferred

interface MessagesRepository {
    fun getChats(): LiveData<List<ChatPreview>>
    fun getChatWithFriendId(friendId: String): LiveData<List<Message>>
    suspend fun insertDummyData()
    suspend fun newChat(friendId: String)
    suspend fun sendMessage(friendId: String, body: String, destructionTimeInMillis: Long): Resource<Boolean>
    suspend fun clearDatabase()
}