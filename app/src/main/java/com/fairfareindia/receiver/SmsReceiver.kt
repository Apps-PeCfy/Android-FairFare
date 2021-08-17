package com.fairfareindia.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SmsReceiver : BroadcastReceiver() {
    private var otpReceiver: OTPListener? = null
    fun injectOTPListener(receiver: OTPListener?) {
        otpReceiver = receiver
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status =
                extras!![SmsRetriever.EXTRA_STATUS] as Status?
            when (status!!.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    val message =
                        extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                    val pattern = Pattern.compile("\\d{6}")
                    val matcher = pattern.matcher(message)
                    println("SMS verification code::SMSBroadcastReceiver:1:  $message")
                    if (matcher.find()) {
                        if (otpReceiver != null) {
                            otpReceiver!!.onOTPReceived(matcher.group(0))
                        }
                    }
                }
                CommonStatusCodes.TIMEOUT -> if (otpReceiver != null) {
                    otpReceiver!!.onOTPTimeOut()
                }
            }
        }
    }

    interface OTPListener {
        fun onOTPReceived(otp: String?)
        fun onOTPTimeOut()
    }
}