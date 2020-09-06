package com.mobile.tracer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager


/**
 * Created by Krishna Upadhya on 04/09/20.
 */

class PhoneCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        Logger.log("call received")

        try {
            val telephony =
                context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephony.listen(object : PhoneStateListener() {
                override fun onCallStateChanged(
                    state: Int,
                    incomingNumber: String
                ) {
                    super.onCallStateChanged(state, incomingNumber)
                    Logger.log("call received from $incomingNumber")

                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}