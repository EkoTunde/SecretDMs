package com.ekosoftware.secretdms.base

import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseListAdapter<T>(callback: DiffUtil.ItemCallback<T>) : ListAdapter<T, BaseViewHolder<*>>(callback) {
    protected var selectionTracker: SelectionTracker<Long>? = null

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.selectionTracker = tracker
    }
    override fun getItemId(position: Int): Long = position.toLong()
}