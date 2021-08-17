package com.fairfareindia.ui.drawer.faq.pojo

import com.google.gson.annotations.SerializedName

class FAQResponsePOJO {
    @SerializedName("faqs")
    var faqs: List<FaqsItem>? = null

    @SerializedName("message")
    var message: String? = null

    override fun toString(): String {
        return "FAQResponsePOJO{" +
                "faqs = '" + faqs + '\'' +
                ",message = '" + message + '\'' +
                "}"
    }

    inner class FaqsItem {
        @SerializedName("values")
        var values: List<ValuesItem>? = null

        @SerializedName("key")
        var key: String? = null

        override fun toString(): String {
            return "FaqsItem{" +
                    "values = '" + values + '\'' +
                    ",key = '" + key + '\'' +
                    "}"
        }
    }

    inner class ValuesItem {
        @SerializedName("question")
        var question: String? = null

        @SerializedName("answer")
        var answer: String? = null

        @SerializedName("id")
        var id = 0

        @SerializedName("category")
        var category: String? = null

        override fun toString(): String {
            return "ValuesItem{" +
                    "question = '" + question + '\'' +
                    ",answer = '" + answer + '\'' +
                    ",id = '" + id + '\'' +
                    ",category = '" + category + '\'' +
                    "}"
        }
    }
}