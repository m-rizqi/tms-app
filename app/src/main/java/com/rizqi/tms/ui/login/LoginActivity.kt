package com.rizqi.tms.ui.login

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rizqi.tms.R
import com.rizqi.tms.TMSPreferences.Companion.setAnonymous
import com.rizqi.tms.TMSPreferences.Companion.setLogin
import com.rizqi.tms.TMSPreferences.Companion.setUserId
import com.rizqi.tms.databinding.ActivityLoginBinding
import com.rizqi.tms.model.User
import com.rizqi.tms.ui.dashboard.DashboardActivity
import com.rizqi.tms.ui.dialog.warning.WarningDialog
import com.rizqi.tms.ui.register.RegisterActivity
import com.rizqi.tms.utility.Resource
import com.rizqi.tms.utility.hideLoading
import com.rizqi.tms.utility.showLoading
import com.rizqi.tms.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(){
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth
    private val viewModel : LoginViewModel by viewModels()
    private val userViewModel : UserViewModel by viewModels()
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 922
    private val TAG = this::class.java.name
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setContentView(binding.root)
        auth = Firebase.auth
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        binding.apply {
            tilLoginEmail.editText.doAfterTextChanged { viewModel.setEmail(it.toString()) }
            tilLoginPassword.editText.doAfterTextChanged { viewModel.setPassword(it.toString()) }
            tvLoginRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
            tvLoginSkip.setOnClickListener { showSkipAlertDialog() }
            btnLoginWithGoogle.root.setOnClickListener { startSignIn() }
            btnLogin.setOnClickListener {
                val validity = viewModel.validate()
                binding.apply {
                    tilLoginEmail.inputLayout.error = validity.emailError?.asString(this@LoginActivity)
                    tilLoginPassword.inputLayout.error = validity.passwordError?.asString(this@LoginActivity)
                }
                if (!validity.isValid) return@setOnClickListener
                showLoading(binding.lLoginLoading)
                val emailPassword = viewModel.getEmailPassword()
                auth.signInWithEmailAndPassword(emailPassword.first, emailPassword.second)
                    .addOnCompleteListener(this@LoginActivity){task ->
                        if (task.isSuccessful) {
                            auth.currentUser?.let { fUser ->
                                val user = User(
                                    firebaseId = fUser.uid, name = fUser.displayName ?: "", fUser.email ?: "", imageUrl = fUser.photoUrl.toString()
                                )
                                val insertObserver = Observer<Resource<Long>>{ res ->
                                    when(res){
                                        is Resource.Error -> {
                                            hideLoading(binding.lLoginLoading)
                                            Toast.makeText(this@LoginActivity, res.message?.asString(this@LoginActivity), Toast.LENGTH_SHORT).show()
                                            goToDashboard()
                                        }
                                        is Resource.Success -> {
                                            hideLoading(binding.lLoginLoading)
                                            res.data?.let { it1 -> setUserId(it1) }
                                            setLogin(true)
                                            setAnonymous(false)

                                            goToDashboard()
                                        }
                                    }
                                }
                                userViewModel.insertUser(user)
                                userViewModel.insertUser.observe(this@LoginActivity, insertObserver)
                            }
                        }else{
                            hideLoading(binding.lLoginLoading)
                            Toast.makeText(this@LoginActivity, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun showSkipAlertDialog(){
        WarningDialog(
            onPositiveClickListener = {
                setLogin(true)
                setAnonymous(true)
                goToDashboard()
            }
        ).show(supportFragmentManager, "SkipAlertDialog")
    }

    private fun goToDashboard(){
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun startSignIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this@LoginActivity){ result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0 )
                } catch (e : IntentSender.SendIntentException){
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this@LoginActivity){e ->
                Log.e(TAG, e.localizedMessage)
                Toast.makeText(this@LoginActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            showLoading(binding.lLoginLoading)
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        auth.currentUser?.let { fUser ->
                                            val newUser = User(
                                                firebaseId = fUser.uid, name = fUser.displayName ?: "", fUser.email ?: "", imageUrl = fUser.photoUrl.toString()
                                            )
                                            val insertObserver = Observer<Resource<Long>>{res ->
                                                when(res){
                                                    is Resource.Error -> {
                                                        hideLoading(binding.lLoginLoading)
                                                        Toast.makeText(this@LoginActivity, res.message?.asString(this@LoginActivity), Toast.LENGTH_SHORT).show()
                                                        goToDashboard()
                                                    }
                                                    is Resource.Success -> {
                                                        hideLoading(binding.lLoginLoading)
                                                        res.data?.let { it1 -> setUserId(it1) }
                                                        setLogin(true)
                                                        setAnonymous(false)
                                                        goToDashboard()
                                                    }
                                                }
                                            }
                                            userViewModel.insertUser(newUser)
                                            userViewModel.insertUser.observe(this@LoginActivity, insertObserver)
                                        }
                                    } else {
                                        hideLoading(binding.lLoginLoading)
                                        Toast.makeText(this@LoginActivity, task.exception?.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        else -> {
                            hideLoading(binding.lLoginLoading)
                            Toast.makeText(this, getString(R.string.no_id_token_passed), Toast.LENGTH_SHORT).show()
                        }
                    }
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
                            if (showOneTapUI) startSignIn()
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