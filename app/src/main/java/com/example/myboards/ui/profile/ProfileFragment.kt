package com.example.myboards.ui.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.myboards.R
import com.example.myboards.databinding.FragmentProfileBinding
import com.example.myboards.domain.model.Image
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.EventObserver
import com.example.myboards.ui.core.ImageExpected
import com.example.myboards.ui.MainActivity
import com.example.myboards.ui.core.BindingAppFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_new_board.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


@AndroidEntryPoint
class ProfileFragment : BindingAppFragment<FragmentProfileBinding>() {

    private val vm: ProfileViewModel by viewModels()

    private var expectedImage: ImageExpected = ImageExpected.NONE
    private lateinit var newBoardIcon: Image
    private var tagTextFieldDisabled: Boolean = false


    override fun onCreateBinding(container: ViewGroup?): FragmentProfileBinding =
        FragmentProfileBinding.inflate(layoutInflater, container, false)


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindingCreated(binding: FragmentProfileBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.apply {
            viewModel = vm
            lifecycleOwner = this@ProfileFragment

            dialogBackground.setOnTouchListener { v, event ->
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

            newBoardButton.setOnClickListener {
                newBoardDialogShown = true
            }

            newTagBtn.setOnClickListener {
                if (!tagTextFieldDisabled) {
                    if (vm.state.tagsList.value.size <= 5) {
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
                    vm.updateUrl(newBoardIcon)
                    titleError.visibility = View.GONE
                    var imagePath = ""
                    if ((::newBoardIcon.isInitialized)) imagePath = newBoardIcon.path

                    vm.postNewBoard(
                        newBoardTitleTextField.text.toString(),
                        imagePath,
                    )
                    newBoardDialogShown = false
                }
            }

            newBoardPreviewGalleryBtn.setOnClickListener {
                expectedImage = ImageExpected.NEW_BOARD
                (activity as MainActivity).openGallery()
            }

            vm.state.newBoardResult.observe(this@ProfileFragment, EventObserver {
                if (it is DelayedResult.Success) {
                    val action =
                        ProfileFragmentDirections.actionProfileFragmentToBoardDetailFragment(it.value.id)
                    (activity as MainActivity).navigatorAction(action)
                }
            })

            vm.state.iconUrl.observe(this@ProfileFragment, {
                if (it.isNotEmpty()) {
                    context?.let { it1 ->
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

            newBoardTagTextField.doOnTextChanged { text, start, before, count ->
                if (text!!.contains(' ', false)) {
                    tagsError.text = "Text must be just one word"
                    newBoardTagTextField.setTextColor(resources.getColor(R.color.colorSecondary))
                    tagsError.visibility = View.VISIBLE
                    tagTextFieldDisabled = true
                } else if (text.length > 20) {
                    tagsError.text = "Text must be shorter"
                    newBoardTagTextField.setTextColor(resources.getColor(R.color.colorSecondary))
                    tagsError.visibility = View.VISIBLE
                    tagTextFieldDisabled = true
                } else {
                    tagsError.visibility = View.GONE
                    newBoardTagTextField.setTextColor(resources.getColor(R.color.black))
                    tagTextFieldDisabled = false
                }
            }

            (activity as MainActivity).currentImageUpdated.observe(
                this@ProfileFragment,
                EventObserver {
                    if (it is DelayedResult.Success) {
                        context?.let { ctx ->
                            when (expectedImage) {
                                ImageExpected.PROFILE_ICON -> {
                                    vm.showImageWithBitmapCircleCrop(
                                        it.value.bitmap,
                                        profileIcon
                                    )
                                    profileIconSpinner.visibility = View.GONE
                                    vm.updateUrl(it.value)
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
        }
    }
    fun onBackButton() {
        println("HOLAAAAAAA")
    }
}