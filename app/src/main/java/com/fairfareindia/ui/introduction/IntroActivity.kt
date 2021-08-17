package com.fairfareindia.ui.introduction

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fairfareindia.R
import com.fairfareindia.ui.Login.LoginActivity
import com.fairfareindia.utils.PreferencesManager


class IntroActivity : AppCompatActivity() {
    var arrayList: ArrayList<Int>? = null
    var tvGetStarted: TextView? = null
    lateinit var screens: IntArray
    var Skip: TextView? = null
    lateinit var dot: Array<TextView?>
    var layout_dot: LinearLayout? = null
    var vp: ViewPager? = null
    var myvpAdapter: MyViewPagerAdapter? = null
    var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object :
        ViewPager.OnPageChangeListener {
        @SuppressLint("SetTextI18n")
        override fun onPageSelected(position: Int) {
            if (position == screens.size - 1) {
                Skip!!.visibility = View.GONE
                layout_dot!!.visibility = View.GONE
                tvGetStarted!!.visibility = View.VISIBLE
            } else {
                addDot(position)
                tvGetStarted!!.visibility = View.GONE
                Skip!!.visibility = View.VISIBLE
                layout_dot!!.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        override fun onPageScrollStateChanged(arg0: Int) {}
    }

    //PreferenceManager preferenceManager;
    private var mPreferencesManager: PreferencesManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        setContentView(R.layout.activity_introduction)
        layout_dot = findViewById(R.id.layout_dot)

        arrayList = ArrayList()
        arrayList!!.add(R.color.Red)
        arrayList!!.add(R.color.Red)
        arrayList!!.add(R.color.Red)
        arrayList!!.add(R.color.Red)
        addDot(0)
        mPreferencesManager = PreferencesManager.instance
        vp = findViewById(R.id.view_pager)
        tvGetStarted = findViewById(R.id.tvGetStarted)
        Skip = findViewById(R.id.skip)
        screens = intArrayOf(
            R.layout.intro_screen1,
            R.layout.intro_screen2,
            R.layout.intro_screen3,
            R.layout.intro_screen4
        )
        myvpAdapter = MyViewPagerAdapter()
        vp!!.setAdapter(myvpAdapter)
        vp!!.addOnPageChangeListener(viewPagerPageChangeListener)
    }

    private fun addDot(page_position: Int) {

        dot = arrayOfNulls(arrayList!!.size)
        layout_dot!!.removeAllViews()



        for (i in 0 until dot.size) {
            dot[i] = TextView(this)
            dot[i]!!.text = Html.fromHtml("&#9673;")
            //set default dot color
            dot[i]!!.setTextColor(resources.getColor(R.color.Black))
            layout_dot!!.addView(dot[i])
        }
        //set active dot color
        //set active dot color
        dot[page_position]!!.setTextColor(resources.getColor(R.color.colorAccent))

    }

    fun next(v: View?) {
        val i = getItem(+1)
        if (i < screens.size) {
            vp!!.currentItem = i
        } else {
            launchMain()
        }
    }

    fun getStarted(view: View?) {
        launchMain()
    }

    fun skip(view: View?) {
        launchMain()
    }

    private fun getItem(i: Int): Int {
        return vp!!.currentItem + i
    }

    private fun launchMain() {
        //preferenceManager.setFirstTimeLaunch(false);
        startActivity(Intent(this@IntroActivity, LoginActivity::class.java))
        finish()
    }

    inner class MyViewPagerAdapter : PagerAdapter() {
        private var inflater: LayoutInflater? = null
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            inflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater!!.inflate(screens[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return screens.size
        }

        override fun destroyItem(
            container: ViewGroup,
            position: Int,
            `object`: Any
        ) {
            val v = `object` as View
            container.removeView(v)
        }

        override fun isViewFromObject(
            v: View,
            `object`: Any
        ): Boolean {
            return v === `object`
        }
    }
}