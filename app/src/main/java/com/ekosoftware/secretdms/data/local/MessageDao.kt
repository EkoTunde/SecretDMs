package com.ekosoftware.secretdms.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ekosoftware.secretdms.base.BaseDao
import com.ekosoftware.secretdms.data.model.ChatPreview
import com.ekosoftware.secretdms.data.model.Message

@Dao
interface MessageDao : BaseDao<Message> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDummyData(vararg message: Message)

    /**
     * Returns a [LiveData] [List] containing Chat Previews
     */
    @Query(
        """
        SELECT 
            chatId AS friendId,
            COUNT(friendId) AS unreadMessages,
            MIN(messages.timer) AS minDestructionTime,
            MAX(messages.sentInMillis) AS lastMessageTime
        FROM chats
        LEFT JOIN messages ON friendId = chatId
        GROUP BY chatId
    """
    )
    fun liveChats(): LiveData<List<ChatPreview>>

    @Query("SELECT * FROM messages WHERE friendId = :friendId ORDER BY sentInMillis ASC")
    fun liveMessagesWithFriendId(friendId: String): LiveData<List<Message>>

    @Query("DELETE FROM messages")
    suspend fun clearDatabase()
}