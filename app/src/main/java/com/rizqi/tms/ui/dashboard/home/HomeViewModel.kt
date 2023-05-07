package com.rizqi.tms.ui.dashboard.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState : StateFlow<HomeUiState> = _uiState.asStateFlow()

    private fun updateUiState(update : (HomeUiState) -> HomeUiState){
        _uiState.update { currentUiState ->
            update(currentUiState)
        }
    }

    data class Item(
        val id : Long,
        val name : String,
        val merchantMainPrice : String,
        val consumerMainPrice : String,
        val image : Bitmap?,
        val onClickListener: (Long) -> Unit
    )

    data class HomeUiState(
        var popularItems : List<Item> = emptyList(),
        var nonBarcodeItems : List<Item> = emptyList()
    )
}