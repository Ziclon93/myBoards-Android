package com.example.myboards.ui.core.postsPresentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.myboards.R
import com.example.myboards.domain.model.Post
import com.example.myboards.ui.boardDetail.BoardDetailViewModel

const val ARG_POST = "post"

class PostFragment(private val vm: BoardDetailViewModel, private var post: Post) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_post_detail_image, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (post.userLikeCode == 1) {
            view.findViewById<ImageButton>(R.id.likeBtn).background =
                resources.getDrawable(R.drawable.ic_like_background_focused, null)
        } else if (post.userLikeCode == 2) {
            view.findViewById<ImageButton>(R.id.dislikeBtn).background =
                resources.getDrawable(R.drawable.ic_dislike_background_focused, null)
        } else {
            view.findViewById<ImageButton>(R.id.likeBtn).setOnClickListener {
                if (post.userLikeCode == 0) {
                    it.background =
                        resources.getDrawable(R.drawable.ic_like_background_focused, null)
                    vm.likePost(post.id)
                    post.userLikeCode = 1
                }
            }
            view.findViewById<ImageButton>(R.id.dislikeBtn).setOnClickListener {
                if (post.userLikeCode == 0) {
                    it.background =
                        resources.getDrawable(R.drawable.ic_dislike_background_focused, null)
                    vm.dislikePost(post.id)
                    post.userLikeCode = 2
                }
            }
        }
        vm.glideServiceImpl.showSquareFromFireBaseUriCenterCrop(
            post.resourceUrl,
            view.findViewById(R.id.post_image)
        )

    }
}