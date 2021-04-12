package com.ekosoftware.secretdms.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.Strings
import kotlin.random.Random

/**
 * Utility class for posting notifications.
 * This class creates the notification channel (as necessary) and posts to it when requested.
 */
object Notifier {

    const val CHANNEL_ID = "Default"

    fun postNotification(
        context: Context,
        title: String,
        content: String,
        intent: PendingIntent,
        priority: Int = NotificationCompat.PRIORITY_HIGH,
        autoCancel: Boolean = true
    ) {

        val notificationManager = App.instance.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_secretdms_24)
            .setPriority(priority)
            .setContentIntent(intent)
            .setAutoCancel(autoCancel)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val name = Strings.get(R.string.defaultChannelName)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = Strings.get(R.string.notificationDescription)
            }
        notificationManager.createNotificationChannel(channel)
    }
}
