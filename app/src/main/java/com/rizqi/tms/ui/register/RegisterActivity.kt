package com.rizqi.tms.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private var _binding : ActivityRegisterBinding? = null
    private val binding : ActivityRegisterBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        setContentView(binding.root)

        binding.apply {
            tvRegisterLogin.setOnClickListener { finish() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}