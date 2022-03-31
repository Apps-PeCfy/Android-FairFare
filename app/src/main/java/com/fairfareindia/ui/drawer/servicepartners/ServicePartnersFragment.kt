package com.fairfareindia.ui.drawer.servicepartners

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.fairfareindia.R
import com.fairfareindia.databinding.FragmentMyRidesBinding
import com.fairfareindia.databinding.FragmentServicePartnersBinding
import com.fairfareindia.ui.Login.pojo.ValidationResponse
import com.fairfareindia.ui.drawer.intercityrides.GetRideResponsePOJO
import com.fairfareindia.ui.drawer.intercityrides.IRidesPresenter
import com.fairfareindia.ui.drawer.intercityrides.RidesImplementer
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager
import com.fairfareindia.utils.ProjectUtilities

class ServicePartnersFragment : Fragment(), IServicePartnersView {

    private var iServicePartnersPresenter: IServicePartnersPresenter? = null
    lateinit var binding: FragmentServicePartnersBinding
    private var mContext: Context?= null

    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null
    var token: String? = null
    var mAdapter: ServicePartnersAdapter? = null
    var list: ArrayList<ServicePartnerModel.DataItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServicePartnersBinding.inflate(layoutInflater)
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

        iServicePartnersPresenter = ServicePartnersImplementer(this)

        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar_home)
        toolbar.title = getString(R.string.drawer_service_partners)


        iServicePartnersPresenter?.getServicePartners(token)
        setRecyclerView()
    }

    private fun setRecyclerView() {
        mAdapter = ServicePartnersAdapter(context, list, object : ServicePartnersAdapter.ServicePartnersAdapterInterface{
            override fun onItemSelected(position: Int, model: ServicePartnerModel.DataItem) {

            }

        })

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = mAdapter
        
        binding.swipeRefresh.setOnRefreshListener { 
            iServicePartnersPresenter?.getServicePartners(token)
        }
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







    override fun validationError(validationResponse: ValidationResponse?) {
        Toast.makeText(
            activity,
            validationResponse!!.errors!![0].message,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun getServicePartnersSuccess(model: ServicePartnerModel?) {
        list = model?.data!!
        mAdapter?.updateAdapter(list)
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
}