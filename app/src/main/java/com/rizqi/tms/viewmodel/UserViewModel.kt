package com.rizqi.tms.viewmodel

import androidx.lifecycle.*
import com.rizqi.tms.model.User
import com.rizqi.tms.repository.UserRepository
import com.rizqi.tms.utility.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(){
    private var _singleUser = MutableLiveData<Resource<User>>()
    val singleUser : LiveData<Resource<User>> = _singleUser

    private var _insertUser = MutableLiveData<Resource<Long>>()
    val insertUser : LiveData<Resource<Long>> = _insertUser

//    fun getUserById(id : Long){
//        _singleUser.value = Resource.Loading()
//        viewModelScope.launch {
//            userRepository.getUser(id).collect{
//                _singleUser.value = Resource.Success(it)
//            }
//        }
//    }

    fun getUserById(id : Long): LiveData<User> {
        return userRepository.getUser(id).asLiveData()
    }

    fun insertUser(user: User){
        _insertUser.value = Resource.Loading()
        viewModelScope.launch {
            _insertUser.value = Resource.Success(
                userRepository.insertUser(user)
            )
        }
    }
}