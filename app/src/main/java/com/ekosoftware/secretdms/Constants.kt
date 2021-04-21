package com.ekosoftware.secretdms

object Constants {
    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = Secrets.server_key
    const val CONTENT_TYPE = "application/json"

    const val MESSAGE_PARAM_SENDER = "sender"
    const val MESSAGE_PARAM_TO = "addressee"
    const val MESSAGE_PARAM_BODY = "body"
    const val MESSAGE_PARAM_TIMER_IN_MILLIS = "timerInMillis"
    const val MESSAGE_PARAM_TIMESTAMP = "timestamp"
}