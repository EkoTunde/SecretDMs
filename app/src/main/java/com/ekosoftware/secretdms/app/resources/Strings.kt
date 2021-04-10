package com.ekosoftware.secretdms.app.resources

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.ekosoftware.secretdms.R
import com.ekosoftware.secretdms.app.App

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return App.instance.getString(stringRes, *formatArgs)
    }

    fun getQuantity(
        @PluralsRes pluralsRes: Int,
        quantity: Int,
        vararg formatArgs: Any = emptyArray()
    ): String {
        return App.instance.resources.getQuantityString(pluralsRes, quantity, *formatArgs)
    }
}