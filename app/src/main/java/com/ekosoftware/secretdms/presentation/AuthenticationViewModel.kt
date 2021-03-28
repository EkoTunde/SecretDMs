package com.ekosoftware.secretdms.presentation

import android.util.Log
import androidx.lifecycle.*
import com.ekosoftware.secretdms.base.AuthState
import com.ekosoftware.secretdms.base.Resource
import com.ekosoftware.secretdms.data.model.User
import com.ekosoftware.secretdms.domain.DefaultAuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repository: DefaultAuthenticationRepository
) : ViewModel() {

    private var isUserAuthenticatedLiveData: LiveData<AuthState<User>>? = null

    fun isUserAuthenticated(): LiveData<AuthState<User>> =
        isUserAuthenticatedLiveData ?: repository.isUserAuthenticated().also {
            isUserAuthenticatedLiveData = it
        }

    fun performUsernameCheck() = repository.validateUser()

    private var isUsernameValid: LiveData<Resource<Boolean>>? = null

    fun isUsernameValid(): LiveData<Resource<Boolean>> = isUsernameValid
        ?: liveData<Resource<Boolean>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                repository.isUsernameValid()?.let {
                    emitSource(it)
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }.also {
            isUsernameValid = it
        }

    var usernameMutableLiveData = MutableLiveData<String>(null)

    fun setUsername(username: String) {
        usernameMutableLiveData.value = username
    }

    fun save() = usernameMutableLiveData.value?.let { repository.saveUserData(it) }

    val userExists = usernameMutableLiveData.switchMap { input ->
        liveData<Resource<Boolean>> {
            emit(Resource.Loading())
            try {
                input?.let { emit(repository.userExists(it)) }
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private var userData: LiveData<Resource<Pair<String, String>>>? = null

    fun getUserData(): LiveData<Resource<Pair<String, String>>> =
        userData
            ?: liveData<Resource<Pair<String, String>>>(viewModelScope.coroutineContext + Dispatchers.Default) {
                emit(Resource.Loading())
                try {
                    emit(repository.getUserData())
                } catch (e: Exception) {
                    emit(Resource.Error(e.toString()))
                }
            }.also {
                userData = it
            }

    fun signOut() {
        repository.signOut()
    }
}