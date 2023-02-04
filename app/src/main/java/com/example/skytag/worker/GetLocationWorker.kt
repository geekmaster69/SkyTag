package com.example.skytag.worker

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.skytag.LocationService
import com.example.skytag.base.database.UserInfoApplication
import com.example.skytag.model.LocationEvent
import com.example.skytag.presentation.location.data.database.dao.entities.UserInfoEntity
import com.google.android.gms.location.*
import org.greenrobot.eventbus.EventBus


class GetLocationWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {

        makeStatusNotification("Location", applicationContext)


        val location: Location?=null
        val fusedLocationProviderClient: FusedLocationProviderClient?
        val locationCallback: LocationCallback


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        val locationRequest: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000)
                .setIntervalMillis(15*60*1000)
                .build()

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                    UserInfoApplication.database.userInfoDao().addUserINfo(UserInfoEntity(
                        latitud = locationResult.lastLocation!!.latitude,
                        longitud = locationResult.lastLocation!!.longitude))
                Log.e("Location", "${location!!.latitude} \n ${location!!.longitude} ")

            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, null)


        return Result.success()
    }

}