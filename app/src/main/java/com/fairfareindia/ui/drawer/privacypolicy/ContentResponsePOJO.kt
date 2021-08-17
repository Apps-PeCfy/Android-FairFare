package com.fairfareindia.ui.drawer.privacypolicy

import com.google.gson.annotations.SerializedName

class ContentResponsePOJO {
    @SerializedName("page_content")
    var pageContent: PageContent? =
        null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "ContentResponsePOJO{" +
                "page_content = '" + pageContent + '\'' +
                ",message = '" + message + '\'' +
                "}"
    }

    inner class PageContent {
        @SerializedName("page_name")
        var pageName: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("content")
        var content: String? = null

        override fun toString(): String {
            return "PageContent{" +
                    "page_name = '" + pageName + '\'' +
                    ",id = '" + id + '\'' +
                    ",content = '" + content + '\'' +
                    "}"
        }
    }
}