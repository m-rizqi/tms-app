package com.rizqi.tms.ui.login

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqi.tms.data.datasource.firebase.auth.FirebaseAuthentication
import com.rizqi.tms.data.datasource.firebase.auth.MainFirebaseAuthentication
import com.rizqi.tms.domain.KeyValue
import com.rizqi.tms.domain.login.SignInWithEmailAndPasswordUseCase
import com.rizqi.tms.shared.StringResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    private val firebaseAuthentication: FirebaseAuthentication
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState : StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun setEmail(email : String){
        updateUiState { it.copy(email = it.email.copy(value = email), emailErrorMessage = null) }
    }

    fun setPassword(password: String){
        updateUiState { it.copy(password = it.password.copy(value = password)) }
    }

    fun setGeneralErrorMessageDisplayed(){
        updateUiState { it.copy(generalErrorMessage = null) }
    }

    fun setDashboardActivityStarted() {
        updateUiState { it.copy(shouldStartDashboardActivity = false) }
    }

    fun login(){
        updateUiState { it.copy(isDisplayingLoading = true) }
        viewModelScope.launch {
            val resource = signInWithEmailAndPasswordUseCase.invoke(uiState.value.email, uiState.value.password)
            if (resource.isSuccess){
                updateUiState { it.copy(shouldStartDashboardActivity = true) }
            }else{
                updateUiState {
                    it.copy(
                        emailErrorMessage = resource.exception?.get(it.email.key),
                        passwordErrorMessage = resource.exception?.get(it.password.key),
                        generalErrorMessage = resource.exception?.get(null)
                    )
                }
            }
            updateUiState { it.copy(isDisplayingLoading = false) }
        }
    }

    private fun updateUiState(update : (LoginUiState) -> LoginUiState){
        _uiState.update { currentUiState ->
            update(currentUiState)
        }
    }

    fun skip() {
        updateUiState { it.copy(isDisplayingLoading = true) }
        viewModelScope.launch {
            val resource = firebaseAuthentication.signInAnonymously()
            if (resource.isSuccess){
                updateUiState { it.copy(shouldStartDashboardActivity = true) }
            } else {
                updateUiState {
                    it.copy(
                        generalErrorMessage = resource.message
                    )
                }
            }
            updateUiState { it.copy(isDisplayingLoading = false) }
        }
    }

    fun loginWithGoogle() {
        updateUiState { it.copy(isDisplayingLoading = true) }
        viewModelScope.launch {
            val resource = firebaseAuthentication.beginSignInRequest()
            if (resource.isSuccess){
                updateUiState {
                    it.copy(
                        signInGoogleIntentSender = resource.intentSender,
                        shouldStartIntentSender = true
                    )
                }
            } else {
                updateUiState {
                    it.copy(
                        generalErrorMessage = resource.message
                    )
                }
            }
            updateUiState { it.copy(isDisplayingLoading = false) }
        }
    }

    fun getIntentSender() : IntentSender? {
        updateUiState { it.copy(shouldStartIntentSender = false) }
        return uiState.value.signInGoogleIntentSender
    }

    fun signInWithCredential(data: Intent?) {
        updateUiState { it.copy(isDisplayingLoading = true) }
        viewModelScope.launch {
            val resource = firebaseAuthentication.signInWithCredential(data)
            if (resource.isSuccess){
                updateUiState {
                    it.copy(
                        shouldStartDashboardActivity = true
                    )
                }
            } else {
                updateUiState {
                    it.copy(
                        generalErrorMessage = resource.message
                    )
                }
            }
            updateUiState { it.copy(isDisplayingLoading = false) }
        }
    }

    data class LoginUiState(
        var email: KeyValue = KeyValue("email", ""),
        var password: KeyValue = KeyValue("password", ""),
        var shouldStartRegisterActivity: Boolean = false,
        var emailErrorMessage : StringResource? = null,
        var passwordErrorMessage : StringResource? = null,
        var generalErrorMessage : StringResource? = null,
        var isDisplayingLoading : Boolean = false,
        var shouldStartDashboardActivity : Boolean = false,
        var signInGoogleIntentSender : IntentSender? = null,
        var shouldStartIntentSender : Boolean = false
    )
}