package com.example.skytag.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.skytag.R
import com.example.skytag.base.database.UserInfoApplication
import com.example.skytag.presentation.location.data.database.dao.entities.UserInfoEntity
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_STAR -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        locationClient.getLocationClient(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->

                    UserInfoApplication.database.userInfoDao()
                        .addUserINfo(UserInfoEntity(
                            latitud = location.latitude, longitud = location.longitude))


            }
            .launchIn(serviceScope)
    }
    private fun stop(){
        stopForeground(true)
        stopSelf()

    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }


    companion object{
        const val ACTION_STAR = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}