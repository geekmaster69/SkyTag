package com.example.skytag

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.work.*
import com.example.skytag.databinding.ActivityMainBinding
import com.example.skytag.model.LocationEvent
import com.example.skytag.service.LocationService
import com.example.skytag.worker.UpdateLocationWorker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var service: Intent?=null
    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){ }
    }
    private val locationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        when{
            it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) ->{
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                    if (ActivityCompat.checkSelfPermission(
                            this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED){
                        backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    }
                }
            }
            it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) ->{
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    //    service = Intent(this, LocationService::class.java)

        binding.btnStar.setOnClickListener {
            checkPermissions()
        }

        binding.btnStop.setOnClickListener {
            stopService(service)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    fun checkPermissions(){
        if(ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ){
            locationPermissions.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION))
        }else{
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STAR
                startService(this)
            }
            myPeriodicWork()

        }
    }
    private fun myPeriodicWork() {
        val constraints = Constraints.Builder()
            .build()

        val myWorkRequest = PeriodicWorkRequest.Builder(
            UpdateLocationWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setConstraints(constraints)
            .addTag("my_id")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("my_id", ExistingPeriodicWorkPolicy.KEEP, myWorkRequest)

    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(service)
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }
    @Subscribe
    fun receiverLocationEvent(locationEvent: LocationEvent){
        binding.tvLatitud.text = "Latitud: ${locationEvent.latitude}"
        binding.tvLongitude.text = "Longitud: ${locationEvent.longitude}"
    }
}

