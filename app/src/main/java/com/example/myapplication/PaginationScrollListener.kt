package com.example.myapplication

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener(var layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    var isLoading = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val childCount = layoutManager.childCount
        val itemCount = layoutManager.itemCount
        val firstItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading
            && childCount + firstItemPosition >= itemCount
            && firstItemPosition >= 0) {
            isLoading = true
            addNextPage()
        }
    }

    abstract fun addNextPage()

}