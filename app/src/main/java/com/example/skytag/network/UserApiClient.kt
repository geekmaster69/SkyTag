package com.example.skytag.network

import com.example.skytag.base.Constans
import com.example.skytag.model.UserInfo
import com.example.skytag.model.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiClient {
    @POST(Constans.TAG_KEY_SERVICE_PATH)
    suspend fun sendInfoSos(@Body data: UserInfo) : UserInfoResponse
}