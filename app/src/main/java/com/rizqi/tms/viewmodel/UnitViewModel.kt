package com.rizqi.tms.viewmodel

import androidx.lifecycle.*
import com.rizqi.tms.model.Unit
import com.rizqi.tms.repository.UnitRepository
import com.rizqi.tms.utility.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnitViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel(){
    private var _singleUnit = MutableLiveData<Resource<Unit>>()
    val singleUnit : LiveData<Resource<Unit>> = _singleUnit

    private var _insertUnit = MutableLiveData<Resource<Long>>()
    val insertUnit : LiveData<Resource<Long>> = _insertUnit

    private var _listUnit = MutableLiveData<Resource<List<Unit>>>()
    val listUnit : LiveData<Resource<List<Unit>>> = _listUnit

    fun getUnitById(id : Long){
        _singleUnit.value = Resource.Loading()
        viewModelScope.launch {
            unitRepository.getById(id).collect{
                _singleUnit.value = Resource.Success(it)
            }
        }
    }

    fun insertUnit(Unit: Unit){
        _insertUnit.value = Resource.Loading()
        viewModelScope.launch {
            _insertUnit.value = Resource.Success(
                unitRepository.insert(Unit)
            )
        }
    }

    fun getListUnit(){
        _listUnit.value = Resource.Loading()
        viewModelScope.launch {
            unitRepository.getAll().collect{
                _listUnit.value = Resource.Success(it)
            }
        }
    }

}