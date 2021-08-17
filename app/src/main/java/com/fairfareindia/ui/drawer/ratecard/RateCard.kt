package com.fairfareindia.ui.drawer.ratecard

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.networking.ApiClient
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.ratecard.pojo.RateCardResponsePOJO
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.android.material.tabs.TabLayout
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.collections.ArrayList

class RateCard : Fragment(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    var activityIsRunning = false
    var selectedPosition = 0
    var itemSelectedPosition = 0


    var check = 0


    var cityPojoList: List<GetAllowCityResponse.CitiesItem> = ArrayList()
    var getRateCardList: List<RateCardResponsePOJO.RateCardsItem> = ArrayList()

    var TabCalled: String? = "1"


    @JvmField
    @BindView(R.id.viewPager)
    var viewPager: ViewPager? = null

    @JvmField
    @BindView(R.id.tvEmptySpinner)
    var tvEmptySpinner: TextView? = null

    @JvmField
    @BindView(R.id.llRateCard)
    var llRateCard: LinearLayout? = null


    @JvmField
    @BindView(R.id.tvCarName)
    var tvCarName: TextView? = null

    @JvmField
    @BindView(R.id.tvNightCharges)
    var tvNightCharges: TextView? = null

    @JvmField
    @BindView(R.id.tvWaitingCharges)
    var tvWaitingCharges: TextView? = null

    @JvmField
    @BindView(R.id.tvSurCHarges)
    var tvSurCHarges: TextView? = null

    @JvmField
    @BindView(R.id.tvLuggage)
    var tvLuggage: TextView? = null

    @JvmField
    @BindView(R.id.tvFare)
    var tvFare: TextView? = null

    @JvmField
    @BindView(R.id.tvNightChargeTime)
    var tvNightChargeTime: TextView? = null


    @JvmField
    @BindView(R.id.spinner_type_rate_card)
    var spinner_type_rate_card: Spinner? = null


    @JvmField
    @BindView(R.id.spinner_city)
    var spinner_city: Spinner? = null


    @JvmField
    @BindView(R.id.myDynamicLayout)
    var myDynamicLayout: RelativeLayout? = null

    @JvmField
    @BindView(R.id.nCharge)
    var nCharge: TextView? = null

    @JvmField
    @BindView(R.id.wCharge)
    var wCharge: TextView? = null

    @JvmField
    @BindView(R.id.rateCardIncentive)
    var rateCardIncentive: ImageView? = null

    @JvmField
    @BindView(R.id.sCharge)
    var sCharge: TextView? = null

    @JvmField
    @BindView(R.id.sFare)
    var sFare: TextView? = null

    @JvmField
    @BindView(R.id.cLuggage)
    var cLuggage: TextView? = null

    var mRgAllButtons: RadioGroup? = null


    @JvmField
    @BindView(R.id.recycler_view_rateCard)
    var recycler_view_rateCard: RecyclerView? = null


    var spinnr: List<String?>? = null

    var cityspinner = ArrayList<String>()

    @JvmField
    @BindView(R.id.tabs)
    var tabLayout: TabLayout? = null

    var token: String? = null

    var citycalles: String? = "first"
    var cityID: String? = null
    var cityID1: String? = null
    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_rate_card, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        initView()
        return rootView
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.menu_home_lang, menu!!)
        super.onCreateOptionsMenu(menu!!, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                preferencesManager!!.setStringValue(
                    Constants.SHARED_PREFERENCE_PICKUP_AITPORT,
                    "LOCALITY"
                )
                sharedpreferences!!.edit().clear().commit()
                val intent = Intent(activity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
        return true
    }


    private fun initView() {

        val spinnerLang: Spinner = activity!!.findViewById(R.id.spinnerLang)
        spinnerLang.visibility = View.GONE

        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar_home)
        toolbar.title = "Rate Card"




        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)
        cityID = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_CITY_ID)
        cityID1 = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_CITY_ID)

        getCity()


    }

    private fun getCity() {


        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false) // set cancelable to false
        progressDialog.setMessage("Please Wait") // set message
        progressDialog.show() // show progress dialog


        val cLat = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_CLat)
        val cLong = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_CLong)


        ApiClient.client.getAllowCities("Bearer $token", cLat, cLong)!!.enqueue(object :
            Callback<GetAllowCityResponse?> {
            override fun onResponse(
                call: Call<GetAllowCityResponse?>,
                response: Response<GetAllowCityResponse?>
            ) {

                progressDialog.dismiss()
                if (response.code() == 200) {

                    cityPojoList = response!!.body()!!.cities
                    cityspinner.clear()
                    for (i in response.body()!!.cities.indices) {
                        cityspinner.add(response!!.body()!!.cities.get(i).name)

                    }


                    val cityspinneradapter: ArrayAdapter<*> =
                        ArrayAdapter<Any?>(
                            context!!,
                            R.layout.simple_spinner,
                            cityspinner!! as List<Any?>
                        )
                    cityspinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner_city!!.adapter = cityspinneradapter

                    for (i in cityspinner!!.indices) {
                        val c = cityPojoList[i].id
                        if ((cityID!!.toInt()).equals(c)) {
                            spinner_city!!.setSelection(i)
                        }

                    }


                    spinner_city!!.onItemSelectedListener = this@RateCard

                    citycalles = "first"



                    getRateCard(cityID)


                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(activity, pojo.message, Toast.LENGTH_LONG).show()


                    } catch (exception: IOException) {
                    }

                } else {
                    Toast.makeText(
                        activity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<GetAllowCityResponse?>,
                t: Throwable
            ) {
                progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })

    }


    fun getRateCard(cityId: String?) {


        ApiClient.client.rateCards("Bearer $token", cityId)!!.enqueue(object :
            Callback<RateCardResponsePOJO?> {
            override fun onResponse(
                call: Call<RateCardResponsePOJO?>,
                response: Response<RateCardResponsePOJO?>
            ) {
                // progressDialog.dismiss()
                if (response.code() == 200) {

                    llRateCard!!.visibility = View.VISIBLE
                    getRateCardList = response.body()!!.rateCards!!
                    if (getRateCardList.size > 0) {


                        tabLayout!!.removeAllTabs()
                        for (j in getRateCardList.indices) {
                            tabLayout!!.addTab(
                                tabLayout!!.newTab()
                                    .setCustomView(createTabItemView(getRateCardList[j].image!!))
                            )
                            tvCarName!!.text = getRateCardList[0].name


                            tvFare!!.text =
                                "Minimum fare of ₹ " + getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).minBaseFare +
                                        " for the first 1.5 KM\n" +
                                        "Subsequently, fare will be chargeable at ₹ " +
                                        getRateCardList[0].rateCards!!.get(0).rateCards!!.get(0).fareAfterMinbdist +
                                        " per KM."



                            tvNightCharges!!.text ="Additional charge of "+
                                getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).nightChargesInPercentage + " of Basic Fare for journey between 0.00 AM to 5.00 AM."

                            tvWaitingCharges!!.text =
                                "₹ " + getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).waitingCharges + " per minute."

                            if ((getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).surcharge).equals(
                                    "0"
                                )
                            ) {
                                tvSurCHarges!!.text = "Not Applicable."
                            } else {
                                tvSurCHarges!!.text =
                                    "₹ " + getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).surcharge + " per booking."

                            }





                            tvLuggage!!.text =
                                "₹ " + getRateCardList[0]!!.rateCards!!.get(0).rateCards!!.get(0).chargesPerLuggage +
                                        " per luggage item other than a briefcase or hand bag."

                            tvNightChargeTime!!.text = "(00:00 AM to 5:00 AM)"
                            nCharge!!.text = "Night Charges"
                            wCharge!!.text = "Waiting Charges"
                            sCharge!!.text = "Surcharges"
                            cLuggage!!.text = "Luggage Charges"
                            sFare!!.text = " Basic Fare"


                        }







                        if (getRateCardList.get(0).rateCards!!.size > 1) {
                            tvEmptySpinner!!.visibility = View.GONE
                            spinner_type_rate_card!!.visibility = View.VISIBLE

                            spinnr = java.util.ArrayList()
                            for (i in (getRateCardList.get(0).rateCards)!!.indices) {
                                (spinnr as ArrayList<String?>).add(
                                    getRateCardList[0]!!.rateCards!!.get(i).name
                                )

                            }
                        }else{
                            tvEmptySpinner!!.visibility = View.VISIBLE
                            spinner_type_rate_card!!.visibility = View.GONE
                            tvEmptySpinner!!.text =  getRateCardList[0]!!.rateCards!!.get(0).name
                            ZerothPosition()

                        }



                        mRgAllButtons = activity!!.findViewById(R.id.radiogroup)

                        mRgAllButtons!!.removeAllViews()

                        mRgAllButtons!!.setOrientation(LinearLayout.VERTICAL)
                        for (i in (getRateCardList.get(0).rateCards)!!.get(0).rateCards!!.indices) {
                            val rdbtn = RadioButton(activity)
                            rdbtn.id = View.generateViewId()
                            rdbtn.setTextColor(getResources().getColor(R.color.colorText))
                            rdbtn.setCircleColor(getResources().getColor(R.color.gradientstartcolor))


                            rdbtn.setOnClickListener(this@RateCard)

                            if (cityId.equals("2707")) {
                                if ((getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(
                                        i
                                    ).rateCardType)!!.contains("DOMS")
                                ) {
                                    val imgResource: Int = R.drawable.domestic_icon
                                    rdbtn.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        imgResource,
                                        0
                                    )
                                    rdbtn.setCompoundDrawablePadding(100)
                                }


                                if ((getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(
                                        i
                                    ).rateCardType)!!.contains("INTL")
                                ) {
                                    val imgResource: Int = R.drawable.international_icon
                                    rdbtn.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        imgResource,
                                        0
                                    )
                                    rdbtn.setCompoundDrawablePadding(130)
                                }

                            } else {
                                if ((getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(
                                        i
                                    ).rateCardType)!!.contains("DOMS")
                                ) {
                                    val imgResource: Int = R.drawable.pune_dom
                                    rdbtn.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        imgResource,
                                        0
                                    )
                                    rdbtn.setCompoundDrawablePadding(100)
                                }


                                if ((getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(
                                        i
                                    ).rateCardType)!!.contains("INTL")
                                ) {
                                    val imgResource: Int = R.drawable.pune_intl
                                    rdbtn.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        imgResource,
                                        0
                                    )
                                    rdbtn.setCompoundDrawablePadding(130)
                                }

                            }



                            rdbtn.text =
                                (getRateCardList.get(0).rateCards!!.get(0).rateCards!!.get(i).rateCardType) + ""
                            mRgAllButtons!!.addView(rdbtn)

                            if (i == 0) {
                                mRgAllButtons!!.check(rdbtn.id)
                            }
                        }


                        spinner_type_rate_card!!.visibility = View.VISIBLE
                        spinner_city!!.visibility = View.VISIBLE

                        if (getRateCardList.get(0).rateCards!!.size > 1) {

                            tvEmptySpinner!!.visibility = View.GONE
                            spinner_type_rate_card!!.visibility = View.VISIBLE


                            val auto: ArrayAdapter<*> = ArrayAdapter<Any?>(
                                context!!,
                                R.layout.simple_spinner,
                                spinnr!!
                            )
                            auto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinner_type_rate_card!!.adapter = auto
                        }else{
                            tvEmptySpinner!!.visibility = View.VISIBLE
                            spinner_type_rate_card!!.visibility = View.GONE
                            tvEmptySpinner!!.text =  getRateCardList[0]!!.rateCards!!.get(0).name

                        }


                        //  viewPager!!.adapter = adapter
                        viewPager!!.addOnPageChangeListener(
                            TabLayout.TabLayoutOnPageChangeListener(
                                tabLayout
                            )
                        )
                        tabLayout!!.addOnTabSelectedListener(object :
                            TabLayout.OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab) {

                                rateCardIncentive?.visibility = View.GONE

                                selectedPosition = tab.position

                                if(selectedPosition ==2){
                                    itemSelectedPosition = 0
                                }


                                tvCarName!!.text = getRateCardList[tab.position].name



                                if (getRateCardList.get(tab.position).rateCards!!.size > 1) {

                                    tvEmptySpinner!!.visibility = View.GONE
                                    spinner_type_rate_card!!.visibility = View.VISIBLE
                                    spinnr = java.util.ArrayList()

                                    for (i in (getRateCardList.get(tab.position).rateCards)!!.indices) {
                                        (spinnr as ArrayList<String?>).add(
                                            getRateCardList[tab.position]!!.rateCards!!.get(
                                                i
                                            ).name
                                        )

                                    }



                                    val auto: ArrayAdapter<*> = ArrayAdapter<Any?>(
                                        context!!,
                                        R.layout.simple_spinner,
                                        spinnr!!
                                    )
                                    auto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                    spinner_type_rate_card!!.adapter = auto
                                    spinner_type_rate_card!!.onItemSelectedListener = this@RateCard
                                }else{
                                    tvEmptySpinner!!.visibility = View.VISIBLE
                                    spinner_type_rate_card!!.visibility = View.GONE
                                    tvEmptySpinner!!.text =  getRateCardList[tab.position]!!.rateCards!!.get(0).name
                                    ZerothPosition()

                                }


                                //
                                // viewPager!!.currentItem = tab.position
                                // adapter.notifyDataSetChanged()
                                // adapter.setSelectedPositio(selectedPosition)

                            }


                            override fun onTabUnselected(tab: TabLayout.Tab) {}
                            override fun onTabReselected(tab: TabLayout.Tab) {}
                        })

                    }


                } else if (response.code() == 422) {
                    val gson = GsonBuilder().create()
                    var pojo: ValidationResponse? = ValidationResponse()
                    try {
                        pojo = gson.fromJson(
                            response.errorBody()!!.string(),
                            ValidationResponse::class.java
                        )
                        Toast.makeText(activity, pojo.errors!!.get(0).message, Toast.LENGTH_LONG)
                            .show()


                    } catch (exception: IOException) {
                    }

                } else {
                    Toast.makeText(
                        activity,
                        "Internal server error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<RateCardResponsePOJO?>,
                t: Throwable
            ) {
                //  progressDialog.dismiss()
                Log.d("response", t.stackTrace.toString())
            }
        })
    }


    private fun createTabItemView(imgUri: String): View? {
        val imageView = ImageView(activity)
        val params = FrameLayout.LayoutParams(
            180,
            120
        )
        imageView.setLayoutParams(params)
        Picasso.get().load(imgUri).into(imageView)
        return imageView
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if (ProjectUtilities.checkInternetAvailable(activity)) {

            if (parent!!.id == R.id.spinner_city) {


                if (citycalles.equals("second")) {

                    citycalles = "second"
                    cityID = (cityPojoList.get(position).id).toString()
                    cityID1 = (cityPojoList.get(position).id).toString()


                    getCity()
                } else {
                    citycalles = "second"
                }

            } else {


                itemSelectedPosition = position
                mRgAllButtons = activity!!.findViewById(R.id.radiogroup)

                mRgAllButtons!!.removeAllViews()
                mRgAllButtons!!.setOrientation(LinearLayout.VERTICAL)
                for (i in (getRateCardList.get(selectedPosition).rateCards)!!.get(position).rateCards!!.indices) {
                    val rdbtn = RadioButton(activity)
                    rdbtn.id = View.generateViewId()
                    rdbtn.setTextColor(getResources().getColor(R.color.colorText))
                    rdbtn.setCircleColor(getResources().getColor(R.color.gradientstartcolor))


                    rdbtn.setOnClickListener(this)
                    if (cityID1.equals("2707")) {
                        if ((getRateCardList.get(selectedPosition).rateCards!!.get(position).rateCards!!.get(
                                i
                            ).rateCardType)!!.contains(
                                "DOMS"
                            )
                        ) {
                            val imgResource: Int = R.drawable.domestic_icon
                            rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                            rdbtn.setCompoundDrawablePadding(100)
                        }


                        if ((getRateCardList.get(selectedPosition).rateCards!!.get(position).rateCards!!.get(
                                i
                            ).rateCardType)!!.contains(
                                "INTL"
                            )
                        ) {
                            val imgResource: Int = R.drawable.international_icon
                            rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                            rdbtn.setCompoundDrawablePadding(130)
                        }

                    } else {
                        if ((getRateCardList.get(selectedPosition).rateCards!!.get(position).rateCards!!.get(
                                i
                            ).rateCardType)!!.contains(
                                "DOMS"
                            )
                        ) {
                            val imgResource: Int = R.drawable.pune_dom
                            rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                            rdbtn.setCompoundDrawablePadding(100)
                        }


                        if ((getRateCardList.get(selectedPosition).rateCards!!.get(position).rateCards!!.get(
                                i
                            ).rateCardType)!!.contains(
                                "INTL"
                            )
                        ) {
                            val imgResource: Int = R.drawable.pune_intl
                            rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                            rdbtn.setCompoundDrawablePadding(130)
                        }

                    }

                    rdbtn.text =
                        (getRateCardList.get(selectedPosition).rateCards!!.get(position).rateCards!!.get(
                            i
                        ).rateCardType)
                    mRgAllButtons!!.addView(rdbtn)

                    if (i == 0) {
                        mRgAllButtons!!.check(rdbtn.id)
                    }
                }


                tvCarName!!.text = getRateCardList[selectedPosition].rateCards!!.get(position).name


                tvFare!!.text =
                    "Minimum fare of ₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(
                        position
                    ).rateCards!!.get(
                        position
                    ).minBaseFare +
                            " for the first 1.5 KM\n" +
                            "Subsequently, fare will be chargeable at ₹ " +
                            getRateCardList[selectedPosition].rateCards!!.get(position).rateCards!!.get(
                                position
                            ).fareAfterMinbdist +
                            " per KM."



                tvNightCharges!!.text = "Additional charge of " +
                        getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(
                            position
                        ).nightChargesInPercentage + " of Basic Fare for journey between 0.00 AM to 5.00 AM."

                tvWaitingCharges!!.text =
                    "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(
                        position
                    ).waitingCharges + " per minute."







                if ((getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(0).surcharge).equals(
                        "0"
                    )
                ) {
                    tvSurCHarges!!.text = "Not Applicable."

                } else {
                    tvSurCHarges!!.text =
                        "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(
                            0
                        ).surcharge + " per booking."

                }



                tvLuggage!!.text =
                    "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(position).rateCards!!.get(
                        0
                    ).chargesPerLuggage +
                            " per luggage item other than a briefcase or hand bag."


            }
        }else{
            ProjectUtilities.showToast(
                activity,
                getString(R.string.internet_error)
            )
        }

    }

    override fun onClick(v: View?) {

        val position: Int = mRgAllButtons!!.indexOfChild(v)



        if (position == 0) {
            rateCardIncentive?.visibility = View.GONE


            tvFare!!.text =
                "Minimum fare of ₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).minBaseFare +
                        " for the first 1.5 KM\n" +
                        "Subsequently, fare will be chargeable at ₹ " +
                        getRateCardList[selectedPosition].rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                            position
                        ).fareAfterMinbdist +
                        " per KM."


            tvNightCharges!!.text ="Additional charge of "+
                getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).nightChargesInPercentage + " of Basic Fare for journey between 0.00 AM to 5.00 AM."

            tvWaitingCharges!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).waitingCharges + " per minute."


            if ((getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).surcharge).equals("0")
            ) {
                tvSurCHarges!!.text = "Not Applicable."

            } else {
                tvSurCHarges!!.text =
                    "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                        position
                    ).surcharge +
                            " per booking."

            }

            tvLuggage!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).chargesPerLuggage +
                        " per luggage item other than a briefcase or hand bag."


        } else {



            if (cityID1.equals("2707")) {
                rateCardIncentive?.visibility = View.VISIBLE

                if ((getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                        position
                    ).rateCardType)!!.contains("DOMS")
                ) {
                    rateCardIncentive?.setBackgroundResource(R.drawable.rate_card_incentive)
                } else {
                    rateCardIncentive?.setBackgroundResource(R.drawable.rate_card_incentive_international)
                }
            }


            tvFare!!.text ="Fare will be chargeable for a minimum distance of "+
                    getRateCardList[selectedPosition].rateCards!!.get(
                        itemSelectedPosition
                    ).rateCards!!.get(
                        position
                    ).minBaseDistance +" KM at ₹ "+

             getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).minBaseFare + " per KM. " +
                    "Subsequently, fare will be chargeable in the distance slabs of " +
                    getRateCardList[selectedPosition].rateCards!!.get(
                    itemSelectedPosition
                ).rateCards!!.get(
                    position
                ).distanceSlab + " KM at ₹ "+getRateCardList[selectedPosition].rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                position
            ).fareAfterMinbdist+" per KM. Additionally, incentives are offered to the drivers for providing Pre-Paid Services from the international terminal."




            tvNightCharges!!.text ="Additional charge of "+
                getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).nightChargesInPercentage + " of Basic Fare for journey between 0.00 AM to 5.00 AM."

            tvWaitingCharges!!.text ="Not Applicable."


            if ((getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).surcharge).equals("0")
            ) {
                tvSurCHarges!!.text = "Not Applicable."

            } else {
                tvSurCHarges!!.text =
                    "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                        position
                    ).surcharge +
                            " per booking."

            }

            tvLuggage!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(itemSelectedPosition).rateCards!!.get(
                    position
                ).chargesPerLuggage +
                        " per luggage item other than a briefcase or hand bag."

        }


    }


    fun ZerothPosition(){


        mRgAllButtons = activity!!.findViewById(R.id.radiogroup)

        mRgAllButtons!!.removeAllViews()
        mRgAllButtons!!.setOrientation(LinearLayout.VERTICAL)
        for (i in (getRateCardList.get(selectedPosition).rateCards)!!.get(0).rateCards!!.indices) {
            val rdbtn = RadioButton(activity)
            rdbtn.id = View.generateViewId()
            rdbtn.setTextColor(getResources().getColor(R.color.colorText))
            rdbtn.setCircleColor(getResources().getColor(R.color.gradientstartcolor))


            rdbtn.setOnClickListener(this)
            if (cityID1.equals("2707")) {
                if ((getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(i).rateCardType)!!.contains(
                        "DOMS"
                    )
                ) {
                    val imgResource: Int = R.drawable.domestic_icon
                    rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                    rdbtn.setCompoundDrawablePadding(100)
                }


                if ((getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(i).rateCardType)!!.contains(
                        "INTL"
                    )
                ) {
                    val imgResource: Int = R.drawable.international_icon
                    rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                    rdbtn.setCompoundDrawablePadding(130)
                }

            } else {
                if ((getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(i).rateCardType)!!.contains(
                        "DOMS"
                    )
                ) {
                    val imgResource: Int = R.drawable.pune_dom
                    rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                    rdbtn.setCompoundDrawablePadding(100)
                }


                if ((getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(i).rateCardType)!!.contains(
                        "INTL"
                    )
                ) {
                    val imgResource: Int = R.drawable.pune_intl
                    rdbtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, imgResource, 0)
                    rdbtn.setCompoundDrawablePadding(130)
                }

            }

            rdbtn.text =
                (getRateCardList.get(selectedPosition).rateCards!!.get(0).rateCards!!.get(
                    i
                ).rateCardType)
            mRgAllButtons!!.addView(rdbtn)

            if (i == 0) {
                mRgAllButtons!!.check(rdbtn.id)


            }
        }


        tvCarName!!.text = getRateCardList[selectedPosition].rateCards!!.get(0).name


        tvFare!!.text =
            "Minimum fare of ₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(0).rateCards!!.get(
                0
            ).minBaseFare +
                    " for the first 1.5 KM\n" +
                    "Subsequently, fare will be chargeable at ₹ " +
                    getRateCardList[selectedPosition].rateCards!!.get(0).rateCards!!.get(
                        0
                    ).fareAfterMinbdist +
                    " per KM."



        tvNightCharges!!.text ="Additional charge of "+
            getRateCardList[selectedPosition]!!.rateCards!!.get(0).rateCards!!.get(
                0
            ).nightChargesInPercentage + " of Basic Fare for journey between 0.00 AM to 5.00 AM."

        tvWaitingCharges!!.text =
            "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(0).rateCards!!.get(
                0
            ).waitingCharges + " per minute."







        if ((getRateCardList[selectedPosition]!!.rateCards!!.get(0).rateCards!!.get(0).surcharge).equals(
                "0"
            )
        ) {
            tvSurCHarges!!.text = "Not Applicable."

        } else {
            tvSurCHarges!!.text =
                "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(0).rateCards!!.get(
                    0
                ).surcharge + " per booking."

        }



        tvLuggage!!.text =
            "₹ " + getRateCardList[selectedPosition]!!.rateCards!!.get(0).rateCards!!.get(
                0
            ).chargesPerLuggage +
                    " per luggage item other than a briefcase or hand bag."

    }













    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun RadioButton.setCircleColor(color: Int){
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked), // unchecked
                intArrayOf(android.R.attr.state_checked) // checked
            ), intArrayOf(
                Color.GRAY, // unchecked color
                color // checked color
            )
        )

        // finally, set the radio button's button tint list
        buttonTintList = colorStateList

        // optionally set the button tint mode or tint blend mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            buttonTintBlendMode = BlendMode.SRC_IN
        }else{
            buttonTintMode = PorterDuff.Mode.SRC_IN
        }


    }











}

