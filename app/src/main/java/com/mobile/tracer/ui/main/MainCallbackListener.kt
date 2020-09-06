package com.mobile.tracer.ui.main

/**
 * Created by Krishna Upadhya on 2019-12-24.
 */
interface MainCallbackListener {

    fun startFirebaseUploadService()
    fun stopFirebaseUploadService()
    fun hasReadSmsPermission(): Boolean
    fun hasReadCallLogsPermission(): Boolean
}