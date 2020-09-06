package com.mobile.tracer.utils

import android.accounts.AccountManager
import android.bluetooth.BluetoothAdapter
import android.text.TextUtils
import com.mobile.tracer.MobileTracerApplication
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Krishna Upadhya on 2019-12-20.
 */
object GenericUtils {

    fun getBluetoothName(): String {

        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        if (btAdapter != null && !TextUtils.isEmpty(btAdapter.name)) {
            return btAdapter.name.replace(' ', '_').trim()
        }
        return AppConstants.DEFAULT_NAME

    }

    fun getMessageDateFilterInMilliSec(filterDate: String?): Long {
        try {
            val sdf = SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.getDefault())
            val date = sdf.parse(filterDate)
            return date!!.time
        } catch (e: Exception) {
        }

        return 0
    }

    fun getUserEmail(): String {
        try {
            val accountManager = AccountManager.get(MobileTracerApplication.applicationInstance)

            val accounts = accountManager.getAccountsByType("com.google")

            if (accounts.isNotEmpty()) {
                return accounts[0].name
            }
        } catch (e: java.lang.Exception) {
            Logger.log("getUserEmail exception ${e.message}")
        }

        return AppConstants.BLANK_STRING
    }

    fun getUserNameFromEmail(): String {
        try {
            val email = getUserEmail()
            val splitEmail = email.split("@")
            if (splitEmail.isNotEmpty()) {
                return splitEmail[0]
            }
        } catch (e: java.lang.Exception) {
            Logger.log("getUserEmail exception ${e.message}")
        }

        return AppConstants.BLANK_STRING
    }

    fun formatDate(date: String?, format: String): String {
        try {
            val dateInMilliSec = date?.toLong()
            val parsedDate = dateInMilliSec?.let { Date(it) }
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            return dateFormat.format(parsedDate)

        } catch (e: java.lang.Exception) {
            Logger.log("getMessageDisplayDate ${e.message}")
        }
        return AppConstants.BLANK_STRING
    }

    fun getYesterdayTimeInMilliSec(): Long {
        try {
            val cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            return (cal.getTime()).time;

        } catch (e: java.lang.Exception) {
            Logger.log("getMessageDisplayDate ${e.message}")
        }
        return 0
    }

    fun getTimeInMilliSec(): Long {
        return Date().time
    }

    fun getCallDuration(durationInSec: String): String {
        try {
            if (!TextUtils.isEmpty(durationInSec)) {
                val s = durationInSec.toInt()
                val sec: Int = s % 60
                val min: Int = s / 60 % 60
                val hours: Int = s / 60 / 60
                return "$hours:$min:$sec"

            }
        } catch (e: Exception) {

        }
        return durationInSec;
    }

    fun getYesterdayDate(format: String): String? {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return sdf.format(cal.time)
    }
}