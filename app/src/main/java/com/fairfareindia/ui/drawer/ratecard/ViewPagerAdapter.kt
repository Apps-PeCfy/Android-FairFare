package com.fairfareindia.ui.drawer.ratecard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class ViewPagerAdapter(fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!) {
    private val mFragmentList: MutableList<Fragment>
    private val mFragmentTitleList: MutableList<String>
    private val mFragmentImageList: List<Int>
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    fun getPageDrawable(position: Int): Int {
        return mFragmentImageList[position]
    }

    init {
        mFragmentList = ArrayList()
        mFragmentTitleList = ArrayList()
        mFragmentImageList = ArrayList()
    }
}