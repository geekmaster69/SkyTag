package com.example.skytag.service

import android.app.Service
import android.content.Intent
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import com.example.skytag.base.database.UserInfoApplication
import com.example.skytag.presentation.location.data.database.dao.entities.UserInfoEntity
import com.google.android.gms.location.LocationServices
import com.polidea.rxandroidble3.NotificationSetupMode
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*

class GpsBTService : Service() {
    private val TAG: String = GpsBTService::class.java.simpleName

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private lateinit var rxBleClient: RxBleClient
    private lateinit var locManager: LocationManager
    private lateinit var dateFormat: SimpleDateFormat
    private var view: BLEView? = null
    private lateinit var deviceMac: String
    private val serviceUUID: ParcelUuid = ParcelUuid.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    private val characteristicUUID: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    private val serviceBinder = ServiceBinder()
    private lateinit var lat: String
    private lateinit var long: String
    //Localizacion
    private val servicesScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)




    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        rxBleClient = RxBleClient.create(applicationContext)
        locManager = getSystemService(LOCATION_SERVICE) as LocationManager
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_STAR -> scanBt()
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

                Log.w("Locatin", "${location.latitude} ${location.longitude}")
            }
            .launchIn(serviceScope)
    }

    private fun scanBt(){
        rxBleClient.scanBleDevices(scanSettings(), scanFilter())
            .firstElement()
            .subscribe(
                { scanResult ->
                    connect(scanResult.bleDevice)
                }, onError())

    }

    private fun connect(bleDevice: RxBleDevice){
        view?.onConnected(bleDevice)
        bleDevice.establishConnection(false)
            .subscribe({rxBleConnection ->
                rxBleConnection.setupIndication(characteristicUUID, NotificationSetupMode.COMPAT)
                    .subscribe({ observable ->
                        observable.subscribe({
                            view?.onKeyPressed()
                            pressKey()
                        },onError())
                    },onError())
            },onError())

    }

    private fun pressKey() {
        view?.onKeyPressed()
        start()


    }

    private fun scanSettings(): ScanSettings =
        ScanSettings.Builder()
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

    private fun scanFilter(): ScanFilter =
        ScanFilter.Builder()
            .setServiceUuid(serviceUUID)
            .build()

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
    inner class ServiceBinder : Binder() {
        internal val service: GpsBTService
            get() = this@GpsBTService
    }
    private fun onError(): (Throwable) -> Unit {
        return { throwable ->
            throwable.message?.let { Log.e(TAG, it) }
            view?.onError(throwable)
        }
    }
}