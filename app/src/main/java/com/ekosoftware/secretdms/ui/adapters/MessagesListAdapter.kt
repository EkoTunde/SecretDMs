package com.ekosoftware.secretdms.ui.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.Strings
import com.ekosoftware.secretdms.app.resources.TimeUnits.asTimeAndUnit
import com.ekosoftware.secretdms.base.BaseViewHolder
import com.ekosoftware.secretdms.data.model.DIRECTION_SENT
import com.ekosoftware.secretdms.data.model.Message
import com.ekosoftware.secretdms.databinding.ItemMessageReceivedBinding
import com.ekosoftware.secretdms.databinding.ItemMessageSentBinding
import com.ekosoftware.secretdms.util.asDateTimeString
import org.joda.time.LocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MessagesListAdapter(private val context: Context, private var onSelected: (Message) -> Unit) :
    ListAdapter<Message, BaseViewHolder<*, *, *>>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int = getItem(position).direction

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*, *, *> {
        return if (viewType == DIRECTION_SENT)
            MessageSentViewHolder(ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false), onSelected)
        else MessageReceivedViewHolder(ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false), onSelected)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*, *, *>, position: Int) {
        getItem(position)?.let { item ->
            when (holder) {
                is MessageSentViewHolder -> holder.bind(
                    item,
                    position == 0 || getItem(position - 1).direction != item.direction,
                    position == itemCount - 1
                )
                is MessageReceivedViewHolder -> holder.bind(
                    item,
                    position == 0 || getItem(position - 1).direction != item.direction,
                    position == itemCount - 1
                )
                else -> throw IllegalArgumentException("The given holder (${holder.javaClass}) isn't valid")
            }
        }
    }

    inner class MessageSentViewHolder(
        private val binding: ItemMessageSentBinding,
        private val onSelected: (Message) -> Unit
    ) : BaseViewHolder<Message, Boolean, Boolean>(binding.root) {
        override fun bind(item: Message, param2: Boolean, param3: Boolean) = binding.run {
            viewMarginTop.isVisible = param2
            pin.isVisible = param2
            viewMarginBottom.isVisible = param3
            subContainer.radius
            val sentInMillis = item.sentInMillis.asDateTimeString()
            arrayOf(root, subContainer).forEach {
                it.setOnClickListener {
                    showInfoCheckBox.isChecked = !showInfoCheckBox.isChecked
                    if (item.sentInMillis.asDateTimeString().isNotEmpty()) {
                        timestampTextView.isVisible = showInfoCheckBox.isChecked
                    }
                }
            }
            sentInMillis.takeIf { it.isNotEmpty() }?.let { timestampTextView.text = it }
            item.timerInMillis.asTimeAndUnit()
                ?.let { result -> timestampTextView.append(Strings.get(R.string.sent_message_extra_info, result.second, result.first)) }
            msgBody.text = item.body
        }
    }

    inner class MessageReceivedViewHolder(
        private val binding: ItemMessageReceivedBinding,
        private val onSelected: (Message) -> Unit
    ) : BaseViewHolder<Message, Boolean, Boolean>(binding.root) {
        override fun bind(item: Message, param2: Boolean, param3: Boolean) = binding.run {
            viewMarginTop.isVisible = param2
            pin.isVisible = param2
            viewMarginBottom.isVisible = param3
            arrayOf(root, subContainer).forEach {
                it.setOnClickListener {
                    showInfoCheckBox.isChecked = !showInfoCheckBox.isChecked
                    timestampTextView.isVisible = showInfoCheckBox.isChecked
                }
            }
            item.receivedInMillis.asDateTimeString().takeIf { it.isNotEmpty() }?.let { timestampTextView.append(it) }
            item.timerInMillis.asTimeAndUnit()?.let { result ->
                timer.text = Strings.get(R.string.received_timer_text, result.second, result.first)
                timer.isVisible = true
            }
            msgBody.text = item.body
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem == newItem
        }
    }
}
