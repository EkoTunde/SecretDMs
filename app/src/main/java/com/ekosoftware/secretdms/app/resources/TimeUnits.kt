package com.ekosoftware.secretdms.app.resources

import com.ekosoftware.secretdms.R

object TimeUnits {

    val SECONDS = Strings.get(R.string.seconds_abbreviated)
    val MINUTES = Strings.get(R.string.minutes_abbreviated)
    val HOURS = Strings.get(R.string.hours_abbreviated)
    val DAYS = Strings.get(R.string.days_abbreviated)

    /**
     * Returns the base time in milliseconds.
     */
    fun getValue(timeUnit: String): Long {
        timeUnits.forEach { if (timeUnit == it.name) return it.inMillis }
        throw IllegalStateException("The specified argument timeUnit isn't a valid TimeUnit.")
    }

    data class TimeUnit(val name: String, val inMillis: Long, val max: Int)

    private val timeUnits: Array<TimeUnit> by lazy {
        arrayOf(
            TimeUnit(Strings.get(R.string.seconds_abbreviated), 1000L, 59), // Seconds
            TimeUnit(Strings.get(R.string.minutes_abbreviated), 1000L * 60L, 59), // Minutes
            TimeUnit(Strings.get(R.string.hours_abbreviated), 1000L * 60L * 60L, 23), // Hours
            TimeUnit(Strings.get(R.string.days_abbreviated), 1000L * 60L * 60L * 24L, 60) // Days
        )
    }

    fun Long?.asTimeAndUnit(): Pair<String, Long>? {
        if (this != null && this > 0L) {
            var time = this
            for (unit in timeUnits) {
                time /= unit.inMillis
                if (time < unit.max + 1) return Pair(unit.name, time)
            }
        }
        return null
    }
}
