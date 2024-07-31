package com.example.feedbackapp.adapter
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SimpleGridSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = spacing
        outRect.right = spacing
        outRect.top = spacing
        outRect.bottom = spacing

        // 注意：这将为所有边缘（包括RecyclerView的顶部和底部）添加间距
        // 如果你的RecyclerView设置了clipToPadding=false并且padding足够大，这可能不是你想要的效果
        // 在这种情况下，你可能需要为RecyclerView的padding单独设置值，并在ItemDecoration中避免为顶部和底部的item添加额外的bottom和top间距
    }
}