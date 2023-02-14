package com.example.skytag.login.network

import com.example.skytag.base.BaseRetrofit
import com.example.skytag.login.model.LoginResponse
import com.example.skytag.login.model.LoginUserInfo
import com.example.skytag.model.UserInfo
import com.example.skytag.model.UserInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginUserService {
    private val retrofit = BaseRetrofit.getRetrofit()

    suspend fun login(loginUserInfo: LoginUserInfo) :LoginResponse{
        return withContext(Dispatchers.IO){
            val response = retrofit.create(LoginApiClient::class.java).onLogin(loginUserInfo)
            response
        }
    }
}