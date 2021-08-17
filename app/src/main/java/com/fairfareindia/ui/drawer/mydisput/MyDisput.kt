package com.fairfareindia.ui.drawer.mydisput

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.fairfareindia.R
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.mydisput.disputDetail.DisputDetailActivity
import com.fairfareindia.ui.drawer.mydisput.pojo.DeleteDisputResponsePOJO
import com.fairfareindia.ui.drawer.mydisput.pojo.GetDisputResponsePOJO
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities

class MyDisput : Fragment(), IMyDisputView, MyDisPutesAdapter.IDisputClickListener {

    private var iMyDisputPresenter: IMyDisputPresenter? = null
    var token: String? = null


    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null


    var myDisPutesAdapter: MyDisPutesAdapter? = null
    private var myDisputsList: List<GetDisputResponsePOJO.DataItem> = ArrayList()


    @JvmField
    @BindView(R.id.recycler_view_myRides)
    var recycler_view_myRides: RecyclerView? = null

    @JvmField
    @BindView(R.id.ivImg)
    var ivImg: ImageView? = null

    @JvmField
    @BindView(R.id.tvEmptyTxt)
    var tvEmptyTxt: TextView? = null

    @JvmField
    @BindView(R.id.rlEmpty)
    var rlEmpty: RelativeLayout? = null

    @JvmField
    @BindView(R.id.rl_sort)
    var rl_sort: RelativeLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_rides, container, false)
        ButterKnife.bind(this, rootView)
        setHasOptionsMenu(true)
        rl_sort!!.visibility = View.GONE
        initView()

        PreferencesManager.initializeInstance(activity!!.applicationContext)
        preferencesManager = PreferencesManager.instance
        token = preferencesManager!!.getStringValue(Constants.SHARED_PREFERENCE_LOGIN_TOKEN)

        iMyDisputPresenter = MyDisputImplementer(this)
        iMyDisputPresenter!!.getMyDisput(token)

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
        toolbar.title = "My Disputes"

        sharedpreferences = activity!!.getSharedPreferences("mypref", Context.MODE_PRIVATE)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun validationError(validationResponse: ValidationResponse?) {
        Toast.makeText(
            activity,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun getDisputSuccess(getDisputResponsePOJO: GetDisputResponsePOJO?) {

        myDisputsList = getDisputResponsePOJO!!.data!!

        if (myDisputsList.size > 0) {
            myDisPutesAdapter = MyDisPutesAdapter(activity, myDisputsList)
            recycler_view_myRides!!.layoutManager = LinearLayoutManager(activity)
            recycler_view_myRides!!.adapter = myDisPutesAdapter
            myDisPutesAdapter!!.setClickListener(this@MyDisput)

            myDisPutesAdapter!!.notifyDataSetChanged()
        } else {

            myDisPutesAdapter = MyDisPutesAdapter(activity, myDisputsList)
            recycler_view_myRides!!.layoutManager = LinearLayoutManager(activity)
            recycler_view_myRides!!.adapter = myDisPutesAdapter
            myDisPutesAdapter!!.setClickListener(this@MyDisput)

            myDisPutesAdapter!!.notifyDataSetChanged()

            rlEmpty!!.visibility = View.VISIBLE
            ivImg!!.setBackgroundResource(R.drawable.empty_disput)
            tvEmptyTxt!!.text = "You have not registered any Disputes yet."
        }


    }

    override fun deleteDisputSuccess(deleteDisputResponsePOJO: DeleteDisputResponsePOJO?) {


        iMyDisputPresenter!!.getMyDisput(token)

    }

    override fun filecomplaintSuccess(deleteDisputResponsePOJO: DeleteDisputResponsePOJO?) {

        val intent = Intent(activity, HomeActivity::class.java)
        intent.action = "filecomplaintSuccess"
        startActivity(intent)


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

    override fun fileComplaintClick(id: Int) {


        if (ProjectUtilities.checkInternetAvailable(activity)) {

            val alertDialog = AlertDialog.Builder(activity!!, R.style.alertDialog)
            alertDialog.setTitle("FairFareIndia")
            alertDialog.setMessage("Are you sure you want to file Complaint?")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("YES") { dialog, which ->

                iMyDisputPresenter!!.fileComplaint(token, id.toString())

            }
            alertDialog.setNegativeButton("N0") { dialog, which -> dialog.cancel() }
            alertDialog.show()
        }else{

            ProjectUtilities.showToast(
                activity,
                getString(R.string.internet_error)
            )
        }


    }

    override fun deleteDisputClick(id: Int) {

        if (ProjectUtilities.checkInternetAvailable(activity)) {

            val alertDialog = AlertDialog.Builder(activity!!, R.style.alertDialog)
            alertDialog.setTitle("FairFareIndia")

            alertDialog.setMessage("Are you sure you want to delete Dispute?")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Yes") { dialog, which ->

                iMyDisputPresenter!!.deleteDisput(token, id.toString())

            }
            alertDialog.setNegativeButton("No") { dialog, which -> dialog.cancel() }
            alertDialog.show()

        }else{
            ProjectUtilities.showToast(
                activity,
                getString(R.string.internet_error)
            )

        }

    }

    override fun detailDisputClick(id: Int) {

        if (ProjectUtilities.checkInternetAvailable(activity)) {

            val intent = Intent(activity, DisputDetailActivity::class.java)
            intent.putExtra("Id", id.toString())
            startActivity(intent)
        }else{
            ProjectUtilities.showToast(
                activity,
                getString(R.string.internet_error)
            )

        }
    }

}