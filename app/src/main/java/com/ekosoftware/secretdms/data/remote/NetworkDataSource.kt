package com.ekosoftware.secretdms.data.remote

import android.util.Log
import com.ekosoftware.secretdms.app.Constants.MESSAGES
import com.ekosoftware.secretdms.data.auth.Authentication
import com.ekosoftware.secretdms.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap

@Singleton
class NetworkDataSource @Inject constructor() {

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    suspend fun postMessage(message: Message): Long {
        val timestamp = Calendar.getInstance().timeInMillis
        val data: HashMap<String, Any?> = hashMapOf(
            MESSAGE_PARAM_SENDER to Authentication.username!!,
            MESSAGE_PARAM_TO to message.friendId,
            MESSAGE_PARAM_BODY to message.body,
            MESSAGE_PARAM_TIMER_IN_MILLIS to message.timerInMillis.toString(),
            MESSAGE_PARAM_TIMESTAMP to message.timestamp
        )
        firestore.collection(MESSAGES)
            .add(data).await()
        return timestamp
    }

    suspend fun notifyMessageReceived(id: String) {
        firestore.collection(MESSAGES).document(id).delete().addOnFailureListener {
            Log.d("SecretDMsNetworkDS", "notifyMessageReceived ERROR: $it")
        }.await()
    }

    companion object {
        const val MESSAGE_PARAM_SENDER = "sender"
        const val MESSAGE_PARAM_TO = "addressee"
        const val MESSAGE_PARAM_BODY = "body"
        const val MESSAGE_PARAM_TIMER_IN_MILLIS = "timerInMillis"
        const val MESSAGE_PARAM_TIMESTAMP = "timestamp"
    }
}