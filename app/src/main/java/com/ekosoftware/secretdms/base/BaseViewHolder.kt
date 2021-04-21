package com.ekosoftware.secretdms.base

import android.view.View
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(item: T) {}
    open fun bind(item: T, b1: Boolean, b2: Boolean) {}
    abstract fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long>
}