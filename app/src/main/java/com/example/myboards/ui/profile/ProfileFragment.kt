package com.example.myboards.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.myboards.R
import com.example.myboards.databinding.FragmentExploreBinding
import com.example.myboards.databinding.FragmentProfileBinding
import com.example.myboards.domain.model.Board
import com.example.myboards.domain.model.Image
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.EventObserver
import com.example.myboards.ui.core.ImageExpected
import com.example.myboards.ui.MainActivity
import com.example.myboards.ui.core.BindingAppFragment
import com.example.myboards.ui.core.carousel.verticalBoardCarousel.VerticalBoardsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_new_board.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


@AndroidEntryPoint
class ProfileFragment : BindingAppFragment<FragmentProfileBinding>() {

    private val vm: ProfileViewModel by viewModels()

    private var expectedImage: ImageExpected = ImageExpected.NONE
    private var newBoardIcon: Image? = null
    private var tagTextFieldDisabled: Boolean = false
    private var lastBoardIdShowed: Int? = null
    private var binding: FragmentProfileBinding? = null


    override fun onCreateBinding(container: ViewGroup?): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater, container, false)


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindingCreated(binding: FragmentProfileBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.apply {
            viewModel = vm
            lifecycleOwner = this@ProfileFragment

            dialogBackground.setOnTouchListener { _, _ ->
                newBoardDialogShown = false
                true
            }

            editProfileBtn.setOnClickListener {
                expectedImage = ImageExpected.PROFILE_ICON
                (activity as MainActivity).openGallery()
            }

            logoutBtn.setOnClickListener {
                vm.authServiceImpl.clearAuthCredentials()
                (activity as MainActivity).finish()
            }


            val boardsAdapter =
                VerticalBoardsAdapter(vm.glideServiceImpl) { board -> adapterOnClick(board) }
            boardCarousel.adapter = boardsAdapter

            vm.state.boardList.observe(this@ProfileFragment, {
                it?.let {
                    boardsAdapter.submitList(it as MutableList<Board>)
                }
            })

            vm.state.iconUrl.observe(this@ProfileFragment, {
                if (it.isNotEmpty()) {
                    context?.let { _ ->
                        vm.glideServiceImpl.showFromFireBaseUri(
                            it,
                            profileIcon
                        )
                    }
                } else {
                    profileIcon.setImageResource(R.drawable.ic_account_icon)
                }
                profileIconSpinner.visibility = View.GONE
            })

            (activity as MainActivity).currentImageUpdated.observe(
                this@ProfileFragment,
                EventObserver {
                    if (it is DelayedResult.Success) {
                        context?.let { _ ->
                            when (expectedImage) {
                                ImageExpected.PROFILE_ICON -> {
                                    vm.showImageWithBitmapCircleCrop(
                                        it.value.bitmap,
                                        profileIcon
                                    )
                                    profileIconSpinner.visibility = View.GONE
                                    vm.updateProfileImage(it.value)
                                }
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

            //New Board
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
            vm.state.newBoardResult.observe(this@ProfileFragment, EventObserver {
                if (it is DelayedResult.Success) {
                    lastBoardIdShowed?.let { id ->
                        if (id != it.value.id) {
                            val action =
                                ProfileFragmentDirections.actionProfileFragmentToBoardDetailFragment(
                                    it.value.id
                                )
                            (activity as MainActivity).navigatorAction(action)
                            vm.requestBoardList()
                            lastBoardIdShowed = it.value.id
                        }
                    } ?: run {
                        val action =
                            ProfileFragmentDirections.actionProfileFragmentToBoardDetailFragment(it.value.id)
                        (activity as MainActivity).navigatorAction(action)
                        vm.requestBoardList()
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

            newBoardPreviewGalleryBtn.setOnClickListener {
                expectedImage = ImageExpected.NEW_BOARD
                (activity as MainActivity).openGallery()
            }
        }
        this.binding = binding
    }

    private fun adapterOnClick(board: Board) {
        val action = ProfileFragmentDirections.actionProfileFragmentToBoardDetailFragment(board.id)
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