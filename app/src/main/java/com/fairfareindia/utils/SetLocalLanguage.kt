package com.fairfareindia.utils

import android.content.Context
import java.util.*

object SetLocalLanguage {
    @Synchronized
    fun setLocaleLanguage(
        context: Context,
        strLanguage: String
    ) {
        val locale = Locale(strLanguage.toLowerCase())
        val conf = context.resources.configuration
        conf.locale = locale
        Locale.setDefault(locale)
        conf.setLayoutDirection(conf.locale)
        context.resources.updateConfiguration(conf, context.resources.displayMetrics)
    }
}