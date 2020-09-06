package com.mobile.tracer.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.tracer.eventbus.DataUploadEvent
import com.mobile.tracer.fcm.FCMUtils
import com.mobile.tracer.model.CustomCallLog
import com.mobile.tracer.model.CustomMessage
import com.mobile.tracer.model.DeviceName
import com.mobile.tracer.utils.AppConstants
import com.mobile.tracer.utils.GenericUtils
import com.mobile.tracer.utils.Logger
import org.greenrobot.eventbus.EventBus
import java.util.HashMap

/**
 * Created by Krishna Upadhya on 2019-12-23.
 */
object FireStoreUtils {

    fun uploadInboxMessageMap(messageMap: HashMap<String, Any>) {
        val instance = FirebaseFirestore.getInstance()
        messageMap.let {
            instance.collection(AppConstants.MESSAGE_COLLECTION_NAME)
                .document(GenericUtils.getUserNameFromEmail())
                .set(it)
                .addOnSuccessListener { documentReference ->
                    Logger.log("uploadInboxMessageData DocumentSnapshot added with ID: ${documentReference}")
                    EventBus.getDefault().post(DataUploadEvent(true))

                }
                .addOnFailureListener { e ->
                    Logger.log(" uploadInboxMessageData Error adding document ${e.message}")
                    EventBus.getDefault().post(DataUploadEvent(false))
                }
        }
    }

    fun uploadCallLog(callLog: HashMap<String, Any>) {
        val instance = FirebaseFirestore.getInstance()
        callLog.let {
            instance.collection(AppConstants.CALL_LOG_COLLECTION_NAME)
                .document(GenericUtils.getUserNameFromEmail())
                .set(it)
                .addOnSuccessListener { documentReference ->
                    Logger.log("uploadCallLog DocumentSnapshot added with ID: ${documentReference}")
                    EventBus.getDefault().post(DataUploadEvent(true))

                }
                .addOnFailureListener { e ->
                    Logger.log(" uploadCallLog Error adding document ${e.message}")
                    EventBus.getDefault().post(DataUploadEvent(false))
                }
        }
    }

    fun uploadDeviceName() {
        val deviceName = DeviceName(GenericUtils.getBluetoothName(),
            GenericUtils.getUserEmail(),
            GenericUtils.getUserNameFromEmail(),
            FCMUtils.getFcmToken(),
            FCMUtils.getFcmTopicName()
        )
        val instance = FirebaseFirestore.getInstance()
        Logger.log("-------- deviceName $deviceName")
        instance.collection(AppConstants.INSTALLED_DEVICE_NAMES)
            .document(GenericUtils.getUserNameFromEmail())
            .set(deviceName)
            .addOnSuccessListener { documentReference ->
                Logger.log(" uploadDeviceName DocumentSnapshot added with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Logger.log(" uploadDeviceName Error adding document ${e.message}")
            }
    }

    fun getMessageMapFromList(messageList: List<CustomMessage>): HashMap<String, Any> {
        return hashMapOf(
           AppConstants.KEY_MESSAGE_LIST to messageList
        )
    }

    fun getCallLogMapFromList(callLogs: List<CustomCallLog>): HashMap<String, Any> {
        return hashMapOf(
            AppConstants.KEY_CALL_LOG_LIST to callLogs
        )
    }
}