package com.rizqi.tms.ui.createitem

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rizqi.tms.R
import com.rizqi.tms.domain.item.CheckItemNameAlreadyExistUseCase
import com.rizqi.tms.domain.item.CreateItemUseCase
import com.rizqi.tms.domain.item.ValidateStep1CreateItemUseCaseImpl
import com.rizqi.tms.shared.StringResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateItemViewModel @Inject constructor(
    private val createItemUseCase: CreateItemUseCase,
    private val checkItemNameAlreadyExistUseCase: CheckItemNameAlreadyExistUseCase
) : ViewModel(){

    private val validateStep1CreateItemUseCase = ValidateStep1CreateItemUseCaseImpl()

    private val _createItemUiState = MutableStateFlow(CreateItemUiState())
    val createItemUiState : StateFlow<CreateItemUiState> = _createItemUiState.asStateFlow()

    private val _step1UiState = MutableStateFlow(Step1UiState())
    val step1UiState : StateFlow<Step1UiState> = _step1UiState.asStateFlow()

    fun updateCurrentStep(nextStep : Int){
        _createItemUiState.update { it.copy(currentStep = nextStep, shouldChangeToNextStep = null) }
    }

    fun setItemImage(image : Bitmap?){
        _step1UiState.update { it.copy(itemImage = image) }
    }

    fun setItemName(name: String){
        _step1UiState.update { it.copy(itemName = name) }
        viewModelScope.launch {
            if (checkItemNameAlreadyExistUseCase(name)){
                _createItemUiState.update { it.copy(generalMessage = StringResource.StringResWithParams(R.string.item_name_already_exist)) }
            }
        }
    }

    fun step1RequestNextStep(){
        val resource = validateStep1CreateItemUseCase(step1UiState.value.itemName)
        _step1UiState.update {
            it.copy(itemNameErrorMessage = resource.exception?.values?.first())
        }
        _step1UiState.update { it.copy(requestNextStepSuccess = resource.isSuccess) }
        if (resource.isSuccess){
            _createItemUiState.update { it.copy(shouldChangeToNextStep = it.currentStep + 1) }
            _step1UiState.update { it.copy(requestNextStepSuccess = false) }
        }
    }

    fun reportGeneralMessageShown(){
        _createItemUiState.update {
            it.copy(generalMessage = null)
        }
    }

    data class CreateItemUiState(
        var currentStep : Int = 1,
        var shouldShowLoading : Boolean = false,
        var shouldChangeToNextStep : Int? = null,
        var generalMessage : StringResource? = null,
    )

    data class Step1UiState(
        var itemImage : Bitmap? = null,
        var itemName : String = "",
        var itemNameErrorMessage : StringResource? = null,
        var requestNextStepSuccess : Boolean = false
    )

}