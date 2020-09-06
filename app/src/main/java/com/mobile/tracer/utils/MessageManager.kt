package com.mobile.tracer.utils

import java.util.*
import kotlin.collections.HashMap

/**
 * Created by Krishna Upadhya on 2019-12-23.
 */
object MessageManager {

    var messageMap: HashMap<String, Any>? = null
        get() = field
        set(value) {
            if (field == null) HashMap<String, Any>()
            field = value
        }
    var callLogsMap: HashMap<String, Any>? = null
        get() = field
        set(value) {
            if (field == null) HashMap<String, Any>()
            field = value
        }

}