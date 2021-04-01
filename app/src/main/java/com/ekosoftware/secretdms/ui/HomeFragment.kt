package com.ekosoftware.secretdms.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.MainActivity
import com.ekosoftware.secretdms.base.AuthState
import com.ekosoftware.secretdms.databinding.FragmentHomeBinding
import com.ekosoftware.secretdms.presentation.AuthenticationViewModel
import com.ekosoftware.secretdms.presentation.MainViewModel
import com.ekosoftware.secretdms.ui.adapters.ChatPreviewsListAdapter
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val authViewModel: AuthenticationViewModel by activityViewModels()

    private val listAdapter: ChatPreviewsListAdapter = ChatPreviewsListAdapter {
        val directions = HomeFragmentDirections.actionHomeFragmentToMessageFragment(it.friendId!!)
        findNavController().navigate(directions)
    }

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
        fetchAuthenticationData()
        initViews()
    }

    private fun initViews() = binding.run {
        loginBtn.setOnClickListener {
            signIn()
        }
        chatsRecyclerView.apply {
            adapter = listAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        newChatBtn.setOnClickListener {
            val directions =
                HomeFragmentDirections.actionHomeFragmentToDialogNewChatDialogFragment()
            findNavController().navigate(directions)
        }
    }

    private fun fetchAuthenticationData() =
        authViewModel.isUserAuthenticated().observe(viewLifecycleOwner) { authState ->
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

    private fun fetchData() = mainViewModel.getChats().observe(viewLifecycleOwner) {
        listAdapter.submitList(it)
        switchVisibilities(recyclerView = true, fab = true)
    }

    private fun switchVisibilities(
        progress: Boolean = false,
        login: Boolean = false,
        recyclerView: Boolean = false,
        fab: Boolean = false
    ) = binding.apply {
        progressBar.isVisible = progress
        loginBtn.isVisible = login
        chatsRecyclerView.isVisible = recyclerView
        newChatBtn.isVisible = fab
    }
}