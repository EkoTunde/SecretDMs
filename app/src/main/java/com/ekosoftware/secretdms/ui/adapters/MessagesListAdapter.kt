package com.ekosoftware.secretdms.ui.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.Strings
import com.ekosoftware.secretdms.base.BaseViewHolder
import com.ekosoftware.secretdms.data.model.DIRECTION_SENT
import com.ekosoftware.secretdms.data.model.Message
import com.ekosoftware.secretdms.databinding.ItemMessageReceivedBinding
import com.ekosoftware.secretdms.databinding.ItemMessageSentBinding
import org.joda.time.LocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MessagesListAdapter(
    private var onSelected: (Message) -> Unit
) :
    ListAdapter<Message, BaseViewHolder<*>>(MessageDiffCallback()) {

    inner class MessageSentViewHolder(
        private val binding: ItemMessageSentBinding,
        private val spaceUp: Boolean = false,
        private val onSelected: (Message) -> Unit
    ) : BaseViewHolder<Message>(binding.root) {
        override fun bind(item: Message, position: Int) = binding.run {
            /*binding.mainContainer.setOnCheckedChangeListener { _, isChecked ->
                footerInfoContainer.isVisible = isChecked
            }*/
            binding.root.setOnClickListener { onSelected.invoke(item) }
            msgBody.text = item.body
            sentStatusImage.setImageResource(getStateIcon(item))
            timestampTextView.text = getSentReceivedDateTime(item.sentInMillis)
            timer.text = getTimerText(item.timerInMillis)
        }
    }

    inner class MessageReceivedViewHolder(
        private val binding: ItemMessageReceivedBinding,
        private val spaceUp: Boolean = false,
        private val onSelected: (Message) -> Unit
    ) : BaseViewHolder<Message>(binding.root) {
        override fun bind(item: Message, position: Int)  = binding.run {
            if(spaceUp)  binding.guideTop.setGuidelineBegin(4)
            binding.root.setOnClickListener { onSelected.invoke(item) }
            msgBody.text = item.body
            timestampTextView.text = getSentReceivedDateTime(item.receivedInMillis)
            timer.text = getTimerText(item.timerInMillis)
        }
    }

    private fun getStateIcon(item: Message) = when {
        item.readInMillis != null -> R.drawable.ic_status_read_12
        item.receivedInMillis != null -> R.drawable.ic_status_received_12
        item.sentInMillis != null -> R.drawable.ic_status_sent_12
        else -> R.drawable.ic_status_pending_12
    }


    private fun getSentReceivedDateTime(timeInMillis: Long?): String {
        if (timeInMillis == null) return ""
        val sentDateTime = LocalDateTime(timeInMillis)
        val now = LocalDateTime()
        val date = if (now.dayOfMonth != sentDateTime.dayOfMonth) {
            if (sentDateTime.isBefore(now.minusWeeks(1)))
                DateFormat.getDateInstance(DateFormat.SHORT).format(sentDateTime.toDate()) ?: ""
            else {
                val sdf = SimpleDateFormat("EEE", Locale.getDefault())
                sdf.format(sentDateTime.toDate())
            } + " "
        } else ""
        val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(sentDateTime.toDate())
        return "$date$time"
    }

    private fun getTimerText(timerInMillis: Long?): String {
        if (timerInMillis != null && timerInMillis > 0L) {
            var time = timerInMillis / 1000
            for (triple: Triple<String, Long, Int> in Strings.TimeUnits.asArray()) {
                time /= triple.second
                if (time < triple.third + 1) return "$time ${triple.first}"
            }
        }
        return Strings.get(R.string.timer_wasnt_set)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).direction

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return if (viewType == DIRECTION_SENT) MessageSentViewHolder(
            ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onSelected
        )
        else MessageReceivedViewHolder(
            ItemMessageReceivedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onSelected
        )
    }


    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) = when (holder) {
        is MessageSentViewHolder -> holder.bind(getItem(position),position)
        is MessageReceivedViewHolder -> holder.bind(getItem(position),position)
        else -> throw IllegalArgumentException("The given holder (${holder.javaClass}) isn't valid")
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
        oldItem == newItem
}
