package com.rizqi.tms.utility

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rizqi.tms.R

interface BluetoothBehavior {
    fun setup(context: Context, activity: Activity)
    fun isBluetoothEnabled(toastType : ToastType) : Boolean
    fun discoverDevices()
    fun refreshPairedDevices()
    fun checkRequestPermission(onGranted: () -> Unit)
    fun toggleBluetoothOnOff()
    fun onLifecycleStateDestroy()
    fun subscribeBluetoothStatus(lifecycleOwner: LifecycleOwner, observer: Observer<Boolean>)
    fun subscribePairedDevices(lifecycleOwner: LifecycleOwner, observer: Observer<MutableSet<BluetoothDevice>>)
    fun subscribeDiscoveredDevices(lifecycleOwner: LifecycleOwner, observer: Observer<MutableList<BluetoothDevice>>)
    fun onLifecycleStateResume()
    fun pairDevice(bluetoothDevice: BluetoothDevice)
}

enum class ToastType {
    BLUETOOTH_NOT_ENABLE,
    GPS_NOT_ENABLE,
    DEVICE_NOT_SUPPORTED
}

class BluetoothBehaviorImpl() : BluetoothBehavior {
    private lateinit var context : Context
    private lateinit var activity : Activity
    private var bluetoothManager : BluetoothManager? = null
    private var bluetoothAdapter : BluetoothAdapter? = null
    private var toast : Toast? = null
    private var lastToast  = 0L
    private var lastToastType  = ToastType.BLUETOOTH_NOT_ENABLE
    private val TOAST_DURATION = 1500
    private val _isBluetoothOn = MutableLiveData(false)
    private val isBluetoothOn : LiveData<Boolean>
        get() = _isBluetoothOn
    private val _discoveredDevices = MutableLiveData(mutableListOf<BluetoothDevice>())
    private val discoveredDevices : LiveData<MutableList<BluetoothDevice>>
        get() = _discoveredDevices
    private val _pairedDevices = MutableLiveData<MutableSet<BluetoothDevice>>(mutableSetOf())
    private val pairedDevices : LiveData<MutableSet<BluetoothDevice>>
        get() = _pairedDevices

    private val discoverReceiver = object : BroadcastReceiver(){
        @SuppressLint("MissingPermission")
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action ?: return
            when(action){
                BluetoothDevice.ACTION_FOUND -> {
                    val device : BluetoothDevice = p1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if (_discoveredDevices.value?.contains(device) == false){
                        _discoveredDevices.value?.add(device)
                    }
                    _discoveredDevices.notifyObserver()
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device : BluetoothDevice = p1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    if (device.bondState == BluetoothDevice.BOND_BONDED){
                        _discoveredDevices.value?.removeIf { it.address == device.address }
                        _discoveredDevices.notifyObserver()
                        refreshPairedDevices()
                    }
                }
            }
        }

    }

    override fun setup(context: Context, activity: Activity) {
        this.context = context
        this.activity = activity
        bluetoothManager = activity.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter
        checkRequestPermission {
            if (bluetoothAdapter == null){
                _isBluetoothOn.value = false
                showToast(context, ToastType.DEVICE_NOT_SUPPORTED)
            }else{
                _isBluetoothOn.value = bluetoothAdapter?.isEnabled
            }
            val bluetoothFoundFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            activity.registerReceiver(discoverReceiver, bluetoothFoundFilter)
            val bluetoothBondFilter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            activity.registerReceiver(discoverReceiver, bluetoothBondFilter)
            discoverDevices()
            refreshPairedDevices()
        }
    }

    override fun isBluetoothEnabled(toastType: ToastType): Boolean {
        if (bluetoothAdapter?.isEnabled == false){
            _isBluetoothOn.value = false
            showToast(context, toastType)
            return false
        }
        _isBluetoothOn.value = true
        return true
    }

    override fun discoverDevices() {
        if (!isBluetoothEnabled(ToastType.BLUETOOTH_NOT_ENABLE)){
            return
        }
        _discoveredDevices.value = mutableListOf()
        _discoveredDevices.notifyObserver()
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(context, context.getString(R.string.turn_on_gps_to_search_device), Toast.LENGTH_SHORT).show()
            context.startActivity(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            )
        }
        bluetoothAdapter?.startDiscovery()
    }

    override fun refreshPairedDevices() {
        if (!isBluetoothEnabled(ToastType.BLUETOOTH_NOT_ENABLE)){
            return
        }
        val pairedDevices = bluetoothAdapter?.bondedDevices ?: mutableSetOf()
        _isBluetoothOn.value = true
        _pairedDevices.value = pairedDevices
        _pairedDevices.notifyObserver()
    }

    override fun checkRequestPermission(onGranted : () -> Unit) {
        val permissionRequests = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){
            permissionRequests.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED){
            permissionRequests.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionRequests.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionRequests.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionRequests.isNotEmpty()){
            ActivityCompat.requestPermissions(activity, permissionRequests.toTypedArray(), 1)
        }else{
            onGranted()
        }
    }

    override fun toggleBluetoothOnOff() {
        checkRequestPermission {
            if (isBluetoothOn.value == false){
                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).also {
                    activity.startActivityForResult(it, 1)
                }
            }else{
                bluetoothAdapter?.disable()
                _isBluetoothOn.value = false
            }
            if (_isBluetoothOn.value == true){
                discoverDevices()
                refreshPairedDevices()
            }
        }
    }

    override fun onLifecycleStateDestroy() {
        bluetoothAdapter?.cancelDiscovery()
        try {
            activity.unregisterReceiver(discoverReceiver)
        }catch (_ : Exception){}
    }

    override fun subscribeBluetoothStatus(
        lifecycleOwner: LifecycleOwner,
        observer: Observer<Boolean>
    ) {
        isBluetoothOn.observe(lifecycleOwner, observer)
    }

    override fun subscribePairedDevices(
        lifecycleOwner: LifecycleOwner,
        observer: Observer<MutableSet<BluetoothDevice>>
    ) {
        pairedDevices.observe(lifecycleOwner, observer)
    }

    override fun subscribeDiscoveredDevices(
        lifecycleOwner: LifecycleOwner,
        observer: Observer<MutableList<BluetoothDevice>>
    ) {
        discoveredDevices.observe(lifecycleOwner, observer)
    }

    override fun onLifecycleStateResume() {
        isBluetoothEnabled(ToastType.BLUETOOTH_NOT_ENABLE)
    }

    override fun pairDevice(bluetoothDevice: BluetoothDevice) {
        bluetoothDevice.createBond()
    }

    private fun showToast(context: Context, toastType: ToastType){
        if (System.currentTimeMillis() - 2 * TOAST_DURATION > lastToast || toastType != lastToastType){
            val message = when(toastType){
                ToastType.BLUETOOTH_NOT_ENABLE -> context.getString(R.string.turn_on_bluetooth_first)
                ToastType.GPS_NOT_ENABLE -> context.getString(R.string.turn_on_gps_to_search_device)
                ToastType.DEVICE_NOT_SUPPORTED -> context.getString(R.string.bluetooth_not_supported)
            }
            toast = Toast.makeText(context, message, TOAST_DURATION)
            toast?.show()
            lastToast = System.currentTimeMillis()
            lastToastType = toastType
        }
    }

}