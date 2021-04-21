package com.ekosoftware.secretdms.domain

import androidx.lifecycle.LiveData
import com.ekosoftware.secretdms.base.Resource
import com.ekosoftware.secretdms.data.auth.Authentication
import com.ekosoftware.secretdms.data.local.LocalDataSource
import com.ekosoftware.secretdms.data.model.ChatPreview
import com.ekosoftware.secretdms.data.model.DIRECTION_RECEIVED
import com.ekosoftware.secretdms.data.model.DIRECTION_SENT
import com.ekosoftware.secretdms.data.model.Message
import com.ekosoftware.secretdms.data.remote.FCMToken
import com.ekosoftware.secretdms.data.remote.NetworkDataSource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMessagesRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
) : MessagesRepository {

    override fun getChats(): LiveData<List<ChatPreview>> = localDataSource.getChats()

    override fun getChatWithFriendId(friendId: String): LiveData<List<Message>> = localDataSource.getChatWithFriendId(friendId)

    override suspend fun insertDummyData() = localDataSource.insertDummyData()

    override suspend fun newChat(friendId: String) = localDataSource.newChat(friendId)

    override suspend fun sendMessage(
        friendId: String,
        body: String,
        timerInMillis: Long
    ): Resource<Boolean> {
        val messageId = UUID.randomUUID().toString()
        val message = Message(
            messageId,
            body,
            DIRECTION_SENT,
            friendId,
            timerInMillis,
            timestamp = Date().time,
            read = true
        )
        localDataSource.insertMessage(message)
        networkDataSource.postMessage(message)
        //localDataSource.updateMessagesStatus(message.apply { read = true })
        return Resource.Success(true)
    }

    override suspend fun saveMessage(friendId: String, body: String, timerInMillis: Long, messageId: String) {
        val message = Message(
            id = UUID.randomUUID().toString(),
            body = body,
            direction = DIRECTION_RECEIVED,
            friendId = friendId,
            timerInMillis = timerInMillis,
            timestamp = Date().time,
            read = false
        )
        localDataSource.insertMessage(message)
        networkDataSource.notifyMessageReceived(messageId)
    }

    override suspend fun deleteChats(chatsForDeletion: List<ChatPreview>?) {
        chatsForDeletion?.forEach {
            localDataSource.deleteChat(it.friendId!!)
            localDataSource.deleteMessagesWithFriendId(it.friendId)
        }
    }

    override suspend fun deleteMessages(messagesForDeletion: List<Message>?) {
        messagesForDeletion?.forEach {
            localDataSource.deleteMessagesWithFriendId(it.friendId)
        }
    }

    override suspend fun updateTimers(friendId: String) {
        localDataSource.updateTimersAncCleanDB(friendId)
    }

    override suspend fun clearData() {
        localDataSource.clearDatabase()
        FCMToken.clearData()
        Authentication.clearData()
    }
}