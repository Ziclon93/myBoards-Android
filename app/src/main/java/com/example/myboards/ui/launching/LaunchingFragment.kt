package com.example.myboards.ui.launching

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.myboards.databinding.FragmentLaunchingBinding
import com.example.myboards.support.EventObserver
import com.example.myboards.ui.LauncherActivity
import com.example.myboards.ui.core.BindingAppFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaunchingFragment : BindingAppFragment<FragmentLaunchingBinding>() {

    private val vm: LaunchingViewModel by viewModels()

    override fun onCreateBinding(container: ViewGroup?): FragmentLaunchingBinding =
        FragmentLaunchingBinding.inflate(layoutInflater, container, false)

    override fun onBindingCreated(binding: FragmentLaunchingBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        binding.apply {
            viewModel = vm
            lifecycleOwner = viewLifecycleOwner

            vm.state.validAuth.observe(this@LaunchingFragment, EventObserver {
                if (it) {
                    (activity as LauncherActivity).toLogin()
                    (activity as LauncherActivity).toMain()
                } else {
                    (activity as LauncherActivity).toLogin()
                }
            })
        }
    }
}