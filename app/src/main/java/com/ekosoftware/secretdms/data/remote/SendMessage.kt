package com.ekosoftware.secretdms.data.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SendMessage(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return Result.success()
    }

    private suspend fun saveInDb() {

    }

    private suspend fun uploadToServer() {

    }
}