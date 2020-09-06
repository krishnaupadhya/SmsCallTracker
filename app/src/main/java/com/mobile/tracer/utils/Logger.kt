package com.mobile.tracer.utils

import android.util.Log

/**
 * Created by Krishna Upadhya on 2019-11-21.
 */
object Logger {

    fun log(msg: String) {
        Log.d(AppConstants.APP_TAG, msg)
    }

    fun log(customTag: String, msg: String) {
        Log.d(AppConstants.APP_TAG, customTag + " " + msg)
    }
}