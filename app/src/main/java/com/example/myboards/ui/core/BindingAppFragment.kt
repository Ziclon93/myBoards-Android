package com.example.myboards.ui.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BindingAppFragment<B : ViewDataBinding> : Fragment() {

    private var viewBinding: B? = null

    abstract fun onCreateBinding(
        container: ViewGroup?
    ): B

    open fun onBindingCreated(
        binding: B,
        savedInstanceState: Bundle?
    ) {
    }

    open fun unbind(binding: B) {}

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = onCreateBinding(container)
        .also {
            viewBinding = it
        }
        .root

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindingCreated(requireNotNull(viewBinding), savedInstanceState)
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding?.let {
            unbind(it)
            it.unbind()
        }
        viewBinding = null
    }

}