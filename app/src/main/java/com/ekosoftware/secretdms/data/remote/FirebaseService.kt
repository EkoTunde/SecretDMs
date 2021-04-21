package com.ekosoftware.secretdms.data.remote

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {

    private val workManager by lazy { WorkManager.getInstance(this) }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Start worker to save data into database and notify user of new message!
        val pairs: Array<Pair<String, String>> = message.data.toList().toTypedArray()
        val saveDataRequest = OneTimeWorkRequestBuilder<HandleReceivedMessageWork>()
            .setInputData(workDataOf(*pairs))
            .build()
        workManager.enqueue(saveDataRequest)
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        FCMToken.token = newToken
    }
}
