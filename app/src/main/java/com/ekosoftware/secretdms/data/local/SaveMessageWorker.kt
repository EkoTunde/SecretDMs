package com.ekosoftware.secretdms.data.local

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class SaveMessageWorker constructor(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {

        saveMessage()
        showNotification()

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun saveMessage() {

    }

    private fun showNotification() {

    }

}
