package com.ekosoftware.secretdms.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T, K, L>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T, param2: K, param3: L)
}