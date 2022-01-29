package com.fairfareindia.ui.drawer.ratecard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import com.fairfareindia.R
import com.fairfareindia.databinding.FragmentRateCardsBinding
import com.fairfareindia.ui.home.HomeActivity
import com.fairfareindia.utils.Constants
import com.fairfareindia.utils.PreferencesManager

class RateCardsFragment : Fragment(){

    lateinit var binding: FragmentRateCardsBinding
    private var mContext: Context?= null

    var preferencesManager: PreferencesManager? = null
    var sharedpreferences: SharedPreferences? = null

    private var pagerAdapter: ViewPagerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRateCardsBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {

        mContext = context
        setHasOptionsMenu(true)
        PreferencesManager.initializeInstance(requireActivity())


        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar_home)
        toolbar.title =  getString(R.string.drawer_ratecard)


        sharedpreferences = requireActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE)

        PreferencesManager.initializeInstance(requireActivity().applicationContext)
        preferencesManager = PreferencesManager.instance


        setTabWithPager()
    }

    private fun setTabWithPager() {
        pagerAdapter = ViewPagerAdapter(childFragmentManager)
        val localRateCard = LocalRateCardFragment()
        val intercityRateCard = IntercityRateCardFragment()
        pagerAdapter?.addFragment(
            localRateCard,
            "Local"
        )
        pagerAdapter?.addFragment(
            intercityRateCard,
            "Intercity"
        )
        binding.viewPager.adapter = pagerAdapter
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


}