package com.ekosoftware.secretdms.data.model

data class ChatPreview(
    val friendId: String? = null,
    val unreadMessages: Int = 0,
    val minDestructionTime: Long? = 0L,
    val lastMessageTime: Long? = 0L,
)
