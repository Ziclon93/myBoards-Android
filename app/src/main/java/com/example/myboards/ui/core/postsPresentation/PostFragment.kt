package com.example.myboards.ui.core.postsPresentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myboards.R
import com.example.myboards.data.GlideServiceImpl
import com.example.myboards.domain.model.Post

const val ARG_POST = "post"

class PostFragment(private val glideService: GlideServiceImpl) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_post_detail_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_POST) }?.apply {
            val post: Post = getParcelable(ARG_POST)!!
            glideService.showSquareFromFireBaseUriCenterCrop(
                post.resourceUrl,
                view.findViewById(R.id.post_image)
            )
        }
    }
}