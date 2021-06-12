package com.example.myboards.ui.core.postsPresentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myboards.data.GlideServiceImpl
import com.example.myboards.domain.model.Post
import com.example.myboards.ui.boardDetail.BoardDetailViewModel

class PostCollectionAdapter(
    fm: FragmentManager,
    lifeCycle: Lifecycle,
    private val postList: List<Post>,
    private val vm: BoardDetailViewModel
) :
    FragmentStateAdapter(fm, lifeCycle) {
    override fun getItemCount(): Int = postList.size

    override fun createFragment(position: Int): Fragment {
        val post: Post = postList[position]
        val fragment = PostFragment(vm, post)
        fragment.arguments = Bundle().apply {
            putParcelable(ARG_POST, post)
        }
        return fragment
    }


}