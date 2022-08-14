package com.rizqi.tms.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityRegisterBinding
import com.rizqi.tms.model.User
import com.rizqi.tms.ui.dashboard.DashboardActivity
import com.rizqi.tms.ui.dialog.skipalert.SkipAlertDialog
import com.rizqi.tms.utility.Resource
import com.rizqi.tms.utility.hideLoading
import com.rizqi.tms.utility.showLoading
import com.rizqi.tms.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel : RegisterViewModel by viewModels()
    private val userViewModel : UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        setContentView(binding.root)
        auth = Firebase.auth

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
                                            goToDashboard()
                                        }
                                        is Resource.Success -> {
                                            hideLoading(binding.lRegisterLoading)
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
        }
    }

    private fun showSkipAlertDialog(){
        SkipAlertDialog {
            goToDashboard()
        }.show(supportFragmentManager, "SkipAlertDialog")
    }

    private fun goToDashboard(){
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}