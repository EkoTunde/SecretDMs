package com.ekosoftware.secretdms.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.resources.Strings
import com.ekosoftware.secretdms.databinding.FragmentNewChatDialogBinding
import com.ekosoftware.secretdms.presentation.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NewChatDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentNewChatDialogBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewChatDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeDialogButton.setOnClickListener { this.dismiss() }
        binding.createChatButton.setOnClickListener {
            val name = binding.name.editText?.text.toString()
            if (name.isNotEmpty()) {
                mainViewModel.newChat(name)
                val directions = NewChatDialogFragmentDirections.newChatDialogToChatFragment(name)
                findNavController().navigate(directions)
                this.dismiss()
            } else {
                binding.name.error = Strings.get(R.string.must_indicate_a_name)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}