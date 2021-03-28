package com.ekosoftware.secretdms.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.Strings
import com.ekosoftware.secretdms.base.AuthState
import com.ekosoftware.secretdms.base.Resource
import com.ekosoftware.secretdms.databinding.FragmentCreateUsernameBinding
import com.ekosoftware.secretdms.presentation.AuthenticationViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*


class CreateUsernameFragment : Fragment(R.layout.fragment_create_username) {
    private lateinit var binding: FragmentCreateUsernameBinding

    private val authViewModel: AuthenticationViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateUsernameBinding.bind(view)
        binding.doneBtn.setOnClickListener {
            if (isValid) {
                authViewModel.userExists.removeObservers(viewLifecycleOwner)
                /*authViewModel.saveUserData(
                    binding.username.editText!!.text.toString().trim()
                        .toLowerCase(Locale.getDefault())
                )*/
                authViewModel.save()
            }
        }
        binding.username.editText?.doOnTextChanged { text, _, _, _ ->
            if (text?.length ?: 0 > 3) {
                //authViewModel.submitUsername(
                binding.progressBar.isVisible = true
                authViewModel.setUsername(text.toString().trim().toLowerCase(Locale.getDefault()))
                //)
                binding.username.error = null
            } else {
                binding.username.error = Strings.get(R.string.username_min_chars_warning)
                binding.doneBtn.isEnabled = false
            }
        }

        fetchUserExists()
        fetchUserIsAuthenticated()
    }

    private var isValid = false

    private fun fetchUserExists() = authViewModel.userExists.observe(viewLifecycleOwner) { result ->
        when (result) {
            is Resource.Error -> binding.apply {
                Snackbar.make(
                    root,
                    R.string.username_validation_error,
                    Snackbar.LENGTH_LONG
                ).show()
                progressBar.isVisible = false
                doneBtn.isEnabled = false
            }
            is Resource.Loading -> binding.doneBtn.isEnabled = false
            is Resource.Success -> binding.apply {
                progressBar.isVisible = false
                isValid = result.data ?: false
                doneBtn.isEnabled = isValid
                username.apply {
                    boxStrokeColor = resources.getColor(
                        if (isValid) R.color.ok_green else R.color.not_ok_red,
                        null
                    )
                    if (!isValid) error = Strings.get(R.string.username_not_available)
                }
            }
        }
    }

    private fun fetchUserIsAuthenticated() =
        authViewModel.isUserAuthenticated().observe(viewLifecycleOwner) {
            when (it) {
                is AuthState.ValidSession -> findNavController().navigateUp()
                else -> {
                }
            }
        }

    // Disables back button
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                // Leave empty do disable back press.
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,  // LifecycleOwner
            callback
        )
    }
}