package com.fairfareindia.ui.drawer.notifications

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairfareindia.R
import com.fairfareindia.databinding.FragmentNotificationsBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.intercityrides.ridedetails.IntercityRideDetailsActivity
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.ui.intercitytrackpickup.RideDetailModel
import com.fairfareindia.ui.intercitytrackpickup.TrackPickUpActivity
import com.fairfareindia.ui.intercitytrackride.InterCityTrackRideActivity
import com.fairfareindia.utils.*

class NotificationsFragment : Fragment(), INotificationView {
    lateinit var binding: FragmentNotificationsBinding
    private var mContext: Context?= null

    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null
    var iNotificationsPresenter: INotificationPresenter? = null


    // PAGINATION VARS
    private val PAGE_START = 1

    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private var isLoading = false

    // If current page is the last page (Pagination will stop after this page load)
    private var isLastPage = false

    // indicates the current page which Pagination is fetching.
    private var currentPage = PAGE_START

    private var list: ArrayList<NotificationModel.DataItem> = ArrayList()

    private var mAdapter: NotificationAdapter?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBinding.inflate(layoutInflater)
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
        
        iNotificationsPresenter = NotificationImplementer(this)


        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar_home)
        toolbar.title = getString(R.string.drawer_notifications)

        setRecyclerView()
    }

    private fun setRecyclerView() {
        mAdapter = NotificationAdapter(mContext, list, object : NotificationAdapter.NotificationAdapterInterface {
            override fun onItemSelected(position: Int, model: NotificationModel.DataItem) {
                if (model.data?.screen == Constants.NOTIF_SCREEN_RIDE && !model.data?.ride_id.isNullOrEmpty()) {
                    iNotificationsPresenter?.getRideDetail(token, model.data?.ride_id)
                }
            }
            

        })

        var layoutManager = LinearLayoutManager(mContext)


        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = mAdapter

        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                this@NotificationsFragment.isLoading = true
                currentPage++
                Handler().postDelayed({ loadNextPage() }, 1000)
            }

            override fun getTotalPageCount(): Int {
                return 99
            }

            override fun isLastPage(): Boolean {
                return this@NotificationsFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@NotificationsFragment.isLoading
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            resetAPI()
        }

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
        iNotificationsPresenter?.getNotificationList(token, currentPage)
    }

    private fun loadNextPage() {
        iNotificationsPresenter?.getNotificationList(token, currentPage)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_lang, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                preferencesManager?.setStringValue(
                    Constants.SHARED_PREFERENCE_PICKUP_AITPORT,
                    "LOCALITY"
                )
                sharedpreferences?.edit()?.clear()?.apply()
                val intent = Intent(activity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
        return true
    }


    override fun validationError(validationResponse: ValidationResponse?) {
        Toast.makeText(
            activity,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun getNotificationListSuccess(model: NotificationModel?) {
        binding.swipeRefresh.isRefreshing = false

        if (currentPage == PAGE_START) {
            mAdapter!!.clear()
        }
        mAdapter!!.removeLoadingFooter()
        isLoading = false

        list = model?.data as ArrayList<NotificationModel.DataItem>


        // if list<10 then last page true
        if (list.size < 10) {
            isLastPage = true
        }
        if (list.size > 0) {
            mAdapter!!.addAll(list)
        }
        if (list.size > 0 || currentPage != PAGE_START) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.rlEmpty.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.rlEmpty.visibility = View.VISIBLE
        }


        // Add loading footer if last page is false
        if (!isLastPage) {
            mAdapter!!.addLoadingFooter()
        }


    }

    override fun getRideDetailSuccess(model: RideDetailModel?) {
        if (model?.data?.status == Constants.BOOKING_SCHEDULED || model?.data?.status == Constants.BOOKING_ARRIVING || model?.data?.status == Constants.BOOKING_ARRIVED){
                  val intent = Intent(activity, TrackPickUpActivity::class.java)
                  intent.putExtra("ride_id", model?.data?.id.toString())
                  startActivity(intent)
              }else if (model?.data?.status == Constants.BOOKING_ACTIVE){
                  val intent = Intent(activity, InterCityTrackRideActivity::class.java)
                  intent.putExtra("ride_id", model?.data?.id.toString())
                  startActivity(intent)
              }else if (model?.data?.status == Constants.BOOKING_COMPLETED){
                  val intent = Intent(activity, IntercityRideDetailsActivity::class.java)
                  intent.putExtra("ride_id", model.data?.id.toString())
                  startActivity(intent)
              }
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
        setup()
        if (Constants.SHOULD_RELOAD){
            Constants.SHOULD_RELOAD = false
            resetAPI()
        }
    }

    /**
     * BROADCAST RECEIVER FOR UNREAD MESSAGE INDICATION
     */
    private fun setup() {
        LocalBroadcastManager.getInstance(mContext!!).registerReceiver(
            mCountReceiver,
            IntentFilter("refresh_ride_list")
        )
    }

    private val mCountReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && action == "refresh_ride_list") {
                resetAPI()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(mContext!!).unregisterReceiver(mCountReceiver)
    }

   
}