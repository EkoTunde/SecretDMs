package com.ekosoftware.secretdms.data.remote

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ekosoftware.secretdms.Constants.MESSAGE_PARAM_BODY
import com.ekosoftware.secretdms.Constants.MESSAGE_PARAM_SENDER
import com.ekosoftware.secretdms.Constants.MESSAGE_PARAM_TIMER_IN_MILLIS
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.MainActivity
import com.ekosoftware.secretdms.app.Notifier
import com.ekosoftware.secretdms.app.resources.Strings
import com.ekosoftware.secretdms.app.resources.TimeUnits.asTimeAndUnit
import com.ekosoftware.secretdms.domain.DefaultMessagesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class HandleReceivedMessageWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val defaultMessagesRepository: DefaultMessagesRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val from: String = inputData.getString(MESSAGE_PARAM_SENDER) ?: ""
        val body: String = inputData.getString(MESSAGE_PARAM_BODY) ?: ""
        val timerInMillis: String = inputData.getString(MESSAGE_PARAM_TIMER_IN_MILLIS) ?: "0"
        val messageId = inputData.getString("messageId") ?: ""

        val timerTuple = timerInMillis.toLong().asTimeAndUnit()
        val content = if (timerTuple == null) Strings.get(R.string.message_has_no_timer) else Strings.get(
            R.string.you_got_n_time_to_read,
            timerTuple.second,
            timerTuple.first
        )

        notifyUser(content)
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            saveInDatabase(from, body, timerInMillis.toLong(), messageId)
        }

        return Result.success()
    }

    private suspend fun saveInDatabase(friendId: String, body: String, timerInMillis: Long, messageId: String) {
        defaultMessagesRepository.saveMessage(friendId, body, timerInMillis, messageId)
        defaultMessagesRepository.newChat(friendId)
    }

    private fun notifyUser(content: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        Notifier.postNotification(applicationContext, Strings.get(R.string.new_message_received), content, pendingIntent)
    }

}
