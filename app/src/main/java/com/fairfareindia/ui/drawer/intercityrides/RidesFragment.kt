package com.fairfareindia.ui.drawer.intercityrides

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairfareindia.R
import com.fairfareindia.databinding.FragmentMyRidesBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.myrides.IMyRidesPresenter
import com.fairfareindia.ui.drawer.myrides.IMyRidesView
import com.fairfareindia.ui.drawer.myrides.MyRidesImplementer
import com.fairfareindia.ui.drawer.myrides.MyTripsAdapter
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.ui.drawer.myrides.ridedetails.MyRideDetailsActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.intercitytrackpickup.TrackPickUpActivity
import com.fairfareindia.ui.ridedetails.RideDetailsActivity
import com.fairfareindia.ui.ridereview.RideReviewActivity
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PaginationScrollListener
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities
import kotlin.collections.ArrayList

class RidesFragment : Fragment(), IMyRidesView{

    private var iMyRidesPresenter: IMyRidesPresenter? = null
    lateinit var binding: FragmentMyRidesBinding
    private var mContext: Context ?= null

    // PAGINATION VARS
    private val PAGE_START = 1

    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private var isLoading = false

    // If current page is the last page (Pagination will stop after this page load)
    private var isLastPage = false

    // indicates the current page which Pagination is fetching.
    private var currentPage = PAGE_START

    var myTripsAdapter: MyTripsAdapter? = null
    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null
    var currentLat: String? = null
    var currentLong: String? = null
    
    private var list: ArrayList<GetRideResponsePOJO.DataItem> = ArrayList()

    private var mAdapter: RidesAdapter ?= null
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyRidesBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {

        mContext = context
        PreferencesManager.initializeInstance(requireActivity())
        preferencesManager = PreferencesManager.instance
        token = preferencesManager?.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        sharedpreferences = mContext?.getSharedPreferences("mypref", Context.MODE_PRIVATE)
        
        iMyRidesPresenter = MyRidesImplementer(this)

        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar_home)
        toolbar.title = "My Rides"
        

        setRecyclerView()
    }

    private fun setRecyclerView() {
        mAdapter = RidesAdapter(mContext, list, object : RidesAdapter.RidesAdapterInterface {
            override fun onItemSelected(position: Int, model: GetRideResponsePOJO.DataItem) {
                if (model.permit_type == Constants.TYPE_INTERCITY){
                    val intent = Intent(activity, TrackPickUpActivity::class.java)
                    intent.putExtra("ride_id", model.id.toString())
                    startActivity(intent)
                }else{
                    if (ProjectUtilities.checkInternetAvailable(activity)) {
                        val intent = Intent(activity, MyRideDetailsActivity::class.java)
                        intent.putExtra("Id", model.id.toString())
                        startActivity(intent)
                    }else{
                        ProjectUtilities.showToast(
                            activity,
                            getString(R.string.internet_error)
                        )
                    }
                }
            }

            override fun onStartRideClick(position: Int, model: GetRideResponsePOJO.DataItem) {
                startRide(model)
            }

            override fun onRateRideClick(position: Int, model: GetRideResponsePOJO.DataItem) {
                if (ProjectUtilities.checkInternetAvailable(activity)) {
                    val intent = Intent(activity, RideReviewActivity::class.java)
                    intent.putExtra("MyRides_id", model.id.toString())
                    intent.putExtra("MyRides", "DrawerMyRides")
                    startActivity(intent)
                }else{
                    ProjectUtilities.showToast(
                        activity,
                        getString(R.string.internet_error)
                    )
                }
            }

            override fun onViewInfoClick(position: Int, model: GetRideResponsePOJO.DataItem) {
            }

        })

        var layoutManager = LinearLayoutManager(mContext)

        
        binding.recyclerViewMyRides.layoutManager = layoutManager
        binding.recyclerViewMyRides.adapter = mAdapter

        binding.recyclerViewMyRides.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                this@RidesFragment.isLoading = true
                currentPage++
                Handler().postDelayed({ loadNextPage() }, 1000)
            }

            override fun getTotalPageCount(): Int {
                return 99
            }

            override fun isLastPage(): Boolean {
                return this@RidesFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@RidesFragment.isLoading
            }
        })

        loadNextPage()
    }

    /**
     * Pagination
     **/

    private fun resetAPI(): Unit {
        // To clear already set adapter and new list on swipe refresh
        mAdapter!!.clear()
        currentPage = PAGE_START
        isLastPage = false
        iMyRidesPresenter?.getRide("Bearer " + token, currentPage, currentLat, currentLong)
    }

    private fun loadNextPage() {
        iMyRidesPresenter?.getRide("Bearer " + token, currentPage, currentLat, currentLong)
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



    override fun schduleRideSuccess(scheduleRideResponsePOJO: ScheduleRideResponsePOJO?) {
    }

    override fun validationError(validationResponse: ValidationResponse?) {
        Toast.makeText(
            activity,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun getRidesSuccess(getRideResponsePOJO: GetRideResponsePOJO?) {


        if (currentPage == PAGE_START) {
            mAdapter!!.clear()
        }
        mAdapter!!.removeLoadingFooter()
        isLoading = false

        list = getRideResponsePOJO?.data as ArrayList<GetRideResponsePOJO.DataItem>


        // if list<10 then last page true
        if (list.size < 10) {
            isLastPage = true
        }
        if (list.size > 0) {
            mAdapter!!.addAll(list)
        }
        if (list.size > 0 || currentPage != PAGE_START) {
            binding.recyclerViewMyRides.visibility = View.VISIBLE
            binding.rlEmpty.visibility = View.VISIBLE
        } else {
            binding.recyclerViewMyRides.visibility = View.GONE
            binding.rlEmpty.visibility = View.GONE
        }


        // Add loading footer if last page is false
        if (!isLastPage) {
            mAdapter!!.addLoadingFooter()
        }


    }

    override fun showWait() {
        ProjectUtilities.showProgressDialog(activity)
    }

    override fun removeWait() {
        ProjectUtilities.dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        Toast.makeText(activity, appErrorMessage, Toast.LENGTH_LONG).show()
    }

    private fun startRide(model: GetRideResponsePOJO.DataItem) {

        val intent = Intent(activity, RideDetailsActivity::class.java)
        intent.putExtra("MyRides_vehicle_rate_card_id", model.vehicleRateCardId.toString())
        intent.putExtra("MyRides_airport_ratr_card_id", model.airportRateCardId)
        intent.putExtra("MyRides_RideID", model.id.toString())
        intent.putExtra("MyRidessLat", model.originPlaceLat)
        intent.putExtra("MyRidessLong", model.originPlaceLong)
        intent.putExtra("MyRidesdLat", model.destinationPlaceLat)
        intent.putExtra("MyRidesdLong", model.destinationPlaceLong)
        intent.putExtra("MyRidesoriginalAddress", model.originFullAddress)
        intent.putExtra("MyRidesdestinationAddress", model.destinationFullAddress)
        intent.putExtra("MyRide", "MyRide")
        intent.putExtra("compareRideList", model.estimatedTrackRide!!.tolls as java.util.ArrayList<GetRideResponsePOJO.TollsItem>?)

        startActivity(intent)


    }






}