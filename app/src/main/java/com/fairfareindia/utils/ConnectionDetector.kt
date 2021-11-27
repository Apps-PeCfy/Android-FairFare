package com.fairfareindia.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

class ConnectionDetector(private val _context: Context) {
    val isConnectingToInternet: Boolean
        get() {
           /* val connectivity =
                _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null) for (i in info.indices) if (info[i]
                        .state == NetworkInfo.State.CONNECTED
                ) {
                    return true
                }
            }
            return false*/

            val cm = _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                if (Build.VERSION.SDK_INT < 23) {
                    val ni = cm.activeNetworkInfo
                    if (ni != null) {
                        return ni.isConnected && (ni.type == ConnectivityManager.TYPE_WIFI || ni.type == ConnectivityManager.TYPE_MOBILE)
                    }
                } else {
                    val n = cm.activeNetwork
                    if (n != null) {
                        val nc = cm.getNetworkCapabilities(n)
                        return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(
                            NetworkCapabilities.TRANSPORT_WIFI)
                    }
                }
            }
            return false
        }

}