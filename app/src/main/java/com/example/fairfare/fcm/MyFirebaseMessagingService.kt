/*
 * Author : kiran Poman.
 * Module : Push notification module
 * Comments : This is service class which is executed by firebase SDK
 * Output : To get notification from firbase
 */
package com.example.fairfare.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.fairfare.R
import com.example.fairfare.ui.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var bitmap: Bitmap? = null
    private var title: String? = null
    private var message: String? = null
    private var notifyType: String? = null
    private var request = ""



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("FCMqw", remoteMessage.data.toString())
        Log.e("FCMNoti", remoteMessage.toString())



        try {
                handleDataMessage(remoteMessage)
            } catch (e: Exception) {
                Log.e("JSONError", e.toString())
            }

    }

    private fun handleDataMessage(json: RemoteMessage) {

         try {


             title = json.notification!!.title
            message = json.notification!!.body
             showNotification()

        } catch (e: JSONException) {
            Log.e(getString(R.string.app_name), "Json Exception: " + e.message)
        } catch (e: Exception) {
            Log.e(getString(R.string.app_name), "Exception: " + e.message)
        }
    }


    fun showNotification() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon)
        val sdf = SimpleDateFormat("HH:mm:ss")
        var formattedDate: String? = ""
        formattedDate = sdf.format(Date())

        val contentView = RemoteViews(getPackageName(), R.layout.custon_push_notofication)
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher)
        contentView.setTextViewText(R.id.title, title)
        contentView.setTextViewText(R.id.text, message)
        contentView.setTextViewText(R.id.tvDateandtime, formattedDate)
        val mBuilder =
            NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(contentView)
                .setGroupSummary(true)
                .setAutoCancel(true)
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = getString(R.string.app_name)
            mNotificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(getApplicationContext(), HomeActivity::class.java)
        intent.action = "MyRides"
        val bundle = Bundle()
        bundle.putString("title", title!!.trim { it <= ' ' })
        bundle.putString("message", message!!.trim { it <= ' ' })
        bundle.putString("notifyAction", "MyRides")
        intent.putExtras(bundle)

        // set intent so it does not start a new activity
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        val randomPIN = (Math.random() * 9000).toInt() + 1000
        sendLocalBroadcast(randomPIN.toString())
        mNotificationManager.notify(randomPIN, mBuilder.build())
    }

    fun sendLocalBroadcast(notificationId: String?) {
        val intent = Intent("notification")
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putString("message", message)
        bundle.putString("notifyType", notifyType)
        bundle.putString("request", request)
        bundle.putString("notificationId", notificationId)
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}