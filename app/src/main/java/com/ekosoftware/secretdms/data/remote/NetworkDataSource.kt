package com.ekosoftware.secretdms.data.remote

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
            PARAM_SENDER to Authentication.username!!,
            PARAM_TO to message.friendId,
            PARAM_BODY to message.body,
            PARAM_TIMER_IN_MILLIS to message.timerInMillis.toString(),
            PARAM_LAST_TOKEN to FCMToken.token
        )
        firestore.collection(MESSAGES)
            .add(data).await()
        return timestamp
    }

    suspend fun notifyMessageReceived() : Long {
        return 0L
    }

    companion object {
        const val PARAM_SENDER = "sender"
        const val PARAM_TO = "addressee"
        const val PARAM_BODY = "body"
        const val PARAM_TIMER_IN_MILLIS = "timerInMillis"
        const val PARAM_LAST_TOKEN = "token"
    }
}