package com.example.myboards.ui.explore

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.myboards.R
import com.example.myboards.databinding.FragmentExploreBinding
import com.example.myboards.domain.model.Board
import com.example.myboards.domain.model.Image
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.EventObserver
import com.example.myboards.ui.MainActivity
import com.example.myboards.ui.core.BindingAppFragment
import com.example.myboards.ui.core.ImageExpected
import com.example.myboards.ui.core.carousel.boardCarousel.BoardsAdapter
import com.example.myboards.ui.profile.ProfileFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_new_board.*


@AndroidEntryPoint
class ExploreFragment : BindingAppFragment<FragmentExploreBinding>() {

    private val vm: ExploreViewModel by viewModels()
    private var tagTextFieldDisabled: Boolean = false
    private var lastBoardIdShowed: Int? = null
    private var expectedImage: ImageExpected = ImageExpected.NONE
    private var newBoardIcon: Image? = null
    private var binding: FragmentExploreBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        vm.requestBoardList()
        vm.requestTagBoardsList()

        super.onCreate(savedInstanceState)
    }

    override fun onCreateBinding(container: ViewGroup?): FragmentExploreBinding =
        FragmentExploreBinding.inflate(layoutInflater, container, false)

    @SuppressLint("ClickableViewAccessibility")
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

            val tagBoardsAdapter1 =
                BoardsAdapter(vm.glideServiceImpl) { board -> adapterOnClick(board) }
            tagBoardsCarousel1.adapter = tagBoardsAdapter1

            vm.state.tagBoardsList.observe(this@ExploreFragment, {
                it?.let {
                    if (it.isNotEmpty()) {
                        tagBoardsTitle1.text = "#" + it[0].tagName
                        tagBoardsAdapter1.submitList(it[0].boardList as MutableList<Board>)
                    }
                }
            })

            val tagBoardsAdapter2 =
                BoardsAdapter(vm.glideServiceImpl) { board -> adapterOnClick(board) }
            tagBoardsCarousel2.adapter = tagBoardsAdapter2

            vm.state.tagBoardsList.observe(this@ExploreFragment, {
                it?.let {
                    if (it.size >= 2) {
                        tagBoardsTitle2.text = "#" + it[1].tagName
                        tagBoardsAdapter2.submitList(it[1].boardList as MutableList<Board>)
                    }
                }
            })

            //New Board
            dialogBackground.setOnTouchListener { _, _ ->
                newBoardDialogShown = false
                true
            }

            newBoardButton.setOnClickListener {
                newBoardDialogShown = true
            }

            deleteTagsButton.setOnClickListener {
                vm.state.tagsList.value.clear()
                vm.state.tagsText.value = ""
                tagTextView.text = ""
            }
            newBoardTagTextField.doOnTextChanged { text, _, _, _ ->
                when {
                    text!!.contains(' ', false) -> {
                        tagsError.text = "Text must be just one word"
                        newBoardTagTextField.setTextColor(resources.getColor(R.color.colorSecondary))
                        tagsError.visibility = View.VISIBLE
                        tagTextFieldDisabled = true
                    }
                    text.length > 20 -> {
                        tagsError.text = "Text must be shorter"
                        newBoardTagTextField.setTextColor(resources.getColor(R.color.colorSecondary))
                        tagsError.visibility = View.VISIBLE
                        tagTextFieldDisabled = true
                    }
                    else -> {
                        tagsError.visibility = View.GONE
                        newBoardTagTextField.setTextColor(resources.getColor(R.color.black))
                        tagTextFieldDisabled = false
                    }
                }
            }
            vm.state.newBoardResult.observe(this@ExploreFragment, EventObserver {
                if (it is DelayedResult.Success) {
                    lastBoardIdShowed?.let { id ->
                        if (id != it.value.id) {
                            val action =
                                ExploreFragmentDirections.actionExploreFragmentToBoardDetailFragment(
                                    it.value.id
                                )
                            (activity as MainActivity).navigatorAction(action)
                            vm.requestBoardList()
                            vm.requestTagBoardsList()
                            lastBoardIdShowed = it.value.id
                        }
                    } ?: run {
                        val action =
                            ExploreFragmentDirections.actionExploreFragmentToBoardDetailFragment(it.value.id)
                        (activity as MainActivity).navigatorAction(action)
                        vm.requestBoardList()
                        vm.requestTagBoardsList()
                        lastBoardIdShowed = it.value.id
                    }
                }
            })

            newTagBtn.setOnClickListener {
                if (!tagTextFieldDisabled && newBoardTagTextField.text!!.isNotEmpty()) {
                    if (vm.state.tagsList.value.size <= 4) {
                        vm.addTag(newBoardTagTextField.text.toString())
                        newBoardTagTextField.text?.clear()
                        tagTextView.text = vm.state.tagsText.value
                    }
                }
            }

            postNewBoardBtn.setOnClickListener {
                if (newBoardTitleTextField.text.toString().isEmpty()) {
                    titleError.text = "Title cant be empty"
                    titleError.visibility = View.VISIBLE

                } else {
                    titleError.visibility = View.GONE

                    vm.postNewBoard(
                        newBoardTitleTextField.text.toString(),
                        newBoardIcon
                    )
                    newBoardDialogShown = false
                    newBoardIcon = null
                }
            }

            (activity as MainActivity).currentImageUpdated.observe(
                this@ExploreFragment,
                EventObserver {
                    if (it is DelayedResult.Success) {
                        context?.let { _ ->
                            when (expectedImage) {
                                ImageExpected.NEW_BOARD -> {
                                    newBoardIcon = it.value
                                    vm.showImageWithBitmap(it.value.bitmap, newBoardPreview)
                                }
                                else -> {
                                }
                            }
                        }
                        expectedImage = ImageExpected.NONE
                    }
                })

            newBoardPreviewGalleryBtn.setOnClickListener {
                expectedImage = ImageExpected.NEW_BOARD
                (activity as MainActivity).openGallery()
            }
        }
        this.binding = binding
    }

    private fun adapterOnClick(board: Board) {
        val action =
            ExploreFragmentDirections.actionExploreFragmentToBoardDetailFragment(board.id)
        (activity as MainActivity).navigatorAction(action)
    }

    fun onBackPressed() {
        binding?.let {
            it.newBoardDialogShown?.let { _ ->
                if (it.newBoardDialogShown!!) {
                    it.newBoardDialogShown = false
                } else {
                    (activity as MainActivity).moveAppToBackground()
                }
            } ?: run {
                (activity as MainActivity).moveAppToBackground()
            }
        } ?: run {
            (activity as MainActivity).moveAppToBackground()
        }
    }
}