package com.ekosoftware.secretdms.data.model

data class ChatPreview(
    val friendId: String? = null,
    val unreadMessages: Int = 0,
    val minTimerInMillis: Long? = 0L,
    val lastMessageTimestamp: Long? = 0L
)
