package com.example.myboards.ui.boardDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myboards.R
import com.example.myboards.databinding.FragmentBoardDetailBinding
import com.example.myboards.domain.model.Image
import com.example.myboards.support.DelayedResult
import com.example.myboards.support.EventObserver
import com.example.myboards.ui.MainActivity
import com.example.myboards.ui.core.BindingAppFragment
import com.example.myboards.ui.core.ImageExpected
import com.example.myboards.ui.core.postPoolView.PostItem
import com.example.myboards.ui.core.postsPresentation.CollectorFragment
import com.example.myboards.ui.core.postsPresentation.PostCollectionAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_new_board.*
import kotlinx.android.synthetic.main.dialog_post_preview.*
import kotlinx.android.synthetic.main.fragment_board_detail.*
import java.util.*

@AndroidEntryPoint
class BoardDetailFragment : BindingAppFragment<FragmentBoardDetailBinding>() {

    private var expectedImage: ImageExpected = ImageExpected.NONE
    private var currentPostImage: Image? = null
    private val vm: BoardDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        vm.setBoard(arguments?.getInt("boardId")!!)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateBinding(container: ViewGroup?): FragmentBoardDetailBinding =
        FragmentBoardDetailBinding.inflate(layoutInflater, container, false)

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindingCreated(
        binding: FragmentBoardDetailBinding,
        savedInstanceState: Bundle?
    ) {
        super.onBindingCreated(binding, savedInstanceState)
        binding.apply {
            viewModel = vm
            lifecycleOwner = this@BoardDetailFragment

            boardDetailDialogBackground.setOnTouchListener { _, _ ->
                postPreviewIsVisible = false
                postListDetailIsVisible = false
                true
            }

            newPostBtn.setOnClickListener {
                expectedImage = ImageExpected.NEW_POST
                (activity as MainActivity).openGallery()
            }

            postPostBtn.setOnClickListener {
                vm.postPost(currentPostImage!!)
                postPreviewIsVisible = false
            }

            vm.state.boardResult.observe(this@BoardDetailFragment, EventObserver {
                if (it is DelayedResult.Success) {
                    postContainer.removeAllViews()

                    for (post in it.value.postList) {

                        val postView: PostItem =
                            layoutInflater.inflate(R.layout.post_item, null, false) as PostItem
                        postView.id = View.generateViewId()
                        postView.setPost(post)
                        postContainer.addView(postView)

                        vm.loadPostResourceImage(
                            post.resourceUrl,
                            postView.findViewById(R.id.post_image)
                        )
                        postView.setConstraints(postContainer)

                        val postList = it.value.postList
                        postView.setOnClickListener {

                            postDetailDialog.postsViewpager.adapter =
                                PostCollectionAdapter(
                                    childFragmentManager,
                                    this@BoardDetailFragment.lifecycle,
                                    postList,
                                    vm.glideServiceImpl
                                )
                            postDetailDialog.postsViewpager.post {
                                postDetailDialog.postsViewpager.currentItem = postList.indexOf(post)
                            }
                            postListDetailIsVisible = true
                        }

                    }
                }


            })

            (activity as MainActivity).currentImageUpdated.observe(
                this@BoardDetailFragment,
                EventObserver {
                    if (it is DelayedResult.Success) {
                        context?.let { ctx ->
                            when (expectedImage) {
                                ImageExpected.NEW_POST -> {
                                    currentPostImage = it.value
                                    postPreviewIsVisible = true
                                    vm.showPostPreviewImage(
                                        it.value.bitmap,
                                        postPreviewImageView
                                    )
                                    vm.state.newPostUrl.value = it.value.path
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
}