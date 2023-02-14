package com.example.skytag.service

import com.polidea.rxandroidble3.RxBleDevice

/**
 * BLE View
 */
interface BLEView {
    fun onConnected(bleDevice: RxBleDevice)
    fun onKeyPressed()
    fun onError(throwable: Throwable)

}