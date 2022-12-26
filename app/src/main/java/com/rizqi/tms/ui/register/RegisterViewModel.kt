package com.rizqi.tms.ui.register

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rizqi.tms.R
import com.rizqi.tms.model.User
import com.rizqi.tms.utility.Message
import com.rizqi.tms.utility.Validator

class RegisterViewModel : ViewModel() {
    private var _name = MutableLiveData("")
    private var _email = MutableLiveData("")
    private var _password = MutableLiveData("")
    private var _confirmPassword = MutableLiveData("")

    fun setName(value : String){
        _name.value = value
    }
    fun setEmail(value : String){
        _email.value = value
    }
    fun setPassword(value : String){
        _password.value = value
    }
    fun setConfirmPassword(value : String){
        _confirmPassword.value = value
    }
    fun validate(context: Context): RegisterValidation{
        return RegisterValidation(
            if (_name.value.isNullOrBlank())
                Message.StringResource(R.string.field_must_be_filled, context.getString(R.string.name)) else null,
            if (!Validator.validateEmail(_email.value!!))
                Message.StringResource(R.string.email_not_valid) else null,
            if (!Validator.validatePassword(_password.value!!))
                Message.StringResource(R.string.password_helper) else null,
            if (!Validator.validateConfirmPassword(_password.value!!, _confirmPassword.value!!))
                Message.StringResource(R.string.confirm_password_must_equal) else null,
        )
    }
    fun getUser() : User{
        return User(
            "", _name.value!!, _email.value!!, _password.value!!
        )
    }

    inner class RegisterValidation(
        val nameError : Message?,
        val emailError : Message?,
        val passwordError : Message?,
        val confirmPasswordError : Message?
    ){
        val isValid : Boolean = Validator.validityAllNull(
            nameError, emailError, passwordError, confirmPasswordError
        )
    }
}