package com.ekosoftware.secretdms.app.resources

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.ekosoftware.secretdms.app.App

object Drawables {
    fun get(@DrawableRes drawableResId: Int): Drawable? {
        return ResourcesCompat.getDrawable(
            App.instance.resources,
            drawableResId,
            App.instance.theme
        )
    }
}