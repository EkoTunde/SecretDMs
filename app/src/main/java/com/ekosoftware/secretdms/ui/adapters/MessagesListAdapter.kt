package com.ekosoftware.secretdms.ui.adapters


import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.TimeUnits
import com.ekosoftware.secretdms.base.BaseViewHolder
import com.ekosoftware.secretdms.data.model.DIRECTION_SENT
import com.ekosoftware.secretdms.data.model.Message
import com.ekosoftware.secretdms.databinding.ItemMessageReceivedBinding
import com.ekosoftware.secretdms.databinding.ItemMessageSentBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import org.joda.time.LocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MessagesListAdapter(
    private val context: Context,
    private var onSelected: (Message) -> Unit
) :
    ListAdapter<Message, BaseViewHolder<*, *, *>>(DIFF_CALLBACK) {

    inner class MessageSentViewHolder(
        private val binding: ItemMessageSentBinding,
        private val onSelected: (Message) -> Unit
    ) : BaseViewHolder<Message, Int, Boolean>(binding.root) {
        override fun bind(item: Message, param2: Int, param3: Boolean) = binding.run {
            guideTop.changeTopGuideline(param2)
            viewMarginBottom.isVisible = param3
            subContainer.radius
            arrayOf(root, subContainer).forEach {
                it.setOnClickListener {
                    showInfoCheckBox.isChecked = !showInfoCheckBox.isChecked
                    footerInfoContainer.isVisible = showInfoCheckBox.isChecked
                }
            }
            msgBody.text = item.body
            timestampTextView.text = getSentReceivedDateTime(item.sentInMillis)
            val timerText = getTimerText(item.timerInMillis)
            timer.text = timerText
            timer.isVisible = timerText == null
        }
    }

    inner class MessageReceivedViewHolder(
        private val binding: ItemMessageReceivedBinding,
        private val onSelected: (Message) -> Unit
    ) : BaseViewHolder<Message, Int, Boolean>(binding.root) {
        override fun bind(item: Message, param2: Int, param3: Boolean) = binding.run {
            guideTop.changeTopGuideline(param2)
            viewMarginBottom.isVisible = param3
            arrayOf(root, subContainer).forEach {
                it.setOnClickListener {
                    showInfoCheckBox.isChecked = !showInfoCheckBox.isChecked
                    timestampTextView.isVisible = showInfoCheckBox.isChecked
                }
            }
            msgBody.text = item.body
            timestampTextView.text = getSentReceivedDateTime(item.receivedInMillis)
            if (item.timerInMillis != null && item.timerInMillis!! > 0L) {
                timer.text = getTimerText(item.timerInMillis)
            } else timer.isVisible = false
        }
    }

    private fun Guideline.changeTopGuideline(positionInList: Int) {
        val constParam: ConstraintLayout.LayoutParams =
            this.layoutParams as ConstraintLayout.LayoutParams
        constParam.guideBegin = if (positionInList == POSITION_FIRST) 32 else 8
        this.layoutParams = constParam
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

    private fun getTimerText(timerInMillis: Long?): String? {
        if (timerInMillis != null && timerInMillis > 0L) {
            var time = timerInMillis / 1000
            for (triple: Triple<String, Long, Int> in TimeUnits.asArray()) {
                time /= triple.second
                if (time < triple.third + 1) return "$time ${triple.first}"
            }
        }
        return null
    }

    override fun getItemViewType(position: Int): Int = getItem(position).direction

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*, *, *> {
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


    override fun onBindViewHolder(holder: BaseViewHolder<*, *, *>, position: Int) {
        getItem(position)?.let { item ->

            val positionInList = if (position == 0 || getItem(position-1).direction != item.direction) {
                POSITION_FIRST
            } else POSITION_NOT_FIRST

            when (holder) {
                is MessageSentViewHolder -> holder.bind(
                    item,
                    positionInList,
                    position == itemCount - 1
                )
                is MessageReceivedViewHolder -> holder.bind(
                    item,
                    positionInList,
                    position == itemCount - 1
                )
                else -> throw IllegalArgumentException("The given holder (${holder.javaClass}) isn't valid")
            }
        }
    }

    companion object {

        private const val POSITION_FIRST = 1
        private const val POSITION_NOT_FIRST = 0

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem == newItem
        }
    }
}
