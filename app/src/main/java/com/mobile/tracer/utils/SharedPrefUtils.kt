package com.mobile.tracer.utils

import android.content.Context
import android.content.SharedPreferences
import com.mobile.tracer.MobileTracerApplication

/**
 * Created by Krishna Upadhya on 2019-12-30.
 */

object SharedPrefUtils {
    val SHARED_PREFERENCES_TAG = "sharedPreferences"
    private var sharedPreferences: SharedPreferences? = null
    val sharedPrefFileName = "MobileTracerPreferences"

    fun putOnSharedPreference(key: String, value: String) {
        if (sharedPreferences == null) {
            sharedPreferences = MobileTracerApplication.applicationInstance.getSharedPreferences(
                sharedPrefFileName, Context.MODE_PRIVATE
            )
        }
        val editor = sharedPreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
        editor?.commit()
    }

    fun getFromSharedPreferences(key: String): String {
        try {
            if (sharedPreferences == null) {
                sharedPreferences =
                    MobileTracerApplication.applicationInstance.getSharedPreferences(
                        sharedPrefFileName, Context.MODE_PRIVATE
                    )
            }
            return sharedPreferences?.getString(key, AppConstants.BLANK_STRING).toString()
        } catch (e: Exception) {
            Logger.log(SHARED_PREFERENCES_TAG, "Exception ${e.message}")
        }
        return AppConstants.BLANK_STRING
    }

}