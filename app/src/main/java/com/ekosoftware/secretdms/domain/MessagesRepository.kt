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
    suspend fun sendMessage(friendId: String, body: String, timerInMillis: Long): Resource<Boolean>
    suspend fun saveMessage(friendId: String, body: String, timerInMillis: Long, messageId: String)
    suspend fun clearData()
    suspend fun deleteChats(chatsForDeletion: List<ChatPreview>?)
    suspend fun deleteMessages(messagesForDeletion: List<Message>?)
    suspend fun updateTimers(friendId: String)
}