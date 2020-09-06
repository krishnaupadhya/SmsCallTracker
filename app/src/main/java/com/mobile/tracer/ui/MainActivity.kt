package com.mobile.tracer.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.mobile.tracer.BuildConfig
import com.mobile.tracer.R
import com.mobile.tracer.fcm.FCMUtils
import com.mobile.tracer.firestore.FireStoreUploadService
import com.mobile.tracer.firestore.FireStoreUtils
import com.mobile.tracer.ui.main.MainCallbackListener
import com.mobile.tracer.ui.main.MainFragment
import com.mobile.tracer.utils.AppConstants
import com.mobile.tracer.utils.Logger
import com.mobile.tracer.utils.PermissionUtils


class MainActivity : FragmentActivity(), MainCallbackListener {

    private var mainFragment: MainFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)


        if (BuildConfig.IS_TRACKER) {
            val trackerView = findViewById<RelativeLayout>(R.id.tracker_view)
            trackerView.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        FirebaseMessaging.getInstance().subscribeToTopic("ALL_DEVICES")

        addMainFragment();

        validatePermissions()
    }

    private fun validatePermissions() {
        if (!PermissionUtils.hasReadSmsPermission(this)
            || !PermissionUtils.hasReadPhoneAndGetAccountsPermission(this)
            || !PermissionUtils.hasReadCallLogsPermission(this)
        ) {
            Logger.log("sms permission not granted")
            PermissionUtils.requestAllPermissions(this);
        } else {
            if (PermissionUtils.hasReadPhoneAndGetAccountsPermission(this)) {
                Logger.log("hasReadPhoneAndGetAccountsPermission permission not granted")
                FireStoreUtils.uploadDeviceName()
            }
            mainFragment?.uploadDeviceDataToServer()
        }
    }

    private fun addMainFragment() {
        mainFragment =
            supportFragmentManager.findFragmentByTag(AppConstants.MAIN_FRAGMENT_TAG) as? MainFragment
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, mainFragment!!, AppConstants.MAIN_FRAGMENT_TAG)
            .commitNow()
    }

    override fun hasReadSmsPermission(): Boolean {
        return PermissionUtils.hasReadSmsPermission(this)
    }

    override fun hasReadCallLogsPermission(): Boolean {
        return PermissionUtils.hasReadCallLogsPermission(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        for (i in permissions.indices) {
            if (permissions[i] == android.Manifest.permission.READ_CONTACTS && grantResults[i] == 0) {
                FirebaseMessaging.getInstance()
                    .subscribeToTopic(FCMUtils.getFcmTopicName())
                FireStoreUtils.uploadDeviceName()
            }
        }
        when (requestCode) {
            PermissionUtils.DASHBOARD_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Logger.log("sms permission received")
                    mainFragment?.uploadDeviceDataToServer()
                    // hideIcon();
                } else {
                    Logger.log("sms permission denied")
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    override fun startFirebaseUploadService() {
        val fireStoreService = Intent(this, FireStoreUploadService::class.java);
        fireStoreService.putExtra(
            AppConstants.KEY_INTENT_SERVICE_TYPE,
            AppConstants.START_UPLOADING
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(fireStoreService)
        } else {
            startService(fireStoreService)
        }
    }

    override fun stopFirebaseUploadService() {
        val fireStoreService = Intent(this, FireStoreUploadService::class.java);
        fireStoreService.putExtra(
            AppConstants.KEY_INTENT_SERVICE_TYPE,
            AppConstants.STOP_UPLOADING
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(fireStoreService)
        } else {
            startService(fireStoreService)
        }
    }
}
