package com.ekosoftware.secretdms.domain

import androidx.lifecycle.LiveData
import com.ekosoftware.secretdms.base.Resource
import com.ekosoftware.secretdms.data.local.LocalDataSource
import com.ekosoftware.secretdms.data.model.ChatPreview
import com.ekosoftware.secretdms.data.model.DIRECTION_SENT
import com.ekosoftware.secretdms.data.model.Message
import com.ekosoftware.secretdms.data.remote.NetworkDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.util.*
import javax.inject.Inject

@ActivityRetainedScoped
class DefaultMessagesRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource
) : MessagesRepository {
    override suspend fun clearDatabase() = localDataSource.clearDatabase()
    override fun getChats(): LiveData<List<ChatPreview>> = localDataSource.getChats()
    override fun getChatWithFriendId(friendId: String): LiveData<List<Message>> =
        localDataSource.getChatWithFriendId(friendId)

    override suspend fun insertDummyData() = localDataSource.insertDummyData()
    override suspend fun newChat(friendId: String) = localDataSource.newChat(friendId)
    override suspend fun sendMessage(
        friendId: String,
        body: String,
        destructionTimeInMillis: Long
    ): Resource<Boolean> {
        val messageId = UUID.randomUUID().toString()
        val message = Message(
            messageId,
            body,
            DIRECTION_SENT,
            friendId,
            destructionTimeInMillis,
            createdInMillis = Date().time
        )
        localDataSource.insertMessage(message)
        val result = networkDataSource.sendMessage(message)
        localDataSource.updateMessagesTime(message.apply {
            sentInMillis = result
        })
        return Resource.Success(true)
    }
}