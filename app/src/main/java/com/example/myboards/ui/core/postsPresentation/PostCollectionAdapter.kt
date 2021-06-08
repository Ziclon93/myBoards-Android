package com.example.myboards.ui.core.postsPresentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myboards.data.GlideServiceImpl
import com.example.myboards.domain.model.Post

class PostCollectionAdapter(
    fm: FragmentManager,
    lifeCycle: Lifecycle,
    private val postList: List<Post>,
    private val glideService: GlideServiceImpl
) :
    FragmentStateAdapter(fm, lifeCycle) {
    override fun getItemCount(): Int = postList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = PostFragment(glideService)
        val post: Post = postList[position]
        fragment.arguments = Bundle().apply {
            putParcelable(ARG_POST, post)
        }
        return fragment
    }


}