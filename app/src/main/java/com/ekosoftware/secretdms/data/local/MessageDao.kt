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
            (SELECT COUNT(*) FROM messages WHERE NOT read AND direction = 2 AND friendId = chatId) AS unreadMessages,
            MIN(messages.timer) AS minTimerInMillis,
            MAX(messages.timestamp) AS lastMessageTimestamp
        FROM chats
        LEFT JOIN messages ON friendId = chatId
        GROUP BY chatId
    """
    )
    fun liveChats(): LiveData<List<ChatPreview>>

    @Query("SELECT * FROM messages WHERE friendId = :friendId ORDER BY timestamp ASC")
    fun liveMessagesWithFriendId(friendId: String): LiveData<List<Message>>

    @Query("DELETE FROM messages WHERE friendId =:friendId")
    suspend fun deleteWithFriendId(friendId: String)

    @Query("DELETE FROM messages")
    suspend fun clearDatabase()

    @Query("UPDATE messages SET timer = timer - 1000 WHERE friendId = :friendId AND direction = 2 AND timer IS NOT NULL AND TIMER > 0")
    suspend fun updateTimer(friendId: String)

    @Query("DELETE FROM messages WHERE friendId = :friendId AND (timer IS NULL OR TIMER = 0) AND direction = 2 ")
    suspend fun deleteMessagesWithFinishedTimers(friendId: String)

    /*@Query(
        """
        SELECT 
            chatId AS friendId,
            (SELECT COUNT(*) FROM messages WHERE readInMillis IS NULL AND direction = 2 AND friendId = chatId) AS unreadMessages,
            MIN(messages.timer) AS minDestructionTime,
            CASE WHEN direction = 1 THEN MAX(messages.sentInMillis) ELSE MAX(messages.receivedInMillis) END lastMessageTime
        FROM chats
        LEFT JOIN messages ON friendId = chatId
        GROUP BY chatId
    """
    )
    fun liveChats3(): LiveData<List<ChatPreview>>
    */
}