package com.rizqi.tms.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityLoginBinding
import com.rizqi.tms.ui.dashboard.DashboardActivity
import com.rizqi.tms.ui.dialog.SkipWarningDialog
import com.rizqi.tms.ui.hideLoading
import com.rizqi.tms.ui.showLoading
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private val viewModel : LoginViewModel by viewModels()
    private val REQ_ONE_TAP = 922
    private var showOneTapUI = true
    private val TAG = this::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setContentView(binding.root)

        lifecycleScope.launchWhenCreated {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {uiState ->

                    binding.tilLoginEmail.errorText = uiState.emailErrorMessage?.asString(this@LoginActivity)
                    binding.tilLoginPassword.errorText = uiState.passwordErrorMessage?.asString(this@LoginActivity)

                    uiState.generalErrorMessage?.let { stringResource ->
                        Toast.makeText(this@LoginActivity, stringResource.asString(this@LoginActivity), Toast.LENGTH_SHORT).show()
                        viewModel.setGeneralErrorMessageDisplayed()
                    }

                    uiState.isDisplayingLoading.also {
                        if (it) showLoading(binding.lLoginLoading)
                        else hideLoading(binding.lLoginLoading)
                    }

                    uiState.shouldStartDashboardActivity.also {
                        if (it) {
                            startActivity(
                                Intent(this@LoginActivity, DashboardActivity::class.java)
                            )
                            viewModel.setDashboardActivityStarted()
                        }
                    }

                    uiState.shouldStartIntentSender.also {
                        if (it){
                            viewModel.getIntentSender()?.let { intentSender ->
                                startIntentSenderForResult(intentSender, REQ_ONE_TAP, null, 0, 0, 0)
                            }
                        }
                    }

                }
            }
        }
        binding.apply {
            tilLoginEmail.etInputLayoutDefault.doAfterTextChanged { viewModel.setEmail(it.toString()) }
            tilLoginPassword.etInputLayoutDefault.doAfterTextChanged { viewModel.setPassword(it.toString()) }
            btnLogin.setOnClickListener { viewModel.loginWithEmailAndPassword() }
            tvLoginSkip.setOnClickListener { showSkipWarningDialog() }
            btnLoginWithGoogle.root.setOnClickListener { viewModel.loginWithGoogle() }
        }
    }

    private fun showSkipWarningDialog(){
        SkipWarningDialog {
            viewModel.skip()
        }.show(supportFragmentManager, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQ_ONE_TAP -> {
                try {
                    viewModel.signInWithCredential(data)
                }catch (e : ApiException){
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(TAG, "One-tap encountered a network error.")
                            // Try again or just ignore.
                            if (showOneTapUI) viewModel.loginWithGoogle()
                        }
                        else -> {
                            Log.d(TAG, "Couldn't get credential from result." +
                                    " (${e.localizedMessage})")
                            Toast.makeText(this@LoginActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}