package com.rizqi.tms.ui.register

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
import com.rizqi.tms.TMSPreferences.Companion.setFirebaseUserId
import com.rizqi.tms.TMSPreferences.Companion.setLogin
import com.rizqi.tms.TMSPreferences.Companion.setUserId
import com.rizqi.tms.databinding.ActivityRegisterBinding
import com.rizqi.tms.model.Unit
import com.rizqi.tms.model.User
import com.rizqi.tms.ui.dashboard.DashboardActivity
import com.rizqi.tms.ui.dialog.warning.WarningDialog
import com.rizqi.tms.utility.Resource
import com.rizqi.tms.utility.hideLoading
import com.rizqi.tms.utility.showLoading
import com.rizqi.tms.viewmodel.UnitViewModel
import com.rizqi.tms.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 922
    private val TAG = this::class.java.name
    private var showOneTapUI = true
    private val viewModel : RegisterViewModel by viewModels()
    private val userViewModel : UserViewModel by viewModels()
    private val unitViewModel : UnitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        setContentView(binding.root)

//        Firebase Initialize
        auth = Firebase.auth
        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        binding.apply {
            tvRegisterLogin.setOnClickListener { finish() }
            tvRegisterSkip.setOnClickListener { showSkipAlertDialog() }
            tilRegisterName.editText.doAfterTextChanged { viewModel.setName(it.toString()) }
            tilRegisterEmail.editText.doAfterTextChanged { viewModel.setEmail(it.toString()) }
            tilRegisterPassword.editText.doAfterTextChanged { viewModel.setPassword(it.toString()) }
            tilRegisterConfirmPassword.editText.doAfterTextChanged { viewModel.setConfirmPassword(it.toString()) }

            btnRegister.setOnClickListener {
                val validity = viewModel.validate(this@RegisterActivity)
                binding.apply {
                    tilRegisterName.inputLayout.error =  validity.nameError?.asString(this@RegisterActivity)
                    tilRegisterEmail.inputLayout.error =  validity.emailError?.asString(this@RegisterActivity)
                    tilRegisterPassword.inputLayout.error =  validity.passwordError?.asString(this@RegisterActivity)
                    tilRegisterConfirmPassword.inputLayout.error =  validity.confirmPasswordError?.asString(this@RegisterActivity)
                }
                if (!validity.isValid) return@setOnClickListener
                showLoading(binding.lRegisterLoading)
                val newUser = viewModel.getUser()
                auth.createUserWithEmailAndPassword(newUser.email, newUser.password)
                    .addOnCompleteListener(this@RegisterActivity) { task ->
                        if (task.isSuccessful){
                            auth.currentUser?.let { fUser ->
                                newUser.firebaseId = fUser.uid
                                val insertObserver = Observer<Resource<Long>>{res ->
                                    when(res){
                                        is Resource.Error -> {
                                            hideLoading(binding.lRegisterLoading)
                                            Toast.makeText(this@RegisterActivity, res.message?.asString(this@RegisterActivity), Toast.LENGTH_SHORT).show()
                                        }
                                        is Resource.Success -> {
                                            res.data?.let { it1 -> setUserId(it1) }
                                            setLogin(true)
                                            setAnonymous(false)
                                            setFirebaseUserId(fUser.uid)
                                            // Create initial unit
                                            val insertUnitObserver = Observer<Resource<Long>>{unitRes ->
                                                when(unitRes){
                                                    is Resource.Error -> {
                                                        hideLoading(binding.lRegisterLoading)
                                                        Toast.makeText(this@RegisterActivity, res.message?.asString(this@RegisterActivity), Toast.LENGTH_SHORT).show()
                                                        goToDashboard()
                                                    }
                                                    is Resource.Success -> {
                                                        hideLoading(binding.lRegisterLoading)
                                                        goToDashboard()
                                                    }
                                                }
                                            }
                                            unitViewModel.insertUnit(Unit(getString(R.string.wrap)))
                                            unitViewModel.insertUnit.observe(this@RegisterActivity, insertUnitObserver)
                                        }
                                    }
                                }
                                userViewModel.insertUser(newUser)
                                userViewModel.insertUser.observe(this@RegisterActivity, insertObserver)
                            }
                        } else {
                            hideLoading(binding.lRegisterLoading)
                            Toast.makeText(this@RegisterActivity, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            btnRegisterWithGoogle.root.setOnClickListener {
                startSignUp()
            }
        }
    }

    private fun startSignUp() {
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(this@RegisterActivity){ result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0 )
                } catch (e : IntentSender.SendIntentException){
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this@RegisterActivity){e ->
                Log.e(TAG, e.localizedMessage)
                Toast.makeText(this@RegisterActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            showLoading(binding.lRegisterLoading)
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
                                                        hideLoading(binding.lRegisterLoading)
                                                        Toast.makeText(this@RegisterActivity, res.message?.asString(this@RegisterActivity), Toast.LENGTH_SHORT).show()
                                                    }
                                                    is Resource.Success -> {
                                                        res.data?.let { it1 -> setUserId(it1) }
                                                        setLogin(true)
                                                        setAnonymous(false)
                                                        setFirebaseUserId(fUser.uid)
                                                        // Create initial unit
                                                        val insertUnitObserver = Observer<Resource<Long>>{unitRes ->
                                                            when(unitRes){
                                                                is Resource.Error -> {
                                                                    hideLoading(binding.lRegisterLoading)
                                                                    Toast.makeText(this@RegisterActivity, res.message?.asString(this@RegisterActivity), Toast.LENGTH_SHORT).show()
                                                                    goToDashboard()
                                                                }
                                                                is Resource.Success -> {
                                                                    hideLoading(binding.lRegisterLoading)
                                                                    goToDashboard()
                                                                }
                                                            }
                                                        }
                                                        unitViewModel.insertUnit(Unit(getString(R.string.wrap)))
                                                        unitViewModel.insertUnit.observe(this@RegisterActivity, insertUnitObserver)
                                                        goToDashboard()
                                                    }
                                                }
                                            }
                                            userViewModel.insertUser(newUser)
                                            userViewModel.insertUser.observe(this@RegisterActivity, insertObserver)
                                        }
                                    } else {
                                        hideLoading(binding.lRegisterLoading)
                                        Toast.makeText(this@RegisterActivity, task.exception?.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        else -> {
                            hideLoading(binding.lRegisterLoading)
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
                            if (showOneTapUI) startSignUp()
                        }
                        else -> {
                            Log.d(TAG, "Couldn't get credential from result." +
                                    " (${e.localizedMessage})")
                            Toast.makeText(this@RegisterActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}