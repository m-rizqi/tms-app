package com.rizqi.tms.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityLoginBinding
import com.rizqi.tms.ui.dashboard.DashboardActivity
import com.rizqi.tms.ui.dialog.skipalert.SkipAlertDialog
import com.rizqi.tms.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity(){
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.apply {
            tvLoginRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
            tvLoginSkip.setOnClickListener { showSkipAlertDialog() }
        }
    }

    private fun showSkipAlertDialog(){
        SkipAlertDialog {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }.show(supportFragmentManager, "SkipAlertDialog")
    }
}