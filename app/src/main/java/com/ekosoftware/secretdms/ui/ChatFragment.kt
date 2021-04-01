package com.ekosoftware.secretdms.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.Strings
import com.ekosoftware.secretdms.app.resources.Strings.TimeUnits.DAYS
import com.ekosoftware.secretdms.app.resources.Strings.TimeUnits.HOURS
import com.ekosoftware.secretdms.app.resources.Strings.TimeUnits.MINUTES
import com.ekosoftware.secretdms.app.resources.Strings.TimeUnits.SECONDS
import com.ekosoftware.secretdms.databinding.FragmentChatBinding
import com.ekosoftware.secretdms.presentation.MainViewModel
import com.ekosoftware.secretdms.ui.adapters.MessagesListAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private val args: ChatFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val listAdapter: MessagesListAdapter = MessagesListAdapter {
        Toast.makeText(requireContext(), it.body, Toast.LENGTH_SHORT).show()
    }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)
        mainViewModel.setChatId(args.chatId)
        initViews()
        fetchChats()
    }

    private fun initViews() = binding.run {
        initBottomSheetBehavior()
        binding.newMessage.suffixText = "@10s"
        //timerTitle.setOnCheckedChangeListener { _, isChecked -> toggleTimerLayoutState(isChecked) }
        slider.addOnChangeListener { _, _, _ -> updateCurrentSliderValueText() }
        slider.setLabelFormatter { it.toInt().toString() }
        chipGroup.setOnCheckedChangeListener { _, _ -> updateCurrentSliderValueText() }
        sendMessageBtn.setOnClickListener {
            val messageBody = newMessage.editText!!.text.toString().trim()
            val timer = getFinalTimerValue()
            mainViewModel.sendMessage(messageBody, timer)
            newMessage.editText!!.setText("")
        }
        messagesRecyclerView.adapter = listAdapter
    }

    private fun initBottomSheetBehavior() = binding.run {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        open.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            newMessage.suffixText = null
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) = Unit

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val position = 1f + slideOffset
                binding.actionsContainer.progress = position
                if (position < 0.5) {
                    val currentValue = binding.sliderCurrentValue.text.toString()
                    binding.newMessage.suffixText = "@${currentValue}"
                } else binding.newMessage.suffixText = null
            }
        })

        binding.closeBottomSheetButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }
    }

    private fun toggleTimerLayoutState(isChecked: Boolean) = binding.run {
        arrayOf(horizontalContainer, slider, cardViewSliderCurrentValue).forEach {
            it.isVisible = isChecked
        }
        timerTitle.text =
            if (isChecked) Strings.get(R.string.timer_options_title_expanded)
            else {
                val params = getTimerParams()
                Strings.get(R.string.timer_options_title_collapsed, params.first, params.second)
            }
    }

    private fun getTimerParams(): Pair<Int, String> {
        val time = binding.slider.value.toInt()
        val unit = when (binding.chipGroup.checkedChipId) {
            R.id.chip_seconds -> SECONDS
            R.id.chip_minutes -> MINUTES
            R.id.chip_hours -> HOURS
            R.id.chip_days -> DAYS
            else -> throw IllegalArgumentException("Unit may only be seconds, minutes, hours or days.")
        }
        return Pair(time, unit)
    }

    private fun updateCurrentSliderValueText() = binding.run {
        slider.valueFrom = .0f
        val params = getTimerParams()
        sliderCurrentValue.text =
            Strings.get(R.string.current_slider_value_placeholder, params.first, params.second)
        val to = when (params.second) {
            SECONDS, MINUTES -> 59f
            HOURS -> 23f
            DAYS -> 60f
            else -> throw IllegalStateException("The specified argument timeUnit isn't a valid TimeUnit.")
        }
        if (params.first > to) slider.value = to
        slider.valueTo = to
    }

    private fun getFinalTimerValue(): Long {
        val params = getTimerParams()
        val amount: Int = params.first
        val unit: Long = Strings.TimeUnits.getValue(params.second)
        return amount * unit
    }

    private fun fetchChats() = mainViewModel.getMessages().observe(viewLifecycleOwner) {
        listAdapter.submitList(it)
        binding.messagesRecyclerView.scrollToPosition(it.lastIndex)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapter.submitList(emptyList())
        mainViewModel.clearChatId()
    }
}