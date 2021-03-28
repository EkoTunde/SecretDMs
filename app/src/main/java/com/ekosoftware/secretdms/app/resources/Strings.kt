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

    object TimeUnits {
        val SECONDS = get(R.string.seconds_abbreviated)
        val MINUTES = get(R.string.minutes_abbreviated)
        val HOURS = get(R.string.hours_abbreviated)
        val DAYS = get(R.string.days_abbreviated)

        /**
         * Returns the base time in milliseconds.
         */
        fun getValue(timeUnit: String): Long = when (timeUnit) {
            SECONDS -> 1000L
            MINUTES -> 1000L * 60L
            HOURS -> 1000L * 60L * 60L
            DAYS -> 1000L * 60L * 60L * 24L
            else -> throw IllegalStateException("The specified argument timeUnit isn't a valid TimeUnit.")
        }

        fun asArray(): Array<Triple<String, Long, Int>> = arrayOf(
            Triple(SECONDS, getValue(SECONDS), getMax(SECONDS)),
            Triple(MINUTES, getValue(MINUTES), getMax(MINUTES)),
            Triple(HOURS, getValue(HOURS), getMax(HOURS)),
            Triple(DAYS, getValue(DAYS), getMax(DAYS))
        )

        private fun getMax(timeUnit: String) = when (timeUnit) {
            SECONDS, MINUTES -> 59
            HOURS -> 23
            DAYS -> 60
            else -> throw IllegalStateException("The specified argument timeUnit isn't a valid TimeUnit.")
        }
    }
}