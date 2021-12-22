package com.example.myboards.ui.followed

import android.os.Bundle
import android.view.ViewGroup
import com.example.myboards.databinding.FragmentFollowedBinding
import com.example.myboards.ui.core.BindingAppFragment

class FollowedFragment : BindingAppFragment<FragmentFollowedBinding>() {

    override fun onCreateBinding(container: ViewGroup?): FragmentFollowedBinding =
        FragmentFollowedBinding.inflate(layoutInflater, container, false)

    override fun onBindingCreated(binding: FragmentFollowedBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        binding.apply {
            //viewModel = vm
            lifecycleOwner = viewLifecycleOwner

            childFragmentManager
        }
    }
}