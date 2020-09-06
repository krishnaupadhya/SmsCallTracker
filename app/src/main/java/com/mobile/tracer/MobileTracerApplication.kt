package com.mobile.tracer

import android.app.Application


/**
 * Created by Krishna Upadhya on 2019-12-20.
 */

class MobileTracerApplication : Application() {

    companion object {
        lateinit var applicationInstance: MobileTracerApplication
    }

    override fun onCreate() {
        super.onCreate()
        applicationInstance = this
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}