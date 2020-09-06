package com.mobile.tracer.model

/**
 * Created by Krishna Upadhya on 2019-12-23.
 */
data class CustomMessage(
    var message: String,
    var date: String,
    var sender: String,
    var receiver: String,
    var receivedDevice: String,
    var simId: String
)