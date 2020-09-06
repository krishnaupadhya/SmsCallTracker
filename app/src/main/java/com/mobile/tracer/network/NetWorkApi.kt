package com.mobile.tracer.network

import com.mobile.tracer.BuildConfig
import com.mobile.tracer.model.NotificationPayload
import com.mobile.tracer.model.NotificationResponse
import io.reactivex.Observable
import retrofit2.Retrofit.Builder
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by Supriya on 15/04/20.
 */
interface NetWorkApi {
    @Headers(
        "Authorization: key=AAAAV5X0jXE:APA91bHCQS2Z_WzSzZzQ3idKjfPtHcth-NanGeEAvVe2X9Xhp-1_QZygmiC1xdNwhhVbErYAcPEn94WAO3_iKLGzgClvtjB8ujE559oCzefqVvLKnIL2M5ofDbWoopXwPPeEZXq-rGyq",
        "Content-Type:application/json"
    )
    @POST(BuildConfig.FCM_SEND)
    fun publishFcmNotification(@Body payload: NotificationPayload): Observable<NotificationResponse>

    companion object {


        @JvmStatic
        fun create(): NetWorkApi {


            val retrofit = Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL)
                .build()

            return retrofit.create(NetWorkApi::class.java)
        }
    }

}