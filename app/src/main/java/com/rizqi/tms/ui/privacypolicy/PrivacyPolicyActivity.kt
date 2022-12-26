package com.rizqi.tms.ui.privacypolicy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rizqi.tms.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnPrivacyPolicyBack.setOnClickListener { onBackPressed() }
            webviewPrivacyPolicy.loadUrl("https://www.freeprivacypolicy.com/live/d8e72927-f2a2-4198-959e-cfc62b5129dd")
            webviewPrivacyPolicy.settings.javaScriptEnabled = true
        }
    }

}