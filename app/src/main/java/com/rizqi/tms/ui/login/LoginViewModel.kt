package com.rizqi.tms.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.R
import com.rizqi.tms.utility.StringResource
import com.rizqi.tms.utility.Validator

class LoginViewModel : ViewModel() {
    private var _email = MutableLiveData("")
    private var _password = MutableLiveData("")

    fun setEmail(value : String){
        _email.value = value
    }

    fun setPassword(value : String){
        _password.value = value
    }

    fun validate() : LoginValidation {
        return LoginValidation(
            if (Validator.validateEmail(_email.value!!)) null else StringResource.StringResWithParams(R.string.email_not_valid),
            if (Validator.validatePassword(_password.value!!)) null else StringResource.StringResWithParams(R.string.password_helper)
        )
    }

    fun getEmailPassword() = Pair(_email.value!!, _password.value!!)

    inner class LoginValidation(
        val emailError : StringResource?,
        val passwordError : StringResource?
    ){
        val isValid = Validator.validityAllNull(emailError, passwordError)
    }
}