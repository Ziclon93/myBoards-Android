package com.example.myboards.ui.core.carousel.verticalBoardCarousel

import androidx.recyclerview.widget.DiffUtil
import com.example.myboards.domain.model.Board

object VerticalBoardDiffCallback : DiffUtil.ItemCallback<Board>() {
    override fun areItemsTheSame(oldItem: Board, newItem: Board): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Board, newItem: Board): Boolean {
        return oldItem.id == newItem.id
    }
}