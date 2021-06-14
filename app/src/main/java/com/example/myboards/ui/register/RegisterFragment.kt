package com.example.myboards.ui.register

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.myboards.data.model.auth.AuthCredentials
import com.example.myboards.databinding.FragmentRegisterBinding
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.EventObserver
import com.example.myboards.ui.LauncherActivity
import com.example.myboards.ui.core.BindingAppFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : BindingAppFragment<FragmentRegisterBinding>() {

    private val vm: RegisterViewModel by viewModels()

    override fun onCreateBinding(container: ViewGroup?): FragmentRegisterBinding =
        FragmentRegisterBinding.inflate(layoutInflater, container, false)

    override fun onBindingCreated(binding: FragmentRegisterBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        binding.apply {
            viewModel = vm
            lifecycleOwner = viewLifecycleOwner

            vm.state.isLoading.observe(this@RegisterFragment) {
                if (it) {
                    loading.visibility = View.VISIBLE
                } else {
                    loading.visibility = View.GONE
                }
            }
            registerButton.setOnClickListener {
                vm.register()
            }

            vm.state.registerResult.observe(this@RegisterFragment, EventObserver {
                if (it is DelayedResult.Success) {

                    vm.authServiceImpl.setAuthCredentials(
                        AuthCredentials(
                            userKey =  it.value.apiKey,
                            username = vm.state.username.value,
                            password = vm.state.password.value
                        )
                    )
                    (activity as LauncherActivity).toMain()
                }
            })

        }


    }


}