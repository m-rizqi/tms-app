package com.rizqi.tms.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityLoginBinding
import com.rizqi.tms.ui.dialog.skipalert.SkipAlertDialog
import com.rizqi.tms.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity(){
    private var _binding : ActivityLoginBinding? = null
    private val binding : ActivityLoginBinding
        get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setContentView(binding.root)

        binding.apply {
            tvLoginRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
            tvLoginSkip.setOnClickListener { showSkipAlertDialog() }
        }
    }

    private fun showSkipAlertDialog(){
        SkipAlertDialog {

        }.show(supportFragmentManager, "SkipAlertDialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}