/*
 * Author : kiran Poman.
 * Module : Push notification module
 * Comments : This is service class which is executed by firebase SDK
 * Output : To get notification from firbase
 */
package com.fairfareindia.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fairfareindia.R
import com.fairfareindia.ui.Login.LoginActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
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
    private var broadcaster: LocalBroadcastManager? = null
    var isLogin: String? = null
    var mPreferencesManager: PreferencesManager? = null
    var CHANNEL_ID = "1"
    private var id = 0

    override fun onCreate() {
        PreferencesManager.initializeInstance(this)
        mPreferencesManager = PreferencesManager.instance
        broadcaster = LocalBroadcastManager.getInstance(this)
        isLogin = mPreferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_ISLOGIN)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("NEW_TOKEN", p0)
    }



    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("FCMqw", remoteMessage.data.toString())
        Log.e("FCMNoti", remoteMessage.toString())

        /* // Check if message contains a data payload.
         if (remoteMessage.data.size > 0) {
             try {
                 handleDataMessage(remoteMessage)
             } catch (e: Exception) {
                 Log.e("JSONError", e.toString())
             }
         }
         if (remoteMessage.notification != null) {
             try {
                 handleDataMessage(remoteMessage)
             } catch (e: Exception) {
                 Log.e("JSONError", e.toString())
             }
         }*/

        showChatNotification(remoteMessage)
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

    private fun showChatNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["body"]
        val screen = remoteMessage.data["screen"]
        var notificationIntent: Intent? = null
        var pendingIntent: PendingIntent? = null

        val intent = Intent(applicationContext, HomeActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        //  intent.putExtra("AnotherActivity", screen);


        val builder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
        if (isLogin == "true") {
            notificationIntent = Intent(
                applicationContext,
                HomeActivity::class.java)
            notificationIntent.action = "MyRides"

            if (notificationIntent != null) {
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    notificationIntent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            }
        } else {
            notificationIntent = Intent(applicationContext, LoginActivity::class.java)
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        }
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        sendNotification(builder)
    }


    private fun sendNotification(builder: NotificationCompat.Builder) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager != null) {
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "1",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(
                id++ /* ID of notification */,
                builder.build()
            )
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
        broadcaster?.sendBroadcast(intent)
    }
}