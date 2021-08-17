@file:Suppress("DEPRECATION")

package com.fairfareindia.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.fairfareindia.R
import com.fairfareindia.ui.trackRide.NearByPlacesPOJO.Location
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.text.DecimalFormat
import java.text.NumberFormat

object ProjectUtilities {
    var location_fetch_time_interval = 1 * 60000
    var latitude = 0.0
    var longitude = 0.0
    var isPopUp = true
    private var pDialog: ProgressDialog? = null
    var notificationcnt = 0
    var activity = 0.0

    // Define variable
    private var isInternetPresent = false
    private var cd: ConnectionDetector? = null
    private val suffix = arrayOf("", "k", "m", "b", "t")
    private const val MAX_LENGTH = 4

    // This method is for checking internet connection
    fun checkInternetAvailable(mContext: Context?): Boolean {
        cd = ConnectionDetector(mContext!!)
        isInternetPresent = cd!!.isConnectingToInternet
        return isInternetPresent
    }

    //This method hide keyboard
    fun hideKeyboard(mActivity: Activity) {
        try {
            val inputMethodManager = mActivity
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                mActivity
                    .currentFocus!!.windowToken, 0
            )
        } catch (e: Exception) {
        }
    }

    //This method show progress dialog
    fun showProgressDialog(mContext: Context?) {
        pDialog = ProgressDialog(mContext)
        pDialog!!.setMessage("Please wait...")
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    fun showProgressBillingDialog(mContext: Context?) {
        pDialog = ProgressDialog(mContext)
        pDialog!!.setMessage("It may take few minutes,your transaction is being processed...")
        pDialog!!.setCancelable(false)
        pDialog!!.show()
    }

    //This method dismiss progress dialog
    fun dismissProgressDialog() {
        if (pDialog != null && pDialog!!.isShowing) pDialog!!.dismiss()
    }

    //this method show internet diaolg
    fun internetDialog(mContext: Context) {
        val alertDialog = AlertDialog.Builder(mContext)
        alertDialog.setTitle(
            mContext.resources
                .getString(R.string.warning)
        )
        alertDialog.setMessage(
            mContext.resources.getString(
                R.string.internet_error
            )
        )
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(
            mContext.resources.getString(R.string.btn_ok)
        ) { dialog, which -> // TODO Auto-generated method stub
            dialog.cancel()
        }
        alertDialog.show()
    }

    fun showAlertDialog(mContext: Context?, message: String?) {
        if (mContext != null) {
            val alertDialog =
                AlertDialog.Builder(mContext)
            alertDialog.setCancelable(false)
            alertDialog.setTitle(mContext.getString(R.string.app_name))
            alertDialog.setMessage(message)
            alertDialog.setPositiveButton(
                mContext.resources.getString(R.string.btn_ok)
            ) { dialog, which -> // TODO Auto-generated method stub
                dialog.cancel()
            }
            alertDialog.show()
        }
    }

    fun showAlertDialog(
        mContext: Context?,
        title: String?,
        message: String?
    ) {
        if (mContext != null) {
            val alertDialog =
                AlertDialog.Builder(mContext)
            alertDialog.setCancelable(false)
            alertDialog.setTitle(title)
            alertDialog.setMessage(message)
            alertDialog.setPositiveButton(
                mContext.resources.getString(R.string.btn_ok)
            ) { dialog, which -> // TODO Auto-generated method stub
                dialog.cancel()
            }
            alertDialog.show()
        }
    }

    fun showToast(mContext: Context?, message: String) {
        Toast.makeText(mContext, "" + message, Toast.LENGTH_SHORT).show()
    }

    fun checkPermission(mContext: Context?): Boolean {
        var PERMISSIONS: Array<String>?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }else{
            PERMISSIONS = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        var size = 0
        for (i in PERMISSIONS.indices) {
            if (PermissionChecker.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(mContext!!, PERMISSIONS[i]))
            {
                size++
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && "android.permission.ACCESS_BACKGROUND_LOCATION".equals(PERMISSIONS[i], ignoreCase = true)) {
                size++
            }
        }
        return size == PERMISSIONS.size
    }

    /*
     * This method is used for runtime permission and make a call
     */
    @SuppressLint("MissingPermission")
    fun makeCall(mContext: Context, phoneNumber: String) {
        mContext.startActivity(
            Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:$phoneNumber")
            )
        )
    }

    /*
     * This method is used for web browser
     */
    fun openUrlToWebView(mContext: Context, url: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        mContext.startActivity(browserIntent)
    }

    fun formatNumber(number: Double): String {
        /* String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }
        return r;*/
        var number = number
        val power: Int
        val suffix = " kmbt"
        var formattedNumber = ""
        val formatter: NumberFormat = DecimalFormat("#,###.#")
        power = StrictMath.log10(number).toInt()
        number = number / Math.pow(10.0, power / 3 * 3.toDouble())
        formattedNumber = formatter.format(number)
        formattedNumber = formattedNumber + suffix[power / 3]
        return if (formattedNumber.length > 4) formattedNumber.replace(
            "\\.[0-9]+".toRegex(),
            ""
        ) else formattedNumber
    }

    @Throws(IOException::class)
    fun downloadCSVFile(list: ArrayList<Location>?, context: Context):File {
        var myFile: File
        var root: String? = fetchStoragePath(context)
        myFile = File("$root/FairFare")
        if (!myFile.exists()) {
            myFile.mkdirs()
            myFile = File("$root/FairFare/CSVFiles")
            if (!myFile.exists()) {
                myFile.mkdirs()
            }
        } else {
            myFile = File("$root/FairFare/CSVFiles")
            if (!myFile.exists()) {
                myFile.mkdirs()
            }
        }
        val fname = "google_path_${System.currentTimeMillis()}.csv"
        val file = File(myFile, fname)
        file.createNewFile()
        val fOut = FileOutputStream(file)
        val myOutWriter = OutputStreamWriter(fOut)
        myOutWriter.append("latitude,longitude,timestamp")
        myOutWriter.append("\n")
        if (list != null && list.size > 0) {
            for (model in list) {
                myOutWriter.append(
                    model.lat
                        .toString() + "," + model.lng.toString() + "," + model.timestamp.toString())
                myOutWriter.append("\n")
            }
            myOutWriter.close()
            fOut.close()
        }

        return file;
    }

    fun fetchStoragePath(context: Context): String? {
        var path: String? = null
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            path = Environment.getExternalStorageDirectory()
                .toString() + File.separator
        } else {
            path = context.getExternalFilesDir(null).toString() + File.separator
        }
        return path
    }
}