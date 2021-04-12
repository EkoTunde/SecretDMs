package com.ekosoftware.secretdms.domain

import androidx.lifecycle.MutableLiveData
import com.ekosoftware.secretdms.base.AuthState
import com.ekosoftware.secretdms.base.Resource
import com.ekosoftware.secretdms.data.auth.AuthenticationDataSource
import com.ekosoftware.secretdms.data.model.User
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAuthenticationRepository @Inject constructor(private val authenticationDataSource: AuthenticationDataSource) : AuthenticationRepository {
    override fun isUserAuthenticated(): MutableLiveData<AuthState<User>> = authenticationDataSource.isUserAuthenticateInFirebaseMutableLiveData
    override fun validateUser() = authenticationDataSource.validateUser()
    override fun isUsernameValid(): MutableLiveData<Resource<Boolean>>? = authenticationDataSource.isUsernameValid
    override fun saveUserData(username: String) = authenticationDataSource.saveUserData(username)
    override suspend fun userExists(username: String) = authenticationDataSource.userExists(username)
    override fun signOut() = authenticationDataSource.logout()
    override suspend fun getUserData(): Resource<Pair<String, String>> = authenticationDataSource.getUserData()
}