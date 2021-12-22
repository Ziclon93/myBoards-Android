package com.example.myboards.ui.core.carousel.verticalBoardCarousel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myboards.R
import com.example.myboards.data.GlideServiceImpl
import com.example.myboards.domain.model.Board

class VerticalBoardsAdapter(
    private val glideServiceImpl: GlideServiceImpl,
    private val onClick: (Board) -> Unit,
) :
    ListAdapter<Board, VerticalBoardsAdapter.BoardViewHolder>(VerticalBoardDiffCallback) {

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

        fun bind(board: Board) {
            currentBoard = board
            val boardIconImageView: ImageView = itemView.findViewById(R.id.board_icon)
            val boardTitleTextView: TextView = itemView.findViewById(R.id.board_title)
            val boardValorationTextView: TextView =
                itemView.findViewById(R.id.board_valoration_text)

            if (currentBoard!!.iconUrl.isBlank()) {
                boardIconImageView.setImageResource(R.drawable.ic_default_board_icon)
            } else {
                glideServiceImpl.showSquareFromFireBaseUriCenterCrop(
                    currentBoard!!.iconUrl,
                    boardIconImageView
                )
            }

            if (board.valoration < 0) {
                itemView.findViewById<ImageView>(R.id.board_valoration_icon)
                    .setImageResource(R.drawable.ic_negative_arrow)
            } else {
                itemView.findViewById<ImageView>(R.id.board_valoration_icon)
                    .setImageResource(R.drawable.ic_positive_arrow)
            }
            boardValorationTextView.text = board.valoration.toString()
            boardTitleTextView.text = board.title
        }

        fun unBind() {
            val boardIconImageView: ImageView = itemView.findViewById(R.id.board_icon)
            glideServiceImpl.clearImage(boardIconImageView)
        }
    }


    /* Creates and inflates view and return FlowerViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tile_board, parent, false)
        return BoardViewHolder(view, onClick, glideServiceImpl)
    }

    /* Gets current flower and uses it to bind view. */
    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val board = getItem(position)
        holder.bind(board)

    }

    override fun onViewRecycled(holder: BoardViewHolder) {
        holder.unBind()
        super.onViewRecycled(holder)
    }
}

