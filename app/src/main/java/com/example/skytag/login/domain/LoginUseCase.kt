package com.example.skytag.login.domain

import com.example.skytag.login.model.LoginResponse
import com.example.skytag.login.model.LoginUserInfo
import com.example.skytag.login.network.LoginUserService
import kotlin.math.log

class LoginUseCase {

    private val loginUserService = LoginUserService()

    suspend operator fun invoke(loginUserInfo: LoginUserInfo): LoginResponse {

        return loginUserService.login(loginUserInfo)
    }


}