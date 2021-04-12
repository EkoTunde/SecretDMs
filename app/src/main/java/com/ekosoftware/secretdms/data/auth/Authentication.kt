package com.ekosoftware.secretdms.data.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ekosoftware.secretdms.app.App
import com.ekosoftware.secretdms.app.Constants.USERS
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await


object Authentication {

    private const val TOKEN_PREF_KEY = "token pref key"
    private const val USER_EMAIL_PREF_KEY = "user email pref key"
    private const val USER_UID_PREF_KEY = "user uid pref key"
    private const val USER_NAME_PREF_KEY = "user name pref key"

    init {
        App.instance.getSharedPreferences("sharedPref", Context.MODE_PRIVATE).also { this.sharedPref = it }
    }

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val workManager = WorkManager.getInstance(App.instance)

    var sharedPref: SharedPreferences? = null

    var token: String?
        get() {
            return sharedPref?.getString(TOKEN_PREF_KEY, null)
        }
        set(value) {
            sharedPref?.edit()?.putString(TOKEN_PREF_KEY, value)?.apply()
        }

    private var uid: String?
        get() {
            return sharedPref?.getString(USER_UID_PREF_KEY, auth.currentUser?.uid)
        }
        set(value) {
            sharedPref?.edit()?.putString(USER_UID_PREF_KEY, value)?.apply()
        }

    var email: String?
        get() {
            return sharedPref?.getString(USER_EMAIL_PREF_KEY, auth.currentUser?.email)
        }
        set(value) {
            sharedPref?.edit()?.putString(USER_EMAIL_PREF_KEY, value)?.apply()
        }

    var username: String?
        get() {
            return sharedPref?.getString(USER_NAME_PREF_KEY, null)
        }
        set(value) {
            value?.let { FirebaseMessaging.getInstance().subscribeToTopic(it) }
            sharedPref?.edit()?.putString(USER_NAME_PREF_KEY, value)?.apply()
        }

    val isUserLoggedIn get() = auth.currentUser != null

    fun clearData() {
        username = null
        email = null
        uid = null
    }


    private var resultLauncher: ActivityResultLauncher<Intent>? = null


    private fun getUsername(): LiveData<WorkInfo> {
        val updateUsernameRequest = OneTimeWorkRequestBuilder<UsernameWorker>()
            .setInputData(createInputDataForUri(uid))
            .build()
        workManager.enqueue(updateUsernameRequest)
        return workManager.getWorkInfoByIdLiveData(updateUsernameRequest.id)
    }

    fun userDocument(uid: String): Task<DocumentSnapshot> {
        return firestore.collection(USERS).document(uid).get()
    }

    private fun createInputDataForUri(uid: String?): Data {
        if (uid == null) throw IllegalStateException("User must be logged in.")
        return Data.Builder().apply {
            putString("uid", uid)
        }.build()
    }

    fun isUsernameValid(username: String): Task<QuerySnapshot> {
        val usersRef = firestore.collection("users")
        val query = usersRef.whereEqualTo("username", username)
        return query.get()
    }

    fun saveUsername(username: String): Task<Void> {
        val data = hashMapOf(
            "username" to username,
            "email" to email
        )
        return firestore.collection("users").document(uid!!).set(data)
    }


}