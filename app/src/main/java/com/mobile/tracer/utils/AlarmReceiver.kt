package com.mobile.tracer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.mobile.tracer.firestore.FireStoreUploadService

/**
 * Created by Krishna Upadhya on 2019-12-28.
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Logger.log(AppConstants.NOTIFICATION_TAG, "AlarmReceiver onReceive")

        val bundle = intent?.extras

        if (AppConstants.START_UPLOADING_BACKGROUND.equals(bundle?.getString(AppConstants.KEY_INTENT_SERVICE_TYPE))) {
            Logger.log(AppConstants.NOTIFICATION_TAG, "AlarmReceiver onReceive")
            val fireStoreService = Intent(context, FireStoreUploadService::class.java);
            fireStoreService.putExtra(
                AppConstants.KEY_INTENT_SERVICE_TYPE,
                AppConstants.START_UPLOADING
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context?.startForegroundService(fireStoreService)
            } else {
                context?.startService(fireStoreService)
            }
        }


    }

}