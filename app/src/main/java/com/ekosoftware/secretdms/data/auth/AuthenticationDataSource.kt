package com.ekosoftware.secretdms.data.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ekosoftware.secretdms.app.Constants.PARAM_EMAIL
import com.ekosoftware.secretdms.app.Constants.PARAM_UID
import com.ekosoftware.secretdms.app.Constants.PARAM_USERNAME
import com.ekosoftware.secretdms.app.Constants.USERS
import com.ekosoftware.secretdms.app.TOPIC
import com.ekosoftware.secretdms.base.AuthState
import com.ekosoftware.secretdms.base.Resource
import com.ekosoftware.secretdms.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationDataSource @Inject constructor(
    @ApplicationContext private val applicationContext: Context
) {
    init {
        authStateListener()
    }

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersRef = firestore.collection(USERS)

    private fun authStateListener() {
        FirebaseAuth.getInstance().addAuthStateListener {
            val firebaseUser = it.currentUser
            if (firebaseUser == null) {
                isUserAuthenticateInFirebaseMutableLiveData.setValue(AuthState.None())
            } else {
                isUserAuthenticateInFirebaseMutableLiveData.setValue(AuthState.Authenticated())
            }
        }
    }

    val isUserAuthenticateInFirebaseMutableLiveData =
        MutableLiveData<AuthState<User>>(AuthState.Checking())

    var isUsernameValid: MutableLiveData<Resource<Boolean>>? =
        MutableLiveData<Resource<Boolean>>(Resource.Success(false))

    suspend fun userExists(username: String): Resource<Boolean> {
        return Resource.Success(usersRef.whereEqualTo(PARAM_USERNAME, username).get().await().isEmpty)
    }

    private val TAG = "AuthenticationDataSourc"

    fun validateUser() {
        val uid = auth.currentUser!!.uid
        isUserAuthenticateInFirebaseMutableLiveData.value = AuthState.Checking()
        usersRef.document(uid).get().addOnCompleteListener { task ->
            if (!task.isSuccessful || task.result == null) {
                isUserAuthenticateInFirebaseMutableLiveData.value = AuthState.AuthError(task.exception?.message)
            } else {
                task.result?.let { doc ->
                    if (doc.exists()) {
                        Authentication.username = doc[PARAM_USERNAME].toString()
                        Authentication.email = doc[PARAM_EMAIL].toString()

                        isUserAuthenticateInFirebaseMutableLiveData.value = AuthState.ValidSession()
                    } else {
                        isUserAuthenticateInFirebaseMutableLiveData.value = AuthState.Validating()
                    }
                    return@addOnCompleteListener
                }
            }
        }
    }

    fun saveUserData(username: String) {
        isUserAuthenticateInFirebaseMutableLiveData.value = AuthState.Checking()
        val uid = auth.currentUser!!.uid
        val email = auth.currentUser!!.email
        usersRef.document(uid).set(
            hashMapOf(
                PARAM_UID to uid,
                PARAM_EMAIL to email,
                PARAM_USERNAME to username,
            )
        ).addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                isUserAuthenticateInFirebaseMutableLiveData.value = AuthState.ValidationError(task.exception?.message)
            } else {
                isUsernameValid = null
                isUserAuthenticateInFirebaseMutableLiveData.value = AuthState.ValidSession()

            }
        }

    }

    fun logout() = FirebaseAuth.getInstance().signOut()

    suspend fun getUserData(): Resource<Pair<String, String>> {
        val doc = usersRef.document(auth.currentUser!!.uid).get(Source.CACHE).await()
        val email: String = doc[PARAM_EMAIL].toString()
        val username: String = doc[PARAM_USERNAME].toString()
        return Resource.Success(Pair(email, username))
    }
}