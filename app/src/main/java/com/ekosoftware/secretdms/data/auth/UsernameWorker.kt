package com.ekosoftware.secretdms.data.auth

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await

class UsernameWorker constructor(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val uid: String = inputData.getString("uid") ?: return Result.failure(
            workDataOf("exception" to Exception("User must be logged in"))
        )
        val snapshot: DocumentSnapshot = Authentication.userDocument(uid).await()
        return if (!snapshot.exists() || snapshot.getString("username").isNullOrEmpty()) {
            Result.success(workDataOf("username" to null))
        } else {
            val username: String = snapshot.getString("username")!!
            Authentication.username = username
            Result.success(workDataOf("username" to username))
        }

    }
}
