package com.ekosoftware.secretdms.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.databinding.FragmentAccountBinding
import com.ekosoftware.secretdms.presentation.AuthenticationViewModel
import com.google.android.material.snackbar.Snackbar


class AccountFragment : Fragment(R.layout.fragment_account) {

    private lateinit var binding: FragmentAccountBinding

    private val authViewModel: AuthenticationViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAccountBinding.bind(view).apply {
            logoutBtn.setOnClickListener {
                authViewModel.signOut()
                findNavController().navigateUp()
            }
        }
        fetchUserData()
    }

    private val TAG = "AboutFragment"
    private fun fetchUserData() =
        authViewModel.getUserData().observe(viewLifecycleOwner) { result ->
            when (result) {
                is com.ekosoftware.secretdms.base.Resource.Error -> {
                    Log.d(TAG, "fetchUserData: ${result.message}")
                    switchMainViewsVisibilities(container = true)
                    Snackbar.make(binding.root, R.string.error_fetching_data, Snackbar.LENGTH_LONG)
                        .show()
                }
                is com.ekosoftware.secretdms.base.Resource.Loading -> switchMainViewsVisibilities(
                    true
                )
                is com.ekosoftware.secretdms.base.Resource.Success -> binding.apply {
                    val user = result.data!!
                    email.text = user.first
                    username.text = user.second
                    copyBtn.setOnClickListener {
                        val clipboard =
                            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("fresh text", user.second)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(
                            requireContext(),
                            R.string.copied_to_clipboard,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    switchMainViewsVisibilities(container = true)
                }
            }
        }

    private fun switchMainViewsVisibilities(pb: Boolean = false, container: Boolean = false) =
        binding.run {
            progressBar.isVisible = pb
            constraintContainer.isVisible = container
        }
}