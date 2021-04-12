package com.ekosoftware.secretdms.ui.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.Strings
import com.ekosoftware.secretdms.data.model.ChatPreview
import com.ekosoftware.secretdms.databinding.ItemChatPreviewBinding
import com.ekosoftware.secretdms.util.asDateTimeString
import org.joda.time.LocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ChatPreviewsListAdapter(private var onSelected: (ChatPreview) -> Unit) :
    ListAdapter<ChatPreview, ChatPreviewsListAdapter.ChatPreviewsListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatPreviewsListViewHolder =
        ChatPreviewsListViewHolder(ItemChatPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false), onSelected)

    override fun onBindViewHolder(holderList: ChatPreviewsListViewHolder, position: Int): Unit = holderList.bind(getItem(position))

    inner class ChatPreviewsListViewHolder(private val binding: ItemChatPreviewBinding, private val onSelected: (ChatPreview) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatPreview) {
            binding.root.setOnClickListener { onSelected(item) }
            binding.run {
                friend.text = item.friendId
                val lastDateTime = item.lastMessageTime.asDateTimeString()
                timestamp.run {
                    isVisible = lastDateTime.isNotEmpty()
                    text = lastDateTime
                }
                if (item.unreadMessages > 0) unread.text = item.unreadMessages.toString() else unread.isVisible = false
                val timeInSeconds = item.minDestructionTime?.div(1000L) ?: 0L
                if (timeInSeconds > 0L) timeLeft.text = Strings.get(R.string.min_time_left_in_chat, timeInSeconds) else timeLeft.isVisible = false
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatPreview>() {
            override fun areItemsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean = oldItem.friendId == newItem.friendId
            override fun areContentsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean = oldItem == newItem
        }
    }
}
