package com.ekosoftware.secretdms.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val DIRECTION_SENT = 1
const val DIRECTION_RECEIVED = 2

@Entity(tableName = "messages")
data class Message(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "idMessage")
    var id: String = "",

    @ColumnInfo(name = "body")
    var body: String,

    @ColumnInfo(name = "direction")
    var direction: Int = DIRECTION_RECEIVED,

    @ColumnInfo(name = "friendId")
    var friendId: String,

    @ColumnInfo(name = "timer")
    var timerInMillis: Long? = 0L,

    @ColumnInfo(name = "createdInMillis")
    var createdInMillis: Long? = null,

    @ColumnInfo(name = "sentInMillis")
    var sentInMillis: Long? = null,

    @ColumnInfo(name = "receivedInMillis")
    var receivedInMillis: Long? = null,

    @ColumnInfo(name = "readInMillis")
    var readInMillis: Long? = null,

    @ColumnInfo(name = "showedTime")
    var showedTimeInMillis: Long? = null,
)
