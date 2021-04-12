package com.ekosoftware.secretdms.data.remote

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
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
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@HiltWorker
class SaveReceivedMessageWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val defaultMessagesRepository: DefaultMessagesRepository,
) : CoroutineWorker(appContext, workerParams) {

    private val TAG = "SaveReceivedMessageWork"
    override suspend fun doWork(): Result {

        val from = inputData.getString("sender") ?: ""
        val body = inputData.getString("body") ?: ""
        val timerInMillis = inputData.getString("timerInMillis") ?: "0"

        val timerTuple = timerInMillis.toLong().asTimeAndUnit()
        val content = if (timerTuple == null) Strings.get(R.string.message_has_no_timer) else Strings.get(
            R.string.you_got_n_time_to_read,
            timerTuple.second,
            timerTuple.first
        )

        Log.d(TAG, "doWork: attempting")
        notifyReceived(content)
        val save = CoroutineScope(Dispatchers.IO).async {
            Log.d(TAG, "doWork: SAVING")
            saveInDatabase(from, body, timerInMillis.toLong())
        }
        save.await()

        return Result.success()
    }

    private suspend fun saveInDatabase(friendId: String, body: String, timerInMillis: Long) {
        defaultMessagesRepository.saveMessage(friendId, body, timerInMillis)
        defaultMessagesRepository.newChat(friendId)
    }

    private fun notifyReceived(content: String) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        Notifier.postNotification(applicationContext, Strings.get(R.string.new_message_received), content, pendingIntent)
    }

}
