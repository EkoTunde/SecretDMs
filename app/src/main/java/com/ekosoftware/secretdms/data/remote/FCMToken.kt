package com.ekosoftware.secretdms.data.remote

import android.content.SharedPreferences

object FCMToken {
    var sharedPref: SharedPreferences? = null
    var token: String?
        get() = sharedPref?.getString("token", null)
        set(value) {
            sharedPref?.edit()?.putString("token", value)?.apply()
        }

    fun clearData() {
        token = null
    }
}