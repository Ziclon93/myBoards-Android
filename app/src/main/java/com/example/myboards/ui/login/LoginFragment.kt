package com.example.myboards.ui.login

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.myboards.R
import com.example.myboards.data.model.auth.AuthCredentials
import com.example.myboards.databinding.FragmentLoginBinding
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.EventObserver
import com.example.myboards.ui.LauncherActivity
import com.example.myboards.ui.core.BindingAppFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BindingAppFragment<FragmentLoginBinding>() {

    private val vm: LoginViewModel by viewModels()

    override fun onCreateBinding(container: ViewGroup?): FragmentLoginBinding =
        FragmentLoginBinding.inflate(layoutInflater, container, false)

    override fun onBindingCreated(binding: FragmentLoginBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        binding.apply {
            viewModel = vm
            lifecycleOwner = viewLifecycleOwner

            loginButton.setOnClickListener {
                vm.login()
            }

            registerButton.setOnClickListener {
                requireNotNull(view?.findNavController()).navigate(R.id.action_loginFragment_to_registerFragment)
            }

            vm.state.isLoading.observe(this@LoginFragment) {
                if (it) {
                    loading.visibility = View.VISIBLE
                } else {
                    loading.visibility = View.GONE
                }
            }

            vm.state.loginResult.observe(this@LoginFragment, EventObserver {
                if (it is DelayedResult.Success) {

                    vm.authServiceImpl.setAuthCredentials(
                        AuthCredentials(
                            userKey = it.value.apiKey,
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