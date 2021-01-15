package com.rick.companiondevicepairing

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import java.util.regex.Pattern


private const val SELECT_DEVICE_REQUEST_CODE = 57

class MyDeviceSelectionActivity : AppCompatActivity() {

    companion object {
        private val TAG = MyDeviceSelectionActivity::class.java.simpleName
    }

    private val deviceManager: CompanionDeviceManager by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(CompanionDeviceManager::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection_device_my)
        Log.d(TAG, "onCreate: ")
        // ...
        val isSupportCdp =
            packageManager.hasSystemFeature(PackageManager.FEATURE_COMPANION_DEVICE_SETUP)
        Log.d(TAG, "isSupportCdp: $isSupportCdp")

        // To skip filtering based on name and supported feature flags (UUIDs),
        // don't include calls to setNamePattern() and addServiceUuid(),
        // respectively. This example uses Bluetooth.
        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            .setNamePattern(Pattern.compile("My device"))
            .setAddress("My Address")
            .addServiceUuid(ParcelUuid(UUID(0x123abcL, -1L)), null)
            .build()

        // The argument provided in setSingleDevice() determines whether a single
        // device name or a list of device names is presented to the user as
        // pairing options.
        val pairingRequest: AssociationRequest = AssociationRequest.Builder()
            .addDeviceFilter(null)
            .setSingleDevice(false)
            .build()

        // When the app tries to pair with the Bluetooth device, show the
        // appropriate pairing request dialog to the user.
        deviceManager.associate(
            pairingRequest,
            object : CompanionDeviceManager.Callback() {

                override fun onDeviceFound(chooserLauncher: IntentSender) {
                    startIntentSenderForResult(
                        chooserLauncher,
                        SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0
                    )
                }

                override fun onFailure(error: CharSequence?) {
                    // Handle failure
                }
            }, null
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SELECT_DEVICE_REQUEST_CODE -> when (resultCode) {
                Activity.RESULT_OK -> {
                    // User has chosen to pair with the Bluetooth device.
                    val deviceToPair: BluetoothDevice? =
                        data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)

//                    deviceToPair.createBond()
                    // ... Continue interacting with the paired device.
                }
            }
        }
    }
}