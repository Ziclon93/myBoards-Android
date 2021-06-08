package com.example.myboards.ui.core.postPoolView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.myboards.domain.model.Post

class PostItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    override fun onViewRemoved(view: View?) {
        if (this.parent != null) {
            (this.parent as ViewGroup).removeView(this)
        }
        super.onViewRemoved(view)
    }

    private var post: Post? = null

    fun setPost(post: Post) {
        this.post = post
    }

    fun setConstraints(parentView: ConstraintLayout) {
        post?.let {
            val set = ConstraintSet()
            set.clone(parentView)

            set.connect(id, ConstraintSet.TOP, parentView.id, ConstraintSet.TOP, 0)
            set.connect(id, ConstraintSet.BOTTOM, parentView.id, ConstraintSet.BOTTOM, 0)
            set.connect(id, ConstraintSet.START, parentView.id, ConstraintSet.START, 0)
            set.connect(id, ConstraintSet.END, parentView.id, ConstraintSet.END, 0)
            set.setHorizontalBias(this.id, post!!.x)
            set.setVerticalBias(this.id, post!!.y)
            set.setRotation(id, post!!.rotation.toFloat())

            set.applyTo(parentView)
        }
    }


}