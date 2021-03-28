package com.ekosoftware.secretdms.presentation

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.*
import com.ekosoftware.secretdms.base.Resource
import com.ekosoftware.secretdms.data.auth.Authentication
import com.ekosoftware.secretdms.data.model.ChatPreview
import com.ekosoftware.secretdms.data.model.Message
import com.ekosoftware.secretdms.domain.DefaultMessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val defaultMessagesRepository: DefaultMessagesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val USER_LOGGED_IN_KEY = "is user logged in key"
        private const val FRIEND_ID_KEY = "friend id key"
    }

    val isUserLoggedIn: MutableLiveData<Boolean> =
        savedStateHandle.getLiveData(USER_LOGGED_IN_KEY, Authentication.isUserLoggedIn)

    fun refreshLoginState() {
        savedStateHandle[USER_LOGGED_IN_KEY] = Authentication.isUserLoggedIn
    }

    fun insertDummyData() = viewModelScope.launch {
        defaultMessagesRepository.insertDummyData()
    }

    private var chats: LiveData<List<ChatPreview>>? = null

    fun getChats(): LiveData<List<ChatPreview>> = chats ?: liveData {
        emitSource(defaultMessagesRepository.getChats())//.map { it.reversed() })
    }.also {
        chats = it
    }

    private val friendId: MutableLiveData<String?> = savedStateHandle.getLiveData<String?>(FRIEND_ID_KEY, null)

    fun setChatId(id: String) {
        savedStateHandle[FRIEND_ID_KEY] = id
    }

    fun clearChatId() {
        savedStateHandle[FRIEND_ID_KEY] = null
    }

    private var messages: LiveData<List<Message>>? = null

    fun getMessages(): LiveData<List<Message>> = messages ?: friendId.switchMap { id ->
        liveData<List<Message>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            id?.let { emitSource(defaultMessagesRepository.getChatWithFriendId(id)) }
        }
    }.also {
        messages = it
    }

    fun newChat(friendId: String): Job =
        CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch {
            defaultMessagesRepository.newChat(friendId)
        }

    fun clearDatabase(): Job =
        CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch {
            defaultMessagesRepository.clearDatabase()
        }

    fun deleteMessage(messageId: String) {
    }

    fun sendMessage(body: String, timerInMillis: Long) =
        CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch {
            savedStateHandle.get<String?>(FRIEND_ID_KEY)?.let { friendId: String ->
                defaultMessagesRepository.sendMessage(friendId, body, timerInMillis)
            }
        }

    private var isUserNameValid: LiveData<Resource<Boolean>>? = null
}