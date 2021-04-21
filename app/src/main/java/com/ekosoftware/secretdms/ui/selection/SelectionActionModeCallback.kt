package com.ekosoftware.secretdms.ui.selection

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import com.ekosoftware.secretdms.R

class SelectionActionModeCallback(private val actionItemClickListener: OnActionItemClickListener) : ActionMode.Callback {

        private var mode: ActionMode? = null

        @MenuRes
        private var menuResId: Int = 0
        private var title: String? = null

        var isActive = false
            private set

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            this.mode = mode
            mode.menuInflater.inflate(menuResId, menu)
            mode.title = title
            isActive = true
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.title = title
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionItemClickListener.onFinished()
            this.mode = null
            isActive = false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.menu_item_select_all -> actionItemClickListener.onSelectAllPressed()
                R.id.menu_item_delete -> {
                    actionItemClickListener.onDeletePressed()
                    finishActionMode()
                }
                else -> throw  IllegalArgumentException("The item id ${item.itemId} is not valid menu item @${this.javaClass.name}")
            }
            return true
        }

        fun startActionMode(activity: AppCompatActivity, @MenuRes menuResId: Int, title: String? = null) {
            this.menuResId = menuResId
            this.title = title
            activity.startActionMode(this)
        }

        fun updateTitle(title: String) {
            this@SelectionActionModeCallback.title = title
            mode?.invalidate()
        }

        fun finishActionMode() {
            mode?.finish()
        }
    }