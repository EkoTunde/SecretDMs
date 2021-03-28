package com.ekosoftware.secretdms.data.remote

import com.ekosoftware.secretdms.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDataSource @Inject constructor() {

    private val db by lazy { FirebaseFirestore.getInstance() }

    suspend fun sendMessage(message: Message): Long {
        val timestamp = Calendar.getInstance().timeInMillis
        val friendMessagesRef =
            db.collection("users")
                .document(message.friendId)
                .collection("messages")
                .document(message.id)
        friendMessagesRef.set(
            hashMapOf(
                "body" to message.body,
                "destructionTime" to message.timerInMillis,
                "sentInMillis" to timestamp
            )
        ).await()
        return timestamp
    }
}