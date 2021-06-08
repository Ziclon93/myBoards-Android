package com.example.myboards.ui.explore

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.myboards.databinding.FragmentExploreBinding
import com.example.myboards.domain.model.Board
import com.example.myboards.ui.MainActivity
import com.example.myboards.ui.core.BindingAppFragment
import com.example.myboards.ui.core.carousel.BoardsAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExploreFragment : BindingAppFragment<FragmentExploreBinding>() {

    private val vm: ExploreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        vm.requestBoardList()

        super.onCreate(savedInstanceState)
    }

    override fun onCreateBinding(container: ViewGroup?): FragmentExploreBinding =
        FragmentExploreBinding.inflate(layoutInflater, container, false)

    override fun onBindingCreated(binding: FragmentExploreBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        binding.apply {
            viewModel = vm
            lifecycleOwner = viewLifecycleOwner

            val boardsAdapter =
                BoardsAdapter(vm.glideServiceImpl) { board -> adapterOnClick(board) }
            boardCarousel.adapter = boardsAdapter

            vm.state.boardList.observe(this@ExploreFragment, {
                it?.let {
                    boardsAdapter.submitList(it as MutableList<Board>)
                }
            })

        }
    }

    private fun adapterOnClick(board: Board) {
        val action =
            ExploreFragmentDirections.actionExploreFragmentToBoardDetailFragment(board.id)
        (activity as MainActivity).navigatorAction(action)
    }
}