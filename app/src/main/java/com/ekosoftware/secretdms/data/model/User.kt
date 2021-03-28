package com.ekosoftware.secretdms.data.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    var uid: String? = null,
    var username: String? = null,
    var email: String? = null,
    @Exclude var isAuthenticated: Boolean = false,
    @Exclude var isNew: Boolean = false,
    @Exclude var isCreated: Boolean = false,
) : Parcelable
