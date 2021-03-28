package com.ekosoftware.secretdms.domain

import androidx.lifecycle.MutableLiveData
import com.ekosoftware.secretdms.base.AuthState
import com.ekosoftware.secretdms.base.Resource
import com.ekosoftware.secretdms.base.UsernameValidationState
import com.ekosoftware.secretdms.data.model.User

interface AuthenticationRepository {
    fun isUserAuthenticated(): MutableLiveData<AuthState<User>>
    fun validateUser()
    fun isUsernameValid(): MutableLiveData<Resource<Boolean>>?
    fun saveUserData(username: String)
    suspend fun userExists(username: String): Resource<Boolean>
    fun signOut()
    suspend fun getUserData(): Resource<Pair<String, String>>
}

