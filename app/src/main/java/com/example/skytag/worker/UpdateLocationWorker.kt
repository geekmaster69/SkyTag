package com.example.skytag.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skytag.base.database.UserInfoApplication
import com.example.skytag.model.UserInfo
import com.example.skytag.network.UserService
import com.example.skytag.presentation.location.data.database.dao.entities.UserInfoEntity
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

private lateinit var dateFormat: SimpleDateFormat

class UpdateLocationWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    private val userService = UserService()
    override suspend fun doWork(): Result {
        makeStatusNotification("update location", applicationContext)

        delay(10000)
        uploadLocation()

        return Result.success()

    }

    private suspend fun uploadLocation() {
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val mensaje = "reporte"
        val usuario = "tagkeyuser"
        val telefono = "5611750632"
        val tagKey = "FF:FF:40:01:2F:3F"
        val codigo = "1"
        val datail = "reporte de tag"
        val date = Date()
        val fecha = dateFormat.format(date)

        val location = UserInfoApplication.database.userInfoDao().getAllData()


       val response =  userService.updateUserInfo(UserInfo(
            mensaje = mensaje,
            fecha = fecha,
            usuario = usuario,
            telefono = telefono,
            tagkey = tagKey,
            codigo = codigo, detail = datail, longitud = location.longitud, latitud = location.latitud))

        UserInfoApplication.database.userInfoDao().deleteUserInfo()

        Log.w("Reponse", response.toString() )

    }
}