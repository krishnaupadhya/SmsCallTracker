package com.mobile.tracer.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by Krishna Upadhya on 2019-12-20.
 */
object PermissionUtils {

    val DASHBOARD_PERMISSION_CODE: Int = 1111

    /**
     * Runtime permission
     */
    public fun hasReadSmsPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    public fun hasReadCallLogsPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Runtime permission
     */
    public fun hasReadPhoneAndGetAccountsPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.GET_ACCOUNTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    public fun requestAllPermissions(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_SMS
            )
        ) {
            Logger.log("shouldShowRequestPermissionRationale(), no permission requested")
            return
        }
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG
            ),
            DASHBOARD_PERMISSION_CODE
        )
    }
}