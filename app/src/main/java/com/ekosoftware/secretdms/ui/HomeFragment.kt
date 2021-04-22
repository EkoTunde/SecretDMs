package com.ekosoftware.secretdms.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.MainActivity
import com.ekosoftware.secretdms.base.AuthState
import com.ekosoftware.secretdms.ui.selection.LinearLayoutManagerWrapper
import com.ekosoftware.secretdms.databinding.FragmentHomeBinding
import com.ekosoftware.secretdms.presentation.AuthenticationViewModel
import com.ekosoftware.secretdms.presentation.MainViewModel
import com.ekosoftware.secretdms.ui.adapters.ChatPreviewsListAdapter
import com.ekosoftware.secretdms.ui.selection.OnActionItemClickListener
import com.ekosoftware.secretdms.ui.selection.SelectionActionModeCallback
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val authViewModel: AuthenticationViewModel by activityViewModels()

    private lateinit var listAdapter: ChatPreviewsListAdapter

    private lateinit var tracker: SelectionTracker<Long>

    private val resultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    private fun signIn() {
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()
        resultLauncher.launch(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        initViews()
        fetchAuthenticationData()
        initSelectionCapabilities()
        //mainViewModel.clearData()
        mainViewModel.insertDummyData()
    }

    private fun initViews() = binding.run {
        loginBtn.setOnClickListener {
            signIn()
        }
        newChatBtn.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToDialogNewChatDialogFragment()
            findNavController().navigate(directions)
        }
        chatsRecyclerView.apply {
            listAdapter = ChatPreviewsListAdapter {
                val directions = HomeFragmentDirections.actionHomeFragmentToMessageFragment(it.friendId!!)
                findNavController().navigate(directions)
            }
            layoutManager = LinearLayoutManagerWrapper(requireContext())
            adapter = listAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun fetchAuthenticationData() = authViewModel.isUserAuthenticated().observe(viewLifecycleOwner) { authState ->
        when (authState) {
            is AuthState.Checking -> switchVisibilities(true)
            is AuthState.None -> switchVisibilities(login = true)
            is AuthState.AuthError -> {
                switchVisibilities(login = true)
                Snackbar.make(
                    binding.root,
                    R.string.error_signing_in,
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.retry) { signIn() }.show()
            }
            is AuthState.Authenticated -> {
                (requireActivity() as MainActivity).loginSuccessful()
                authViewModel.performUsernameCheck()
            }
            is AuthState.Validating -> validateUser()
            is AuthState.ValidSession -> fetchData()
            is AuthState.ValidationError -> {
            }
        }
    }

    private fun validateUser() {
        val directions = HomeFragmentDirections.actionHomeFragmentToCreateUsernameFragment()
        findNavController().navigate(directions)
    }

    private val TAG = "HomeFragment"
    private fun fetchData() = mainViewModel.getChats().observe(viewLifecycleOwner) {
        listAdapter.submitList(it)
        Log.d(TAG, "fetchData: $it")
        switchVisibilities(recyclerView = true, fab = true)
    }

    private fun switchVisibilities(progress: Boolean = false, login: Boolean = false, recyclerView: Boolean = false, fab: Boolean = false) =
        binding.apply {
            progressBar.isVisible = progress
            loginBtn.isVisible = login
            chatsRecyclerView.isVisible = recyclerView
            newChatBtn.isVisible = fab
        }

    private fun initSelectionCapabilities() {
        tracker = SelectionTracker.Builder<Long>(
            "HomeFragment Selection Id",
            binding.chatsRecyclerView,
            ItemIdKeyProvider(binding.chatsRecyclerView),
            ItemLookup(binding.chatsRecyclerView),
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

    private val actionItemClickListener: OnActionItemClickListener = object : OnActionItemClickListener {
        override fun onSelectAllPressed() {
            tracker.clearSelection()
            (0 until mainViewModel.chatsCount).forEach { tracker.select(it.toLong()) }
        }

        override fun onDeletePressed() {
            val selectedPositions = tracker.selection.toList()
            mainViewModel.deleteChatsWithPositions(selectedPositions)
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
                return (recyclerView.getChildViewHolder(view) as ChatPreviewsListAdapter.ChatPreviewsListViewHolder).getItemDetails()
            }
            return null
        }
    }
}