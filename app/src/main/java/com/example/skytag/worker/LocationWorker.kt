package com.example.skytag.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skytag.base.database.UserInfoApplication
import com.example.skytag.presentation.location.data.database.dao.entities.UserInfoEntity
import com.example.skytag.service.DefaultLocationClient
import com.example.skytag.service.LocationClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
private lateinit var locationClient: LocationClient
class LocationWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {

        makeStatusNotification("Get Location", applicationContext)



        starLocation()
        return Result.success()


    }

    private fun starLocation() {

        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext))

        locationClient.getLocationClient(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->

                UserInfoApplication.database.userInfoDao()
                    .addUserINfo(
                        UserInfoEntity(
                        latitud = location.latitude, longitud = location.longitude))

                UserInfoApplication.database.userInfoDao()
                    .addUserINfo(UserInfoEntity(
                        latitud = location.latitude, longitud = location.longitude))

                Log.w("LocationWorkManager", "${location.latitude} ${location.longitude}")

            }
            .launchIn(serviceScope)

    }
}