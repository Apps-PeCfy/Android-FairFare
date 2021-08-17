package com.fairfareindia.ui.drawer.contactus.pojo

import com.google.gson.annotations.SerializedName

class ContactUsResponsePojo {
    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "ContactUsResponsePojo{" +
                "message = '" + message + '\'' +
                "}"
    }
}