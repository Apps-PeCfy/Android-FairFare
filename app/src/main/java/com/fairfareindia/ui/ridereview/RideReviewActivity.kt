package com.fairfareindia.ui.ridereview

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.contactus.pojo.ContactUsResponsePojo
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.gson.GsonBuilder
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RideReviewActivity : AppCompatActivity() {

    var sharedpreferences: SharedPreferences? = null


    @JvmField
    @BindView(R.id.toolbar_rideReview)
    var mToolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.btnSubmitReview)
    var btnSubmitReview: Button? = null

    @JvmField
    @BindView(R.id.ratingBar)
    var ratingBar: SimpleRatingBar? = null

@JvmField
    @BindView(R.id.editReview)
    var editReview: EditText? = null


    var token: String? = null
    var rideID: String? = null
    var ridefromDrawer: String? = ""
    var preferencesManager: PreferencesManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_review)
        ButterKnife.bind(this)
        setStatusBarGradiant(this)
        sharedpreferences = this@RideReviewActivity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)



        PreferencesManager.initializeInstance(this@RideReviewActivity)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        if(intent.getStringExtra("RIDEID")!=null){
            rideID = intent.getStringExtra("RIDEID")
        }else{
            rideID = intent.getStringExtra("MyRides_id")
            ridefromDrawer = intent.getStringExtra("MyRides")
        }




        mToolbar!!.title = "Ride Review"
        mToolbar!!.setTitleTextColor(Color.WHITE)
        if(ridefromDrawer.equals("DrawerMyRides")){
            mToolbar!!.setNavigationIcon(R.drawable.back_arrow)
        }
        setSupportActionBar(mToolbar)
        mToolbar!!.setNavigationOnClickListener { onBackPressed() }

    }

    override fun onBackPressed() {


        if(ridefromDrawer.equals("DrawerMyRides")){

            super.onBackPressed()


        }else{

        }
    }



    private fun setStatusBarGradiant(activity: RideReviewActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val background = activity.resources.getDrawable(R.drawable.app_gradient)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }

    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        if(ridefromDrawer.equals("DrawerMyRides")){



        }else{
            menuInflater.inflate(R.menu.menu_skip, menu)

            val positionOfMenuItem = 0 // or whatever...

            val item = menu.getItem(positionOfMenuItem)
            val s = SpannableString("Skip")
            s.setSpan(ForegroundColorSpan(Color.WHITE), 0, s.length, 0)
            item.setTitle(s)

        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_skip -> {
                preferencesManager!!.setStringValue(
                    Constants.SHARED_PREFERENCE_PICKUP_AITPORT,
                    "LOCALITY"
                )
                sharedpreferences!!.edit().clear().commit()
                val intent = Intent(this@RideReviewActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                startActivity(intent)
            }
        }
        return true
    }

    @OnClick(R.id.ratingBar)
    fun rating(){

        if(ratingBar!!.rating<=1) {
            ratingBar!!.rating=1.0f

        }

      }

    @OnClick(R.id.btnSubmitReview)
    fun reView(){

        if(ProjectUtilities.checkInternetAvailable(this@RideReviewActivity)) {

            val progressDialog = ProgressDialog(this@RideReviewActivity)
            progressDialog.setCancelable(false) // set cancelable to false
            progressDialog.setMessage("Please Wait") // set message
            progressDialog.show() // show progress dialog

            ApiClient.client.setRideReview(
                "Bearer $token", rideID,
                ratingBar!!.getRating().toString(), editReview!!.text.toString()
            )!!.enqueue(object :
                Callback<ContactUsResponsePojo?> {
                override fun onResponse(
                    call: Call<ContactUsResponsePojo?>,
                    response: Response<ContactUsResponsePojo?>
                ) {
                    progressDialog.dismiss()
                    if (response.code() == 200) {
                        val intent = Intent(this@RideReviewActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else if (response.code() == 422) {
                        val gson = GsonBuilder().create()
                        var pojo: ValidationResponse? = ValidationResponse()
                        try {
                            pojo = gson.fromJson(
                                response.errorBody()!!.string(),
                                ValidationResponse::class.java
                            )
                            Toast.makeText(this@RideReviewActivity, pojo.message, Toast.LENGTH_LONG)
                                .show()


                        } catch (exception: IOException) {
                        }

                    } else {
                        Toast.makeText(
                            this@RideReviewActivity,
                            "Internal server error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ContactUsResponsePojo?>,
                    t: Throwable
                ) {
                    progressDialog.dismiss()
                    Log.d("response", t.stackTrace.toString())
                }
            })

        }else{
            ProjectUtilities.showToast(this@RideReviewActivity,getString(R.string.internet_error))
        }


    }



}
