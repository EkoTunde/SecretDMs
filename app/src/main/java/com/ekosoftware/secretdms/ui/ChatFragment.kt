package com.ekosoftware.secretdms.ui

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.Strings
import com.ekosoftware.secretdms.app.resources.TimeUnits
import com.ekosoftware.secretdms.app.resources.TimeUnits.DAYS
import com.ekosoftware.secretdms.app.resources.TimeUnits.HOURS
import com.ekosoftware.secretdms.app.resources.TimeUnits.MINUTES
import com.ekosoftware.secretdms.app.resources.TimeUnits.SECONDS
import com.ekosoftware.secretdms.base.BaseViewHolder
import com.ekosoftware.secretdms.databinding.FragmentChatBinding
import com.ekosoftware.secretdms.presentation.MainViewModel
import com.ekosoftware.secretdms.ui.adapters.MessagesListAdapter
import com.ekosoftware.secretdms.ui.selection.LinearLayoutManagerWrapper
import com.ekosoftware.secretdms.ui.selection.OnActionItemClickListener
import com.ekosoftware.secretdms.ui.selection.SelectionActionModeCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private val args: ChatFragmentArgs by navArgs()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var listAdapter: MessagesListAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<MaterialCardView>

    private lateinit var tracker: SelectionTracker<Long>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)
        mainViewModel.setChatId(args.chatId)
        initViews()
        fetchChats()
    }

    private var scrollPosition = 0
    private var chatListSize = 0

    private fun initViews() = binding.run {
        initBottomSheetBehavior()
        binding.newMessage.suffixText = "@10s"
        slider.apply {
            addOnChangeListener { _, _, _ -> updateCurrentSliderValueText() }
            setLabelFormatter { it.toInt().toString() }
        }
        chipGroup.setOnCheckedChangeListener { _, _ -> updateCurrentSliderValueText() }
        sendMessageBtn.setOnClickListener {
            newMessage.editText!!.text.toString().trim().takeIf { it.isNotEmpty() }?.let { messageBody ->
                val timer = getFinalTimerValue()
                mainViewModel.sendMessage(messageBody, timer)
                newMessage.editText!!.setText("")
            }
        }
        messagesRecyclerView.apply {
            listAdapter = MessagesListAdapter()
            val manager = LinearLayoutManagerWrapper(requireContext())
            manager.stackFromEnd = true
            layoutManager = manager
            adapter = listAdapter
            initSelectionCapabilities()
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    scrollPosition += dy
                    if (scrollPosition <= 50 && scrollPosition >= -50) {
                        if (fabGoBackDown.isShown) fabGoBackDown.hide()
                    } else {
                        if (!fabGoBackDown.isShown) fabGoBackDown.show()
                    }
                }
            })
        }
        fabGoBackDown.setOnClickListener {
            resetScrollingPosition()
        }
    }

    private fun resetScrollingPosition() = binding.run {
        scrollPosition = 0
        fabGoBackDown.hide()
        messagesRecyclerView.scrollToPosition(messagesRecyclerView.adapter!!.itemCount - 1)
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

        closeBottomSheetButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun initSelectionCapabilities() {
        tracker = SelectionTracker.Builder<Long>(
            "ChatFragment Selection Id",
            binding.messagesRecyclerView,
            ItemIdKeyProvider(binding.messagesRecyclerView),
            ItemLookup(binding.messagesRecyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()
        listAdapter.setTracker(tracker)
        selectionActionModeCallback = SelectionActionModeCallback(actionItemClickListener)
        tracker.addObserver(selectionObserver)
    }

    private lateinit var selectionActionModeCallback: SelectionActionModeCallback

    private val selectionObserver: SelectionTracker.SelectionObserver<Long> = object : SelectionTracker.SelectionObserver<Long>() {
        override fun onSelectionChanged() {

            // Update ActionMode's title
            selectionActionModeCallback.updateTitle(getString(R.string.selected_amount, tracker.selection.size()))

            // Start ActionMode if it isn't already started
            if (!selectionActionModeCallback.isActive && tracker.selection.size() == 1) {
                selectionActionModeCallback.startActionMode(
                    requireActivity() as AppCompatActivity, R.menu.menu_action_mode, getString(
                        R.string.selected_amount, 1
                    )
                )
            }

            // Finish ActionMode when there's no selection and it is currently active
            if (selectionActionModeCallback.isActive && tracker.selection.size() == 0) selectionActionModeCallback.finishActionMode()
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
        val unit: Long = TimeUnits.getValue(params.second)
        return amount * unit
    }

    private fun fetchChats() = mainViewModel.getMessages().observe(viewLifecycleOwner) {
        listAdapter.submitList(it)
        binding.messagesRecyclerView.scrollToPosition(it.lastIndex)
        chatListSize = it.size
        if (!shouldTimerRun) {
            shouldTimerRun = true
            runTimer()
        }
    }

    override fun onStop() {
        super.onStop()
        shouldTimerRun = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapter.submitList(emptyList())
        mainViewModel.clearChatId()
        shouldTimerRun = false
    }

    private val actionItemClickListener: OnActionItemClickListener = object : OnActionItemClickListener {
        override fun onSelectAllPressed() {
            tracker.clearSelection()
            (0 until mainViewModel.messagesCount).forEach { tracker.select(it.toLong()) }
        }

        override fun onDeletePressed() {
            val selectedPositions = tracker.selection.toList()
            mainViewModel.deleteMessagesWithPositions(selectedPositions)
            Toast.makeText(requireContext(), "DELETING", Toast.LENGTH_SHORT).show()
            tracker.clearSelection()
        }

        override fun onFinished() {
            tracker.clearSelection()
        }
    }

    inner class ItemIdKeyProvider(private val recyclerView: RecyclerView) : ItemKeyProvider<Long>(SCOPE_MAPPED) {
        override fun getKey(position: Int): Long =
            recyclerView.adapter?.getItemId(position) ?: throw IllegalStateException("RecyclerView adapter is not set!")

        override fun getPosition(key: Long): Int = recyclerView.findViewHolderForItemId(key)?.layoutPosition ?: RecyclerView.NO_POSITION
    }

    inner class ItemLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            recyclerView.findChildViewUnder(event.x, event.y)?.let { view ->
                return (recyclerView.getChildViewHolder(view) as BaseViewHolder<*>).getItemDetails()
            }
            return null
        }
    }

    /**
     * To determine whereas messages with timer should run or not.
     */
    private var shouldTimerRun = false

    /**
     * Executes a coroutine so to run a non stop timer to decrease seconds in message's timers.
     */
    private fun runTimer() = CoroutineScope(lifecycleScope.coroutineContext + Dispatchers.Default).launch {
        while (shouldTimerRun) {
            delay(1000)
            mainViewModel.decreaseTimers()
        }
    }
}