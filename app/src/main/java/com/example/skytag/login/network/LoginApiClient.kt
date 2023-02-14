package com.example.skytag.login.network

import com.example.skytag.base.Constans
import com.example.skytag.login.model.LoginResponse
import com.example.skytag.login.model.LoginUserInfo
import com.example.skytag.model.UserInfo
import com.example.skytag.model.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiClient {
    @POST(Constans.TAG_KEY_SERVICE_PATH)
    suspend fun onLogin(@Body data: LoginUserInfo) : LoginResponse
}
