package com.example.meeting_app.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.meeting_app.R
import com.example.meeting_app.ui.login.LoginActivity
import com.example.meeting_app.ui.onBoarding.OnBoardingActivity
import com.example.meeting_app.utils.pref.OnBoardingPref

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        Handler(mainLooper).postDelayed({
            if(OnBoardingPref.getFirstLaunchApp(this)) {
                startActivity(Intent(this, OnBoardingActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 2000)
    }
}