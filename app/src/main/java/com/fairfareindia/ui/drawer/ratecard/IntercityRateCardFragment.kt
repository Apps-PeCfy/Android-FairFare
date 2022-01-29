package com.fairfareindia.ui.drawer.ratecard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairfareindia.R
import com.fairfareindia.databinding.FragmentIntercityRateCardBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.ratecard.pojo.RateCardModel
import com.fairfareindia.ui.home.pojo.GetAllowCityResponse
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.squareup.picasso.Picasso


class IntercityRateCardFragment : Fragment(), IRateCardsView {

    private var iRateCardsPresenter: IRateCardsPresenter? = null
    lateinit var binding: FragmentIntercityRateCardBinding
    private var mContext: Context?= null
    private var mAdapter: RateCardAdapter?= null


    private var fromCityList: ArrayList<GetAllowCityResponse.CitiesItem> = ArrayList()
    private var toCityList: ArrayList<GetAllowCityResponse.CitiesItem> = ArrayList()
    private var localCityList: ArrayList<GetAllowCityResponse.CitiesItem> = ArrayList()

    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null
    var fromCityID: String? = null
    var toCityID: String? = null
    var rateCardModel: RateCardModel? = null
    var selectedRateCardModel: RateCardModel.RateCardsItem? = null


    private var fromCitySpinner = ArrayList<String>()
    private var toCitySpinner = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntercityRateCardBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {

        mContext = context
        setHasOptionsMenu(true)
        PreferencesManager.initializeInstance(requireActivity())
        preferencesManager = PreferencesManager.instance
        token = preferencesManager?.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        sharedpreferences = mContext?.getSharedPreferences("mypref", Context.MODE_PRIVATE)

        iRateCardsPresenter = RateCardsImplementer(this)

        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar_home)
        toolbar.title = getString(R.string.drawer_ratecard)

       // iRateCardsPresenter?.getCity(token, "", "")
        iRateCardsPresenter?.getFromInterCities(token)

        setListeners()

    }

    private fun setListeners() {
        binding.apply {
            spinnerFromCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (!fromCityList.isNullOrEmpty()){
                        fromCityID = fromCityList[position].id.toString()
                        toCitySpinner.clear()
                        iRateCardsPresenter?.getToInterCities(token, fromCityID)
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            spinnerToCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    if (!toCityList.isNullOrEmpty() && !fromCityID.isNullOrEmpty()){
                        toCityID = toCityList[position].id.toString()
                        iRateCardsPresenter?.getRateCards(token, Constants.TYPE_INTERCITY, fromCityID, toCityID)
                    }


                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

            tabs.setOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    selectedRateCardModel = rateCardModel?.rateCards!![tab.position]
                    setRecyclerView()
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

            swipeRefresh.setOnRefreshListener {
                swipeRefresh.isRefreshing = false
            }
        }

    }

    private fun setRecyclerView() {
        if(!selectedRateCardModel?.rateCardsDetails.isNullOrEmpty()){
            mAdapter = RateCardAdapter(mContext, selectedRateCardModel?.rateCardsDetails!!, object : RateCardAdapter.RateCardAdapterInterface{
                override fun onItemSelected(position: Int, model: RateCardModel.RateCardsDetailItem) {
                    selectedRateCardModel = rateCardModel?.rateCards!![position]
                    setRecyclerView()
                }

            })

            binding.recyclerView.layoutManager = LinearLayoutManager(mContext)
            binding.recyclerView.adapter = mAdapter
        }


    }

    private fun createTabItemView(imgUri: String): View? {
        val imageView = ImageView(mContext)
        val params = FrameLayout.LayoutParams(
            150,
            100
        )
        imageView.layoutParams = params
        Picasso.get().load(imgUri).into(imageView)
        return imageView
    }


    /**
     * API CALLING SUCCESS
     */

    override fun getRateCards(model: RateCardModel?) {
       rateCardModel = model
        if (rateCardModel?.rateCards.isNullOrEmpty()){
            binding.recyclerView.visibility = View.GONE
            binding.rlNoData.visibility = View.VISIBLE
        }else{
            binding.recyclerView.visibility = View.VISIBLE
            binding.rlNoData.visibility = View.GONE
            binding.tabs.removeAllTabs()
            if (!rateCardModel?.rateCards.isNullOrEmpty()){
                for (vehicleRateCardModel in rateCardModel?.rateCards!!) {
                    binding.tabs!!.addTab(
                        binding.tabs.newTab()
                            .setCustomView(createTabItemView(vehicleRateCardModel.image!!))
                    )
                }

                selectedRateCardModel = rateCardModel?.rateCards!![0]
                setRecyclerView()
            }



        }

    }

    override fun getCitySuccess(getAllowCityResponse: GetAllowCityResponse?) {
        localCityList = getAllowCityResponse?.cities!!
    }

    override fun getFromInterCitiesSuccess(getAllowCityResponse: GetAllowCityResponse?) {
        fromCityList = getAllowCityResponse?.cities!!
        if (!fromCityList.isNullOrEmpty()){
            fromCitySpinner.clear()


            for (cityModel in fromCityList) {
                fromCitySpinner.add(cityModel.name)

            }


            val cityspinneradapter: ArrayAdapter<*> =
                ArrayAdapter<Any?>(
                    mContext!!,
                    R.layout.simple_spinner,
                    fromCitySpinner as List<Any?>
                )
            cityspinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerFromCity.adapter = cityspinneradapter


            fromCityID = fromCityList[0].id.toString()
            iRateCardsPresenter?.getToInterCities(token, fromCityID)
        }

    }

    override fun getToInterCitiesSuccess(getAllowCityResponse: GetAllowCityResponse?) {
        toCityList = getAllowCityResponse?.cities!!
        if (!toCityList.isNullOrEmpty() && !fromCityID.isNullOrEmpty()){
            toCitySpinner.clear()
            for (cityModel in toCityList) {
                toCitySpinner.add(cityModel.name)

            }


            val cityspinneradapter: ArrayAdapter<*> =
                ArrayAdapter<Any?>(
                    mContext!!,
                    R.layout.simple_spinner,
                    toCitySpinner as List<Any?>
                )
            cityspinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerToCity.adapter = cityspinneradapter

            toCityID = toCityList[0].id.toString()
            iRateCardsPresenter?.getRateCards(token, Constants.TYPE_INTERCITY, fromCityID, toCityID)
        }
    }

    override fun validationError(validationResponse: ValidationResponse?) {
        Toast.makeText(
            mContext,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun showWait() {
       // ProjectUtilities.showProgressDialog(mContext)
    }

    override fun removeWait() {
        binding.swipeRefresh.isRefreshing = false
        ProjectUtilities.dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        binding.swipeRefresh.isRefreshing = false
        Toast.makeText(activity, appErrorMessage, Toast.LENGTH_LONG).show()
    }


}