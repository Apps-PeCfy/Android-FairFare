package com.fairfareindia.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fairfareindia.R
import com.fairfareindia.ui.Login.LoginActivity
import com.fairfareindia.ui.drawer.intercityrides.ridedetails.IntercityRideDetailsActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.intercitytrackpickup.TrackPickUpActivity
import com.fairfareindia.ui.intercitytrackride.InterCityTrackRideActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson


class NewFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FirebaseMessageService"
    var CHANNEL_ID = "1"
    private var id = 0
    private var broadcaster: LocalBroadcastManager? = null

    var isLogin: String? = null
    var mPreferencesManager: PreferencesManager? = null

    val VIBRATE_PATTERN = longArrayOf(0, 500)


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("NEW_TOKEN", p0)
    }

    override fun onCreate() {
        PreferencesManager.initializeInstance(this)
        mPreferencesManager = PreferencesManager.instance
        isLogin = mPreferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_ISLOGIN)
        broadcaster = LocalBroadcastManager.getInstance(this)
    }


    override fun onMessageReceived(p0: RemoteMessage) {
        Log.e(TAG, "From: " + p0!!.from)

        // Check if message contains a data payload.
        if (p0.data.isNotEmpty()) {
            val message = p0.data["body"]
            val type = p0.data["type"]
            val screen = p0.data["screen"]
            val data : NotificationDataModel = Gson().fromJson(p0.data["data"], NotificationDataModel::class.java)

            if (isLogin == "true") {
                if (screen == Constants.NOTIF_SCREEN_RIDE){
                    val intent1 = Intent(
                        applicationContext,
                        HomeActivity::class.java
                    ).setAction("ride_status_changed")
                    broadcaster!!.sendBroadcast(intent1)
                }

                if (type == Constants.NOTIF_PAID_PAYMENT){
                    val intent1 = Intent(
                        applicationContext,
                        IntercityRideDetailsActivity::class.java
                    ).setAction("payment_status_change")
                    broadcaster!!.sendBroadcast(intent1)
                }

            }

            showChatNotification(p0)
        }

        if (p0.notification != null) {
            Log.d(
                TAG,
                "Message Notification Body: " + p0.notification!!.body
            )
        }

    }

    private fun showChatNotification(remoteMessage: RemoteMessage?) {
        val title =
            applicationContext.resources.getString(R.string.app_name)
        val message = remoteMessage?.data?.get("body")
        val type = remoteMessage?.data?.get("type")
        val screen = remoteMessage?.data?.get("screen")
        val data : NotificationDataModel = Gson().fromJson(remoteMessage?.data?.get("data"), NotificationDataModel::class.java)
        var notificationIntent: Intent? = null
        var pendingIntent: PendingIntent? = null


        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)

        if (isLogin == "true") {
            if (screen == Constants.NOTIF_SCREEN_RIDE) {
                if (data.ride_status == Constants.BOOKING_PENDING || data.ride_status == Constants.BOOKING_CANCELLED){
                    notificationIntent = Intent(applicationContext, HomeActivity::class.java)
                    notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                            or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    notificationIntent.action = "MyRides"
                }else if (data.ride_status == Constants.BOOKING_ACTIVE){
                    notificationIntent = Intent(applicationContext, InterCityTrackRideActivity::class.java)
                            .putExtra("ride_id", data.ride_id)
                }else if (data.ride_status == Constants.BOOKING_SCHEDULED || data.ride_status == Constants.BOOKING_ARRIVING || data.ride_status == Constants.BOOKING_ARRIVED){
                    notificationIntent =    Intent(applicationContext, TrackPickUpActivity::class.java)
                            .putExtra("ride_id", data.ride_id)
                }else if (data.ride_status == Constants.BOOKING_COMPLETED){
                    notificationIntent =   Intent(applicationContext, IntercityRideDetailsActivity::class.java)
                        .putExtra("ride_id", data.ride_id)
                        .putExtra("isFromEndRide", true)
                }

            }else{
                notificationIntent = Intent(applicationContext, HomeActivity::class.java)
                notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

        } else {
            notificationIntent = Intent(applicationContext, LoginActivity::class.java)
        }

        if (notificationIntent != null) {
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_ONE_SHOT
            )
        }
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        sendNotification(builder)
    }

    private fun sendNotification(builder: NotificationCompat.Builder) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "1",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(id++ /* ID of notification */, builder.build())
    }
}