package com.example.fairfare.ui.ridedetails

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.fairfare.R
import com.example.fairfare.ui.home.HomeActivity
import java.io.*


class RideDetailsActivity : AppCompatActivity() {

    var sharedpreferences: SharedPreferences? = null

    @JvmField
    @BindView(R.id.toolbar_rideDetails)
    var mToolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.tv_uploadPhoto)
    var tv_uploadPhoto: TextView? = null

   @JvmField
    @BindView(R.id.iv1)
    var viewImage: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_details)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)

        mToolbar!!.title = "View Ride"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }

    }

    override fun onDestroy() {
       // sharedpreferences!!.edit().clear().commit()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home_lang, menu)
        return true
    }

    @OnClick(R.id.tv_uploadPhoto)
    fun btnClick(){
        selectImage()
    }

    private fun selectImage() {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder =
            AlertDialog.Builder(this@RideDetailsActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val f = File(Environment.getExternalStorageDirectory(), "temp.jpg")
                val photoURI = FileProvider.getUriForFile(
                    this@RideDetailsActivity,
                    applicationContext.packageName + ".provider", f)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, 1)
            } else if (options[item] == "Choose from Gallery") {
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(intent, 2)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                var f =
                    File(Environment.getExternalStorageDirectory().toString())
                for (temp in f.listFiles()) {
                    if (temp.name == ".jpg") {
                        f = temp
                        break
                    }
                }
                try {
                    val bitmap: Bitmap
                    val bitmapOptions = BitmapFactory.Options()
                    bitmap = BitmapFactory.decodeFile(
                        f.absolutePath,
                        bitmapOptions
                    )
                    viewImage!!.setImageBitmap(bitmap)
                    val path = (Environment
                        .getExternalStorageDirectory()
                        .toString() + File.separator
                            + "Phoenix" + File.separator + "default")
                    f.delete()
                    var outFile: OutputStream? = null
                    val file =
                        File(path, System.currentTimeMillis().toString() + ".jpg")
                    try {
                        outFile = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile)
                        outFile.flush()
                        outFile.close()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (requestCode == 2) {
                val selectedImage = data!!.data
                val filePath =
                    arrayOf(MediaStore.Images.Media.DATA)
                val c =
                    contentResolver.query(selectedImage!!, filePath, null, null, null)
                c!!.moveToFirst()
                val columnIndex = c.getColumnIndex(filePath[0])
                val picturePath = c.getString(columnIndex)
                c.close()
                val thumbnail = BitmapFactory.decodeFile(picturePath)

                viewImage!!.setImageBitmap(thumbnail)
            }
        }
    }


        override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                sharedpreferences!!.edit().clear().commit()
                val intent = Intent(this@RideDetailsActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }


    private fun setStatusBarGradiant(activity: RideDetailsActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }
}