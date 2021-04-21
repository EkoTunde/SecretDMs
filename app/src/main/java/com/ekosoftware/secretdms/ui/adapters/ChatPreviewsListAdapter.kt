package com.ekosoftware.secretdms.ui.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.DiffUtil
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.Strings
import com.ekosoftware.secretdms.base.BaseListAdapter
import com.ekosoftware.secretdms.base.BaseViewHolder
import com.ekosoftware.secretdms.data.model.ChatPreview
import com.ekosoftware.secretdms.databinding.ItemChatPreviewBinding
import com.ekosoftware.secretdms.util.asDateTimeString

class ChatPreviewsListAdapter(private var onSelected: (ChatPreview) -> Unit) :
    BaseListAdapter<ChatPreview>(DIFF_CALLBACK) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> =
        ChatPreviewsListViewHolder(ItemChatPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false), onSelected)

    override fun onBindViewHolder(holderList: BaseViewHolder<*>, position: Int): Unit = when (holderList) {
        is ChatPreviewsListViewHolder -> holderList.bind(getItem(position))
        else -> throw IllegalArgumentException("${holderList.javaClass} is not a valid BaseViewHolder @${this.javaClass.name}")
    }

    inner class ChatPreviewsListViewHolder(private val binding: ItemChatPreviewBinding, private val onSelected: (ChatPreview) -> Unit) :
        BaseViewHolder<ChatPreview>(binding.root) {
        override fun bind(item: ChatPreview): Unit = binding.run {
            root.setOnClickListener { onSelected(item) }
            friend.text = item.friendId
            val lastDateTime = item.lastMessageTimestamp.asDateTimeString()
            timestamp.run {
                isVisible = lastDateTime.isNotEmpty()
                text = lastDateTime
            }
            if (item.unreadMessages > 0) unread.text = item.unreadMessages.toString() else unread.isVisible = false
            val timeInSeconds = item.minTimerInMillis?.div(1000L) ?: 0L
            if (timeInSeconds > 0L) timeLeft.text = Strings.get(R.string.min_time_left_in_chat, timeInSeconds) else timeLeft.isVisible = false
            //val color = root.context.resources.getColor(R.color.teal_200)
            //root.setCardBackgroundColor(ColorStateList.valueOf(color))
            selectionTracker?.let {
                if (it.isSelected(bindingAdapterPosition.toLong())) {
                    it.select(bindingAdapterPosition.toLong())
                    root.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.grey_background))
                } else {
                    it.deselect(bindingAdapterPosition.toLong())
                    root.setCardBackgroundColor(ContextCompat.getColor(root.context, R.color.fui_transparent))
                }
            }
        }

        override fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = bindingAdapterPosition
            override fun getSelectionKey(): Long = itemId
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatPreview>() {
            override fun areItemsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean = oldItem.friendId == newItem.friendId
            override fun areContentsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean = oldItem == newItem
        }
    }
}
