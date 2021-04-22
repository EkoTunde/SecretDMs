package com.ekosoftware.secretdms.ui.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.DiffUtil
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.Strings
import com.ekosoftware.secretdms.app.resources.TimeUnits.asTimeAndUnit
import com.ekosoftware.secretdms.base.BaseListAdapter
import com.ekosoftware.secretdms.base.BaseViewHolder
import com.ekosoftware.secretdms.data.model.DIRECTION_SENT
import com.ekosoftware.secretdms.data.model.Message
import com.ekosoftware.secretdms.databinding.ItemMessageReceivedBinding
import com.ekosoftware.secretdms.databinding.ItemMessageSentBinding
import com.ekosoftware.secretdms.util.asDateTimeString


class MessagesListAdapter : BaseListAdapter<Message>(DIFF_CALLBACK) {

    init {
        setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).direction

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return if (viewType == DIRECTION_SENT)
            MessageSentViewHolder(ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else MessageReceivedViewHolder(ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        getItem(position)?.let { item ->
            when (holder) {
                is MessageSentViewHolder -> holder.bind(
                    item,
                    position == 0 || getItem(position - 1).direction != item.direction,
                    position == itemCount - 1,
                    position
                )
                is MessageReceivedViewHolder -> holder.bind(
                    item,
                    position == 0 || getItem(position - 1).direction != item.direction,
                    position == itemCount - 1,
                    position
                )
                else -> throw IllegalArgumentException("The given holder (${holder.javaClass}) isn't valid")
            }
        }
    }

    inner class MessageSentViewHolder(private val binding: ItemMessageSentBinding) : BaseViewHolder<Message>(binding.root) {
        override fun bind(item: Message, b1: Boolean, b2: Boolean, position: Int): Unit = binding.run {
            viewMarginTop.isVisible = b1
            pin.isVisible = b1
            viewMarginBottom.isVisible = b2
            subContainer.radius
            val sentInMillis = item.timestamp.asDateTimeString()
            arrayOf(root, subContainer).forEach {
                it.setOnClickListener {
                    showInfoCheckBox.isChecked = !showInfoCheckBox.isChecked
                    if (item.timestamp.asDateTimeString().isNotEmpty()) {
                        timestampTextView.isVisible = showInfoCheckBox.isChecked
                    }
                }
            }
            sentInMillis.takeIf { it.isNotEmpty() }?.let { timestampTextView.text = it }
            item.timerInMillis.asTimeAndUnit()
                ?.let { result -> timestampTextView.append(Strings.get(R.string.sent_message_extra_info, result.second, result.first)) }
            msgBody.text = item.body
            selectionTracker?.let {
                if (it.isSelected(bindingAdapterPosition.toLong())) {
                    it.select(bindingAdapterPosition.toLong())
                    root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.selected))
                } else {
                    it.deselect(bindingAdapterPosition.toLong())
                    root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.white))
                }
            }
        }

        override fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = bindingAdapterPosition
            override fun getSelectionKey(): Long = itemId
        }
    }

    inner class MessageReceivedViewHolder(private val binding: ItemMessageReceivedBinding) : BaseViewHolder<Message>(binding.root) {
        override fun bind(item: Message, b1: Boolean, b2: Boolean, position: Int): Unit = binding.run {
            viewMarginTop.isVisible = b1
            pin.isVisible = b1
            viewMarginBottom.isVisible = b2
            arrayOf(root, subContainer).forEach {
                it.setOnClickListener {
                    showInfoCheckBox.isChecked = !showInfoCheckBox.isChecked
                    timestampTextView.isVisible = showInfoCheckBox.isChecked
                }
            }
            item.timestamp.asDateTimeString().takeIf { it.isNotEmpty() }?.let { timestampTextView.append(it) }
            item.timerInMillis.asTimeAndUnit()?.let { result ->
                timer.text = Strings.get(R.string.received_timer_text, result.second, result.first)
                timer.isVisible = true
            }
            msgBody.text = item.body
            selectionTracker?.let {
                if (it.isSelected(bindingAdapterPosition.toLong())) {
                    it.select(bindingAdapterPosition.toLong())
                    root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.selected))
                } else {
                    it.deselect(bindingAdapterPosition.toLong())
                    root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.white))
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
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem == newItem
        }
    }
}
