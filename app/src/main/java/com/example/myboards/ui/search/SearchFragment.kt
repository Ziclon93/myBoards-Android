package com.example.myboards.ui.search

import android.view.ViewGroup
import com.example.myboards.databinding.FragmentSearchBinding
import com.example.myboards.ui.core.BindingAppFragment

class SearchFragment : BindingAppFragment<FragmentSearchBinding>() {

    override fun onCreateBinding(container: ViewGroup?): FragmentSearchBinding =
        FragmentSearchBinding.inflate(layoutInflater, container, false)
}