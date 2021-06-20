package com.example.myboards.ui.core.carousel.boardCarousel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myboards.R
import com.example.myboards.data.GlideServiceImpl
import com.example.myboards.domain.model.Board

class BoardsAdapter(
    private val glideServiceImpl: GlideServiceImpl,
    private val onClick: (Board) -> Unit,
) :
    ListAdapter<Board, BoardsAdapter.BoardViewHolder>(BoardDiffCallback) {

    class BoardViewHolder(
        itemView: View,
        val onClick: (Board) -> Unit,
        private val glideServiceImpl: GlideServiceImpl
    ) :
        RecyclerView.ViewHolder(itemView) {


        private var currentBoard: Board? = null

        init {
            itemView.setOnClickListener {
                currentBoard?.let {
                    onClick(it)
                }
            }
        }

        /* Bind flower name and image. */
        fun bind(board: Board) {
            currentBoard = board
            val boardIconImageView: ImageView = itemView.findViewById(R.id.board_icon)
            val boardTitleTextView: TextView = itemView.findViewById(R.id.board_title)
            val imageLoading: ProgressBar = itemView.findViewById(R.id.board_loading)

            if (board.iconUrl.isEmpty()) {
                boardIconImageView.setImageResource(R.drawable.ic_default_board_icon)
                imageLoading.visibility = View.GONE
            } else {
                glideServiceImpl.showSquareFromFireBaseUriCenterCrop(board.iconUrl, boardIconImageView)
                imageLoading.visibility = View.GONE
            }

        }
    }


    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.board_item, parent, false)
        return BoardViewHolder(view, onClick, glideServiceImpl)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val board = getItem(position)
        holder.bind(board)

    }
}

