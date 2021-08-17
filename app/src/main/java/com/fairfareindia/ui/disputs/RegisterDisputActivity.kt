package com.fairfareindia.ui.disputs

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.disputs.pojo.DisputesReasonResponsePOJO
import com.fairfareindia.ui.disputs.pojo.SaveDisputResponsePOJO
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.ridedetails.GridSpacingItemDecoration
import com.fairfareindia.ui.ridedetails.ImageModel
import com.fairfareindia.ui.ridedetails.SelectedImageAdapter
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PhotoSelector
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RegisterDisputActivity : AppCompatActivity() {


    var imageList: ArrayList<ImageModel>? = null
    var selectedImageList: ArrayList<String>? = null
    var sharedpreferences: SharedPreferences? = null
    var selectedImageAdapter: SelectedImageAdapter? = null

    val REQUEST_IMAGE_CAPTURE = 1
    val PICK_IMAGES = 2
    var image: File? = null
    var mCurrentPhotoPath: String? = null
    var projection =
        arrayOf(MediaStore.MediaColumns.DATA)

    var reasonId = ArrayList<Int>()

    /**
     * iLoma Team :- Mohasin 8 Jan
     */
    protected var photoSelector: PhotoSelector? = null
    var context: Context = this
    var filePath: Uri? = null


    @JvmField
    @BindView(R.id.toolbar_rideDetails)
    var mToolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.tv_vahicalNO)
    var tv_vahicalNO: TextView? = null

    @JvmField
    @BindView(R.id.editReview)
    var editReview: EditText? = null

    @JvmField
    @BindView(R.id.tv_driverName)
    var tv_driverName: TextView? = null

    @JvmField
    @BindView(R.id.tv_bagCount)
    var tv_bagCount: TextView? = null

    @JvmField
    @BindView(R.id.tv_Datetime)
    var tv_Datetime: TextView? = null

    @JvmField
    @BindView(R.id.btnSaveDisputes)
    var btnSaveDisputes: Button? = null


    @JvmField
    @BindView(R.id.tv_uploadPhoto)
    var tv_uploadPhoto: TextView? = null

    @JvmField
    @BindView(R.id.edt_meterReading)
    var edt_meterReading: EditText? = null

    @JvmField
    @BindView(R.id.edtEndMeterReading)
    var edtEndMeterReading: EditText? = null

    @JvmField
    @BindView(R.id.txtReview)
    var txtReview: TextView? = null


    @JvmField
    @BindView(R.id.edtTotalFareCharged)
    var edtTotalFareCharged: EditText? = null

    //  var reasonList = arrayOf<String?>()

    val reasonList: ArrayList<String> = ArrayList()
    val Listreason: ArrayList<Int> = ArrayList()


    @JvmField
    @BindView(R.id.selected_recycler_view)
    var selectedImageRecyclerView: RecyclerView? = null



    var mSpinner: MultipleSelectionSpinner? = null

    var token: String? = null
    var preferencesManager: PreferencesManager? = null



    @JvmField
    @BindView(R.id.tv_carName)
    var tv_carName: TextView? = null

    @JvmField
    @BindView(R.id.ivUserIcon)
    var ivUserIcon: ImageView? = null

    @JvmField
    @BindView(R.id.iv_vehical)
    var iv_vehical: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_disputs)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        PreferencesManager.initializeInstance(this@RegisterDisputActivity)
        preferencesManager = PreferencesManager.instance
        mSpinner = findViewById(R.id.spinnerReason)


        tv_Datetime!!.text = intent.getStringExtra("Datetime")
        tv_bagCount!!.text = intent.getStringExtra("bagCount")
        tv_driverName!!.text = intent.getStringExtra("driverName")

        if(intent.getStringExtra("driverName")!!.isEmpty()){
            ivUserIcon?.visibility= View.GONE

        }
        tv_vahicalNO!!.text = intent.getStringExtra("vahicalNo")
        tv_carName!!.text = intent.getStringExtra("vahicalName")
        if(intent.getStringExtra("MeterReading").equals("0")){
            edt_meterReading!!.setEnabled(true)
        }else{
            edt_meterReading!!.setText(intent.getStringExtra("MeterReading"))

        }



        Glide.with(this@RegisterDisputActivity)
            .load(intent.getStringExtra("vahicalImg"))
            .apply(
                RequestOptions()
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
            ).into(iv_vehical!!)

        if (isStoragePermissionGranted()) {
            init()
            setSelectedImageList()
        }


        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)


        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE)

        photoSelector = PhotoSelector(this)

        mToolbar!!.title = "Register Dispute"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }

        val progressDialog = ProgressDialog(this@RegisterDisputActivity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog

        ApiClient.client.getDisputeReasons("Bearer $token")!!
            .enqueue(object : Callback<DisputesReasonResponsePOJO?> {
                override fun onResponse(
                    call: Call<DisputesReasonResponsePOJO?>,
                    response: Response<DisputesReasonResponsePOJO?>
                ) {
                    progressDialog.dismiss()
                    if (response.code() == 200) {

                        for (i in response.body()!!.data!!.indices) {

                            reasonList.add((response.body()!!.data?.get(i)?.reason)!!)
                        }
                        mSpinner!!.setItems(reasonList)

                        val ha = Handler()
                        ha.postDelayed(object : Runnable {
                            override fun run() {

                                if (mSpinner!!.buildSelectedItemString().isNotEmpty()) {
                                    txtReview!!.text = (mSpinner!!.buildSelectedItemString())

                                }

                                ha.postDelayed(this, 1000)
                            }
                        }, 1000)


                    } else {
                        Toast.makeText(
                            this@RegisterDisputActivity,
                            "Internal server error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<DisputesReasonResponsePOJO?>,
                    t: Throwable
                ) {
                    progressDialog.dismiss()
                    Log.d("response", t.stackTrace.toString())
                }
            })


    }



    private fun setSelectedImageList() {


        selectedImageAdapter = SelectedImageAdapter(this, selectedImageList!!, object : SelectedImageAdapter.SelectedImageAdapterInterface{
            override fun itemClick(position: Int, imageName: String?) {

            }

            override fun onRemoveClick(position: Int, imageName: String?) {
                showConfirmationDialog(position)
            }

        } )
        val spanCount = 2
        selectedImageRecyclerView!!.layoutManager = GridLayoutManager(this, spanCount)
        val spacing = 15
        val includeEdge = true
        selectedImageRecyclerView!!.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                spacing,
                includeEdge
            )
        )
        selectedImageRecyclerView!!.adapter = selectedImageAdapter

    }

    private fun isStoragePermissionGranted(): Boolean {
        val ACCESS_EXTERNAL_STORAGE: Int = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (ACCESS_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100
            )
            return false
        }
        return true
    }

    private fun init() {
        selectedImageList = ArrayList<String>()
        imageList = ArrayList()
    }

    private fun showConfirmationDialog(position: Int) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("FairFareIndia")
        alertDialog.setMessage("Are you sure you remove this image?")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Yes") { dialog, which ->
            imageList!!.removeAt(position)
            selectedImageList!!.removeAt(position)
            selectedImageAdapter!!.notifyDataSetChanged()
        }
        alertDialog.setNegativeButton("No") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }


    override fun onDestroy() {
        // sharedpreferences!!.edit().clear().commit()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home_lang, menu)
        return true
    }

    @OnClick(R.id.btnSaveDisputes)
    fun btnSaveDisputes() {


        if(ProjectUtilities.checkInternetAvailable(this@RegisterDisputActivity)) {
            reasonId = ArrayList<Int>()
            for (i in mSpinner!!.myNumbers()!!.indices) {
                reasonId.add((mSpinner!!.myNumbers())!!.get(i) + 1)
                Log.d("qasdes", ((mSpinner!!.myNumbers())!!.get(i) + 1).toString())

            }


            /**
             * iLoma Team :- Mohasin 8 Jan
             */

            if (imageList != null && imageList!!.size > 0) {
                saveDisputeMultipart()
            } else {
                saveDisputeAPI()
            }


        }else{
            ProjectUtilities.showToast(this@RegisterDisputActivity,getString(R.string.internet_error))
        }



    }

    private fun saveDisputeMultipart() {
        val imagesMultipart = arrayOfNulls<MultipartBody.Part>(
            imageList!!.size
        )

        for (pos in imageList!!.indices) {
            val file = File(imageList!![pos].image!!)
            val surveyBody: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            imagesMultipart[pos] = MultipartBody.Part.createFormData("dispute_images[]", imageList!![pos].image!!, surveyBody)
        }


        val map = HashMap<String?, String?>()

        map["ride_id"] = intent.getStringExtra("RIDEID")
        map["type"] = "Dispute"
        map["start_meter_reading"] = edt_meterReading!!.text.toString()
        map["end_meter_reading"] = edtEndMeterReading!!.text.toString()
        map["actual_meter_charges"] = edtTotalFareCharged!!.text.toString()
        map["comment"] = editReview!!.text.toString()



        val map1 = HashMap<String?, ArrayList<Int>?>()
        map1["dispute_reason_id[]"] = reasonId


        val progressDialog = ProgressDialog(this@RegisterDisputActivity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show()

        val call = ApiClient.client.multipartSaveDispute(
            "Bearer $token",
            imagesMultipart,
            map, reasonId
        )
        call!!.enqueue(object : Callback<SaveDisputResponsePOJO?> {
            override fun onResponse(
                call: Call<SaveDisputResponsePOJO?>,
                response: Response<SaveDisputResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    val intent = Intent(this@RegisterDisputActivity, HomeActivity::class.java)
                    intent.action = "RegisterDisput"
                    startActivity(intent)

                }else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(
                                this@RegisterDisputActivity,
                                pojo.errors!!.get(0).message,
                                Toast.LENGTH_LONG
                            )
                            .show()


                    } catch (exception: IOException) {
                    }

                } else {
                    Toast.makeText(
                        this@RegisterDisputActivity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<SaveDisputResponsePOJO?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
            }
        })


    }


    private fun saveDisputeAPI() {
        var edtcomment = editReview!!.text.toString()


        val progressDialog = ProgressDialog(this@RegisterDisputActivity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog

        ApiClient.client.saveDispute(
            "Bearer $token",
            intent.getStringExtra("RIDEID"),
            "Dispute", reasonId,
            edt_meterReading!!.text.toString(),
            edtEndMeterReading!!.text.toString(),
            edtTotalFareCharged!!.text.toString(), edtcomment
        )!!.enqueue(object :
            Callback<SaveDisputResponsePOJO?> {
            override fun onResponse(
                call: Call<SaveDisputResponsePOJO?>,
                response: Response<SaveDisputResponsePOJO?>
            ) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    val intent = Intent(this@RegisterDisputActivity, HomeActivity::class.java)
                    intent.action = "RegisterDisput"
                    startActivity(intent)

                }else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(
                                this@RegisterDisputActivity,
                                pojo.errors!!.get(0).message,
                                Toast.LENGTH_LONG
                            )
                            .show()


                    } catch (exception: IOException) {
                    }

                } else {
                    Toast.makeText(
                        this@RegisterDisputActivity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<SaveDisputResponsePOJO?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })

    }

    @OnClick(R.id.tv_uploadPhoto)
    fun btnClick() {
        setImageList()
    }

    private fun setImageList() {

        /*val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder =
            android.app.AlertDialog.Builder(this@RegisterDisputActivity)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Take Photo") {
                takePicture()
            } else if (options[item] == "Choose from Gallery") {
                getPickImageIntent()
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()*/

        /**
         * iLoma Team :- Mohasin 8 Jan
         */

        if(photoSelector!!.isPermissionGranted(context)){
            photoSelector!!.selectImage(null)
        }


    }

    fun getPickImageIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGES)
    }


    private fun takePicture() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Continue only if the File was successfully created;
        // Continue only if the File was successfully created;
        val photoFile: File? = createImageFile()
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
            startActivityForResult(
                cameraIntent, REQUEST_IMAGE_CAPTURE
            )
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }


    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (resultCode == Activity.RESULT_OK) {
             if (requestCode == REQUEST_IMAGE_CAPTURE) {
                 if (mCurrentPhotoPath != null) {
                     addImage(mCurrentPhotoPath)
                 }
             } else if (requestCode == PICK_IMAGES) {
                 if (data!!.clipData != null) {
                     val mClipData = data.clipData
                     for (i in 0 until mClipData!!.itemCount) {
                         val item = mClipData.getItemAt(i)
                         val uri = item.uri
                         getImageFilePath(uri)
                     }
                 } else if (data.data != null) {
                     val uri = data.data
                     getImageFilePath(uri)
                 }
             }
         }
     }*/

    /**
     * LIFECYCLE
     */
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        //Profile Picture
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PhotoSelector.SELECT_FILE) {
                filePath = photoSelector!!.onSelectFromGalleryResult(data)
                val imageModel = ImageModel()
                imageModel.filePath = photoSelector!!.getPath(filePath, context)
                imageModel.image = photoSelector!!.getPath(filePath, context)
                imageModel.isSelected
                imageList!!.add(0, imageModel)
                selectedImageList!!.add(0, imageModel.image !!)
                selectedImageAdapter!!.notifyDataSetChanged()
            } else if (requestCode == PhotoSelector.REQUEST_CAMERA) {
                filePath = photoSelector!!.onCaptureImageResult()
                val imageModel = ImageModel()
                imageModel.filePath = photoSelector!!.getPath(filePath, context)
                imageModel.image = photoSelector!!.getPath(filePath, context)
                imageModel.isSelected
                imageList!!.add(0, imageModel)
                selectedImageList!!.add(0, imageModel.image !!)
                selectedImageAdapter!!.notifyDataSetChanged()
            }
        }
    }

    fun getImageFilePath(uri: Uri?) {
        val cursor =
            contentResolver.query(uri!!, projection, null, null, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val absolutePathOfImage =
                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                absolutePathOfImage?.let { checkImage(it) } ?: checkImage(uri.toString())
            }
        }
    }

    fun checkImage(filePath: String?) {
        // Check before adding a new image to ArrayList to avoid duplicate images
        if (!selectedImageList!!.contains(filePath!!)) {
            for (pos in imageList!!.indices) {
                if (imageList!!.get(pos).image != null) {
                    if (imageList!!.get(pos).image.equals(filePath)) {
                        imageList!!.removeAt(pos)
                    }
                }
            }
            addImage(filePath)
        }
    }


    fun addImage(filePath: String?) {
        val imageModel = ImageModel()
        imageModel.image = filePath
        imageModel.isSelected
        imageList!!.add(0, imageModel)
        selectedImageList!!.add(0, filePath!!)
        selectedImageAdapter!!.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                sharedpreferences!!.edit().clear().commit()
                val intent = Intent(this@RegisterDisputActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                startActivity(intent)
            }
        }
        return true
    }


    private fun setStatusBarGradiant(activity: RegisterDisputActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            init()
            // getAllImages();
            setImageList()
            setSelectedImageList()
        }
    }


}