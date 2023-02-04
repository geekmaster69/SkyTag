package com.example.skytag

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.skytag.base.database.UserInfoApplication
import com.example.skytag.model.LocationEvent
import com.example.skytag.presentation.location.data.database.dao.entities.UserInfoEntity
import com.google.android.gms.location.*
import org.greenrobot.eventbus.EventBus

class LocationService : Service() {

    companion object{
        const val CHANNEL_ID = "100"
        const val NOTIFICATION_ID = 200
    }

    private var fusedLocationProviderClient: FusedLocationProviderClient?=null
    private var locationCallback: LocationCallback?=null
    private var locationRequest: LocationRequest?=null

    private var notificationManager: NotificationManager?=null

    private var location: Location?=null

    override fun onCreate() {

        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000)
                .setIntervalMillis(10*60*1000).build()


        locationCallback = object : LocationCallback(){
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult)
                Thread{
                    UserInfoApplication.database.userInfoDao().addUserINfo(UserInfoEntity(latitud = location!!.latitude, longitud = location!!.longitude))
                }.start()

            }
        }
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, "location",NotificationManager.IMPORTANCE_HIGH)
        notificationManager?.createNotificationChannel(notificationChannel)

    }

    @SuppressLint("MissingPermission")
    fun createLocationRequest(){
        try {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest!!, locationCallback!!, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeLocationUpdates(){
        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
        stopForeground(true)
        stopSelf()
    }

    private fun onNewLocation(locationResult: LocationResult) {
        location = locationResult.lastLocation
        EventBus.getDefault().post(
            LocationEvent(
            latitude = location?.latitude,
            longitude = location?.longitude))
        startForeground(NOTIFICATION_ID, getNotification())
    }
    private fun getNotification(): Notification{
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText("Location Updates")
            .setContentText(
                "Latitud: ${location?.latitude}\n  Longitud: ${location?.longitude}")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
        notification.setChannelId(CHANNEL_ID)
        return notification.build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        createLocationRequest()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
      //  removeLocationUpdates()
    }
}