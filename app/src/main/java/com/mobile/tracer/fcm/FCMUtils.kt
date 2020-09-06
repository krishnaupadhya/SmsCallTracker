package com.mobile.tracer.fcm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.mobile.tracer.MobileTracerApplication
import com.mobile.tracer.firestore.FireStoreUtils
import com.mobile.tracer.utils.*

/**
 * Created by Krishna Upadhya on 2019-12-24.
 */
object FCMUtils {

    fun getFcmTopicName(): String {
        return GenericUtils.getUserNameFromEmail()
    }

    fun getFcmToken(): String {
        return SharedPrefUtils.getFromSharedPreferences(AppConstants.FCM_TOKEN)
    }

    fun processNotification(notificationData: MutableMap<String, String>) {
        val date = notificationData.get(AppConstants.GET_MESSAGE_FROM_DATE)
        Logger.log(AppConstants.NOTIFICATION_TAG, "processNotification date $date")
        if (!TextUtils.isEmpty(date)) {
            val messageFilerDate = GenericUtils.getMessageDateFilterInMilliSec(date)
            val messageList = InboxUtil.getInboxMessages(messageFilerDate)

            var callLogsList = CallLogUtils.getCallDetails(messageFilerDate);
            if (callLogsList != null && !callLogsList.isEmpty()) {
                callLogsList = callLogsList.reversed()
                MessageManager.callLogsMap = FireStoreUtils.getCallLogMapFromList(callLogsList)
            }
            if (messageList.isNotEmpty()) {
                MessageManager.messageMap = FireStoreUtils.getMessageMapFromList(messageList)
                startAlarmManager();
            }
        }
    }

    private fun startAlarmManager() {
        Logger.log(AppConstants.NOTIFICATION_TAG, "startAlarmManager")
        //Alarm Manager will trigger event bus to call LATEST VEHICLE DATA
        val intent = Intent(MobileTracerApplication.applicationInstance, AlarmReceiver::class.java)
        intent.putExtra(
            AppConstants.KEY_INTENT_SERVICE_TYPE,
            AppConstants.START_UPLOADING_BACKGROUND
        )
        val alarmPendingIntent =
            PendingIntent.getBroadcast(MobileTracerApplication.applicationInstance, 0, intent, 0)
        val alarmManager =
            MobileTracerApplication.applicationInstance.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Remove any previous pending intent.
        alarmManager.cancel(alarmPendingIntent)
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + AppConstants.TWO_SECONDS, alarmPendingIntent
        )
    }
}