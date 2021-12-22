package com.fairfareindia.ui.drawer.intercityrides

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.databinding.FragmentMyRidesBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.common.CommonMessageDialog
import com.fairfareindia.ui.drawer.intercityrides.ridedetails.IntercityRideDetailsActivity
import com.fairfareindia.ui.drawer.myrides.pojo.GetRideResponsePOJO
import com.fairfareindia.ui.drawer.myrides.ridedetails.MyRideDetailsActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.intercitytrackpickup.TrackPickUpActivity
import com.fairfareindia.ui.intercitytrackride.InterCityTrackRideActivity
import com.fairfareindia.ui.ridedetails.RideDetailsActivity
import com.fairfareindia.ui.ridereview.RideReviewActivity
import com.fairfareindia.ui.viewride.pojo.ScheduleRideResponsePOJO
import com.fairfareindia.utils.*

class RidesFragment : Fragment(), IRidesView{

    private var iRidesPresenter: IRidesPresenter? = null
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

    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null
    var currentLat: String? = null
    var currentLong: String? = null

    var commonMessageDialog: CommonMessageDialog? = null

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
        setHasOptionsMenu(true)
        PreferencesManager.initializeInstance(requireActivity())
        preferencesManager = PreferencesManager.instance
        token = preferencesManager?.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        sharedpreferences = mContext?.getSharedPreferences("mypref", Context.MODE_PRIVATE)

        iRidesPresenter = RidesImplementer(this)

        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar_home)
        toolbar.title = getString(R.string.title_my_rides)
        

        setRecyclerView()
    }

    private fun setRecyclerView() {
        mAdapter = RidesAdapter(mContext, list, object : RidesAdapter.RidesAdapterInterface {
            override fun onItemSelected(position: Int, model: GetRideResponsePOJO.DataItem) {
                if (model.status == Constants.BOOKING_SCHEDULED || model.status == Constants.BOOKING_ARRIVING || model.status == Constants.BOOKING_ARRIVED){
                    val intent = Intent(activity, TrackPickUpActivity::class.java)
                    intent.putExtra("ride_id", model.id.toString())
                    startActivity(intent)
                }else if (model.status == Constants.BOOKING_ACTIVE){
                    val intent = Intent(activity, InterCityTrackRideActivity::class.java)
                    intent.putExtra("ride_id", model.id.toString())
                    startActivity(intent)
                }else if (model.status == Constants.BOOKING_COMPLETED){
                    val intent = Intent(activity, IntercityRideDetailsActivity::class.java)
                    intent.putExtra("ride_id", model.id.toString())
                    startActivity(intent)
                }
            }

            override fun onStartRideClick(position: Int, model: GetRideResponsePOJO.DataItem) {
            }

            override fun onCancelRideClick(position: Int, model: GetRideResponsePOJO.DataItem) {
                openConfirmationDialog(model)
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
                openInfoDialog(model)
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

        binding.swipeRefresh.setOnRefreshListener {
            resetAPI()
        }

        loadNextPage()
    }

    private fun openConfirmationDialog(model: GetRideResponsePOJO.DataItem) {
        commonMessageDialog = CommonMessageDialog(context,getString(R.string.str_cancel_ride_message), getString(R.string.btn_cancel_not), getString(R.string.btn_proceed), object : CommonMessageDialog.CommonMessageDialogInterface{
            override fun onPositiveButtonClick() {
                commonMessageDialog?.dismiss()
            }

            override fun onNegativeButtonClick() {
                iRidesPresenter?.cancelRide(token, model.id.toString(), Constants.BOOKING_CANCELLED)
                commonMessageDialog?.dismiss()
            }

        })
        commonMessageDialog?.setCancelable(false)
        commonMessageDialog?.show()
    }



    private fun openInfoDialog(model: GetRideResponsePOJO.DataItem) {
        var eventDialogBind = AddressPopUp()

        eventDialogBind.eventInfoDialog = context?.let { Dialog(it, R.style.dialog_style) }
        eventDialogBind.eventInfoDialog?.setCancelable(true)
        val inflater1 = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view12: View = inflater1.inflate(R.layout.destination_address_popup, null)
        eventDialogBind.eventInfoDialog?.setContentView(view12)
        ButterKnife.bind(eventDialogBind, view12)

        eventDialogBind.tvDestinationAddress?.text = model.estimatedTrackRide?.destinationFullAddress
        eventDialogBind.eventInfoDialog?.show()

    }

    /**
     * Pagination
     **/

    private fun resetAPI(): Unit {
        // To clear already set adapter and new list on swipe refresh
        mAdapter!!.clear()
        currentPage = PAGE_START
        isLastPage = false
        iRidesPresenter?.getRide("Bearer " + token, currentPage, currentLat, currentLong)
    }

    private fun loadNextPage() {
        iRidesPresenter?.getRide("Bearer " + token, currentPage, currentLat, currentLong)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_lang, menu)
        super.onCreateOptionsMenu(menu, inflater)
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
        binding.swipeRefresh.isRefreshing = false

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
            binding.rlEmpty.visibility = View.GONE
        } else {
            binding.recyclerViewMyRides.visibility = View.GONE
            binding.rlEmpty.visibility = View.VISIBLE
        }


        // Add loading footer if last page is false
        if (!isLastPage) {
            mAdapter!!.addLoadingFooter()
        }


    }

    override fun getCancelRideSuccess(getRideResponsePOJO: GetRideResponsePOJO?) {
        binding.swipeRefresh.isRefreshing = false
        resetAPI()
    }

    override fun showWait() {
        ProjectUtilities.showProgressDialog(activity)
    }

    override fun removeWait() {
        binding.swipeRefresh.isRefreshing = false
        ProjectUtilities.dismissProgressDialog()
    }

    override fun onFailure(appErrorMessage: String?) {
        binding.swipeRefresh.isRefreshing = false
        Toast.makeText(activity, appErrorMessage, Toast.LENGTH_LONG).show()
    }




    override fun onResume() {
        super.onResume()
        if (Constants.SHOULD_RELOAD){
            Constants.SHOULD_RELOAD = false
            resetAPI()
        }
    }


}