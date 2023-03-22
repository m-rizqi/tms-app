package com.rizqi.tms.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.rizqi.tms.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnBoardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnBoardingUiState())
    val uiState : StateFlow<OnBoardingUiState> = _uiState.asStateFlow()

    fun onPageSelected(position : Int){
        _uiState.update { currentUiState ->
            currentUiState.copy(fragmentIndex = position)
        }
    }

    fun nextPage() {
        if (_uiState.value.fragmentIndex == 2){
            _uiState.update { currentUiState ->
                currentUiState.copy(shouldStartToLoginActivity = true)
            }
        }else{
            _uiState.update { currentUiState ->
                currentUiState.copy(fragmentIndex = currentUiState.fragmentIndex + 1)
            }
        }
    }

    data class OnBoardingUiState(
        val fragmentIndex : Int = 0,
        val shouldStartToLoginActivity : Boolean = false,
        val onBoardingContents : List<OnBoardingContent> = listOf(
            OnBoardingContent(
                R.string.scan_and_save_item,
                R.string.scan_save_see_items_in_your_merchant,
                R.drawable.vector_onboarding1
            ),
            OnBoardingContent(
                R.string.easy_cashier_feature,
                R.string.scan_to_speed_up_merchant_performance,
                R.drawable.vector_onboarding2
            ),
            OnBoardingContent(
                R.string.see_financial_progress,
                R.string.income_resume_to_see_merchant_progress,
                R.drawable.vector_onboarding3
            ),
        )
    )

    data class OnBoardingContent(
        @StringRes
        val titleResId : Int,
        @StringRes
        val descriptionResId : Int,
        @DrawableRes
        val imageResId : Int
    )

}