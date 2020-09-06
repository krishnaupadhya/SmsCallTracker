package com.mobile.tracer.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mobile.tracer.utils.AppConstants
import com.mobile.tracer.utils.Logger
import com.mobile.tracer.utils.SharedPrefUtils

/**
 * Created by Krishna Upadhya on 2019-12-23.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.data.let {
            Logger.log("Message data payload: ${remoteMessage.data}")
            FCMUtils.processNotification(remoteMessage.data)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Logger.log("FCM TOKEN: $token")
        SharedPrefUtils.putOnSharedPreference(AppConstants.FCM_TOKEN, token)
    }

}