package com.example.meeting_app.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.meeting_app.R
import com.example.meeting_app.databinding.ActivityOnBoardingBinding
import com.example.meeting_app.ui.login.LoginActivity
import com.example.meeting_app.utils.pref.OnBoardingPref
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var activityOnBoardingBinding: ActivityOnBoardingBinding
    private lateinit var sliderAdapter: SliderAdapter
    private var dots: Array<TextView?>? = null
    private lateinit var layouts: Array<Int>
    private var sliderChangeListener: ViewPager.OnPageChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityOnBoardingBinding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(activityOnBoardingBinding.root)
        supportActionBar?.hide()
        initSliderAdapter()
        initSliderChangeListener()
        dataSet()
        interactions()
    }

    private fun initSliderAdapter() {
        layouts = arrayOf(
            R.layout.on_boarding_slide1,
            R.layout.on_boarding_slide2,
            R.layout.on_boarding_slide3,
            R.layout.on_boarding_slide4,
        )
        sliderAdapter = SliderAdapter(this, layouts)
    }

    private fun dataSet() {
        addBottomDots(0)

        activityOnBoardingBinding.slider.apply {
            adapter = sliderAdapter
            sliderChangeListener?.let { addOnPageChangeListener(it) }
        }
    }

    private fun interactions() {
        activityOnBoardingBinding.skipBtn.setOnClickListener {
            navigateToLogin()
        }

        activityOnBoardingBinding.startBtn.setOnClickListener {
            navigateToLogin()
        }

        activityOnBoardingBinding.nextBtn.setOnClickListener {
            /**
             *  Checking for last page, if last page
             *  login screen will be launched
             * */
            val current = getCurrentScreen(+1)
            if (current < layouts.size) {
                /**
                 * Move to next screen
                 * */
                activityOnBoardingBinding.slider.currentItem = current
            } else {
                navigateToLogin()
            }
        }
    }

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(layouts.size)

        dotsLayout.removeAllViews()
        for (i in dots!!.indices) {
            dots!![i] = TextView(this)
            dots!![i]?.text = Html.fromHtml("&#8226;")
            dots!![i]?.textSize = 40f
            dots!![i]?.setTextColor(resources.getColor(R.color.colorGrey))
            dotsLayout.addView(dots!![i])
        }

        if (dots!!.isNotEmpty()) {
            dots!![currentPage]?.setTextColor(resources.getColor(R.color.colorPrimaryDark))
        }
    }

    private fun getCurrentScreen(i: Int): Int = activityOnBoardingBinding.slider.currentItem.plus(i)

    private fun navigateToLogin() {
        OnBoardingPref.setFirstLaunchApp(this, false)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun initSliderChangeListener() {
        sliderChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                addBottomDots(position)

                if (position == layouts.size.minus(1)) {
                    activityOnBoardingBinding.nextBtn.visibility = View.INVISIBLE
                    activityOnBoardingBinding.skipBtn.visibility = View.INVISIBLE
                    activityOnBoardingBinding.startBtn.visibility = View.VISIBLE
                } else {
                    activityOnBoardingBinding.nextBtn.visibility = View.VISIBLE
                    activityOnBoardingBinding.skipBtn.visibility = View.VISIBLE
                    activityOnBoardingBinding.startBtn.visibility = View.GONE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}

        }
    }

}