package com.rizqi.tms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rizqi.tms.TMSPreferences.Companion.isLogin
import com.rizqi.tms.ui.dashboard.DashboardActivity
import com.rizqi.tms.ui.login.LoginActivity
import com.rizqi.tms.ui.onboarding.OnBoardingActivity
import com.rizqi.tms.ui.register.RegisterActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (isLogin()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            return
        }
        startActivity(Intent(this, LoginActivity::class.java))

    }
}