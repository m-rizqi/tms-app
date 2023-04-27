package com.rizqi.tms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rizqi.tms.data.datasource.firebase.auth.FirebaseAuthentication
import com.rizqi.tms.ui.dashboard.DashboardActivity
import com.rizqi.tms.ui.onboarding.OnBoardingActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuthentication: FirebaseAuthentication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(firebaseAuthentication.isLogin()){
            startActivity(
                Intent(this, DashboardActivity::class.java)
            )
            return
        }
        startActivity(Intent(this, OnBoardingActivity::class.java))

    }
}