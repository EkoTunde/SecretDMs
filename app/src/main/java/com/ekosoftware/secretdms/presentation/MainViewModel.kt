package com.ekosoftware.secretdms.presentation

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
        private const val SELECTED_CHATS_KEY = "friend id key"
        private const val CHATS_COUNT_KEY = "chats count key"
    }

    val isUserLoggedIn: MutableLiveData<Boolean> =
        savedStateHandle.getLiveData(USER_LOGGED_IN_KEY, Authentication.isUserLoggedIn)

    fun refreshLoginState() {
        savedStateHandle[USER_LOGGED_IN_KEY] = Authentication.isUserLoggedIn
    }

    fun insertDummyData() = viewModelScope.launch {
        defaultMessagesRepository.insertDummyData()
    }

    private val selectedChats: MutableLiveData<MutableList<String>> = savedStateHandle.getLiveData(SELECTED_CHATS_KEY, mutableListOf())

    private var chats: LiveData<List<ChatPreview>>? = null

    fun getChats(): LiveData<List<ChatPreview>> = chats ?: liveData {
        emitSource(defaultMessagesRepository.getChats())
    }.also { chats = it }

    val chatsCount: Int get() = chats?.value?.size ?: 0

    private val friendId: MutableLiveData<String?> = savedStateHandle.getLiveData<String?>(FRIEND_ID_KEY, null)

    fun setChatId(id: String) {
        savedStateHandle[FRIEND_ID_KEY] = id
    }

    fun clearChatId() {
        savedStateHandle[FRIEND_ID_KEY] = null
        messages = null
    }

    private var messages: LiveData<List<Message>>? = null

    fun getMessages(): LiveData<List<Message>> = messages ?: friendId.switchMap { id ->
        liveData<List<Message>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (id != null) emitSource(defaultMessagesRepository.getChatWithFriendId(id))
            else emit(emptyList())
        }
    }.also {
        messages = it
    }

    val messagesCount: Int get() = messages?.value?.size ?: 0

    fun newChat(friendId: String): Job =
        CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch { defaultMessagesRepository.newChat(friendId) }

    fun clearData(): Job = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch { defaultMessagesRepository.clearData() }

    fun deleteChatsWithPositions(positionsSelected: List<Long>) {
        val chatsForDeletion = chats?.value?.filterIndexed { index, _ ->
            index.toLong() in positionsSelected
        }
        CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch { defaultMessagesRepository.deleteChats(chatsForDeletion) }
    }

    fun deleteMessagesWithPositions(positionsSelected: List<Long>) {
        val messagesForDeletion = messages?.value?.filterIndexed { index, _ ->
            index.toLong() in positionsSelected
        }
        CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch { defaultMessagesRepository.deleteMessages(messagesForDeletion) }
    }

    private var job: Job? = Job()

    fun startJob() {
        job = CoroutineScope(viewModelScope.coroutineContext + Dispatchers.Default).launch {
            var messagesMaxTimer = messages?.value?.maxOf {
                it.timerInMillis ?: 0
            } ?: 0
            while (true) {
                delay(1000)
                friendId.value?.let { defaultMessagesRepository.updateTimers(it) }
            }
        }
        job?.start()
    }

    fun stopJob() {
        job?.cancel()
    }

    fun clearJob() {
        job = null
    }

    fun sendMessage(body: String, timerInMillis: Long) =
        CoroutineScope(viewModelScope.coroutineContext + Dispatchers.IO).launch {
            savedStateHandle.get<String?>(FRIEND_ID_KEY)?.let { friendId: String ->
                defaultMessagesRepository.sendMessage(friendId, body, timerInMillis)
            }
        }

    private var isUserNameValid: LiveData<Resource<Boolean>>? = null
}