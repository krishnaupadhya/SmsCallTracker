package com.mobile.tracer.ui.main

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobile.tracer.network.NetWorkApi
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.tracer.firestore.FireStoreUtils
import com.mobile.tracer.model.CustomCallLog
import com.mobile.tracer.model.CustomMessage
import com.mobile.tracer.model.Data
import com.mobile.tracer.model.NotificationPayload
import com.mobile.tracer.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MainViewModel : ViewModel() {

    var uploadDataToServer = MutableLiveData<Boolean>()
    var fireBaseDataFetchSuccess = MutableLiveData<Boolean>()
    var selectedName = MutableLiveData<String>()
    var selectedDate = MutableLiveData<String>()
    var callsList = MutableLiveData<ArrayList<CustomCallLog>>()
    var messagesList = MutableLiveData<ArrayList<CustomMessage>>()

    private val netWorkApi by lazy {
        NetWorkApi.create()
    }

    init {
        selectedDate.value = GenericUtils.getYesterdayDate(AppConstants.DATE_FORMAT)
    }

    fun fetchAllLogs() {
        val name = selectedName.value
        if (!TextUtils.isEmpty(name)) {
            fetchMessagesFromFirebase(name!!)
            fetchCallLogsFirebase(name)
        }
    }

    private fun fetchMessagesFromFirebase(collectionName: String) {
        FirebaseFirestore.getInstance()
            .document("${AppConstants.MESSAGE_COLLECTION_NAME}/$collectionName")
            .get()
            .addOnSuccessListener { document ->
                var firebaseMessagesList = ArrayList<CustomMessage>()
                if (document != null) {
                    val data = document.data
                    data?.let {
                        val messageList = data.get(AppConstants.KEY_MESSAGE_LIST) as ArrayList<Any>
                        for (msg in messageList) {
                            val map = msg as HashMap<String, String>
                            firebaseMessagesList.add(
                                CustomMessage(
                                    map["message"]!!,
                                    map["date"]!!,
                                    map["sender"]!!,
                                    map["receiver"]!!,
                                    map["receivedDevice"]!!,
                                    map["simId"]!!
                                )
                            )
                        }
                    }
                }
                messagesList.value = firebaseMessagesList
                fireBaseDataFetchSuccess.value = true
            }
            .addOnFailureListener { exception ->
                fireBaseDataFetchSuccess.value = false
                Logger.log("fetchMessagesFromFirebase get failed: ${exception.message}")
            }
    }

    private fun fetchCallLogsFirebase(collectionName: String) {
        FirebaseFirestore.getInstance()
            .document("${AppConstants.CALL_LOG_COLLECTION_NAME}/$collectionName")
            .get()
            .addOnSuccessListener { document ->
                var firebaseCallLogList = ArrayList<CustomCallLog>()
                if (document != null) {
                    val data = document.data
                    data?.let {
                        val messageList = data.get(AppConstants.KEY_CALL_LOG_LIST) as ArrayList<Any>
                        for (msg in messageList) {
                            val map = msg as HashMap<String, String>
                            firebaseCallLogList.add(
                                CustomCallLog(
                                    map["number"]!!,
                                    map["type"]!!,
                                    map["duration"]!!,
                                    map["time"]!!
                                )
                            )
                        }
                    }
                }
                callsList.value = firebaseCallLogList
                fireBaseDataFetchSuccess.value = true
            }
            .addOnFailureListener { exception ->
                fireBaseDataFetchSuccess.value = false
                Logger.log("fetchCallLogsFirebase get failed: ${exception.message}")
            }
    }

    fun prepareDataToUpload() {
        val messageFilerDate = GenericUtils.getYesterdayTimeInMilliSec()
        val messageList: List<CustomMessage> = InboxUtil.getInboxMessages(messageFilerDate)
        if (messageList.isNotEmpty()) {
            MessageManager.messageMap = FireStoreUtils.getMessageMapFromList(messageList)
        }

        var callLogsList: List<CustomCallLog> = CallLogUtils.getCallDetails(messageFilerDate);
        if (callLogsList.isNotEmpty()) {
            callLogsList = callLogsList.reversed()
            MessageManager.callLogsMap = FireStoreUtils.getCallLogMapFromList(callLogsList)
        }

        if (messageList.isNotEmpty() || callLogsList.isNotEmpty()) {
            uploadDataToServer.value = true
        }
    }

    fun publishNotification() {

        var disposable =
            netWorkApi.publishFcmNotification(getFcmPayload()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    Logger.log("Notification publish success ---${data.message_id} ")

                }, { e -> Logger.log("Notification publish failed ---error ${e.message} ") })


    }

    private fun getHeaderMap(): Map<String, String> {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] =
            "key=AAAAV5X0jXE:APA91bHCQS2Z_WzSzZzQ3idKjfPtHcth-NanGeEAvVe2X9Xhp-1_QZygmiC1xdNwhhVbErYAcPEn94WAO3_iKLGzgClvtjB8ujE559oCzefqVvLKnIL2M5ofDbWoopXwPPeEZXq-rGyq}"
        headerMap["Content-Type"] = "application/json"
        return headerMap
    }

    private fun getFcmPayload(): NotificationPayload {
        val data = Data(selectedDate.value!!)
        val name = selectedName.value!!
        return NotificationPayload(data, "/topics/$name")
    }
}