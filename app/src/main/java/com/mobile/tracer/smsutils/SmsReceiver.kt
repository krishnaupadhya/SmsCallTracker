package com.mobile.tracer.smsutils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import com.mobile.tracer.utils.Logger

/**
 * Created by Krishna Upadhya on 2019-12-20.
 */

class SmsReceiver() :
    BroadcastReceiver() {

    private val TAG = "SmsBroadcastReceiver"

    private var listener: Listener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            var smsSender: String? = ""
            var smsBody = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.displayOriginatingAddress
                    smsBody += smsMessage.messageBody
                }
            } else {
                val smsBundle = intent.extras
                if (smsBundle != null) {
                    val pdus = smsBundle.get("pdus") as Array<Any>?
                    if (pdus == null) {
                        // Display some error to the user
                        Logger.log(TAG, "SmsBundle had no pdus key")
                        return
                    }
                    val messages = arrayOfNulls<SmsMessage>(pdus.size)
                    for (i in messages.indices) {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        smsBody += messages[i]?.getMessageBody()
                    }
                    smsSender = messages[0]?.getOriginatingAddress()
                }
            }
            Logger.log(TAG, " sent by $smsSender")
            if (listener != null) {
                listener!!.onTextReceived(smsBody)
            }
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    interface Listener {
        fun onTextReceived(text: String)
    }

}