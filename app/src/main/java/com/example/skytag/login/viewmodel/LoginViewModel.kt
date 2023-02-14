package com.example.skytag.login.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skytag.login.domain.LoginUseCase
import com.example.skytag.login.model.LoginResponse
import com.example.skytag.login.model.LoginUserInfo
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    val loginUseCase = LoginUseCase()

    var loginModel = MutableLiveData<LoginResponse>()

    fun onLogin(loginUserInfo: LoginUserInfo){
        viewModelScope.launch {
            val result = loginUseCase(loginUserInfo)
            loginModel.postValue(result)
        }
    }
}