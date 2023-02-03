package com.example.skytag.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skytag.model.LocationEvent
import com.example.skytag.network.UserService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class UpdateLocationWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        uploadLocation()


        return Result.success()

    }

    private fun uploadLocation() {

        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)


            @Subscribe
            fun receiverLocationEvent(locationEvent: LocationEvent){
                binding.tvLatitud.text = "Latitud: ${locationEvent.latitude}"
                binding.tvLongitude.text = "Longitud: ${locationEvent.longitude}"
            }

        val userService: UserService

        userService.updateUserInfo()


    }
}