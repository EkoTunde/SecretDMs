package com.ekosoftware.secretdms.data.remote

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import javax.inject.Inject

class NotifyReceivedWorker constructor(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {

        notifyReceived()

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    fun notifyReceived() {

    }
}
