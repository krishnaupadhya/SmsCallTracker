package com.mobile.tracer.firestore

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.mobile.tracer.R
import com.mobile.tracer.ui.MainActivity
import com.mobile.tracer.utils.AppConstants
import com.mobile.tracer.utils.Logger.log
import com.mobile.tracer.utils.MessageManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import com.mobile.tracer.eventbus.DataUploadEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe


/**
 * Created by Krishna Upadhya on 2019-08-20.
 */

class FireStoreUploadService : Service() {
    private lateinit var firebaseServiceType: String
    private lateinit var notificationView: RemoteViews
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var notificationManager: NotificationManager? = null
    private val FOREGROUND_CHANNEL_ID = "foreground_channel_id"

    override fun onCreate() {
        super.onCreate()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            log(
                "onStartCommand intent == null stopping service"
            )
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }
        log(
            "onStartCommand"
        )
        if (intent.hasExtra(AppConstants.KEY_INTENT_SERVICE_TYPE)) {
            firebaseServiceType = intent.getStringExtra(AppConstants.KEY_INTENT_SERVICE_TYPE)
        }


        when (firebaseServiceType) {
            AppConstants.START_UPLOADING -> startDataUploadToFireStore()
            AppConstants.STOP_UPLOADING -> stopService()
            else -> log(
                "command cannot be empty"
            )

        }
        return START_NOT_STICKY
    }

    private fun startDataUploadToFireStore() {
        startForeground(AppConstants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification())
        MessageManager.messageMap?.let {
            if (it.isNotEmpty()) {
                FireStoreUtils.uploadInboxMessageMap(it)
            }
        }
        MessageManager.callLogsMap?.let {
            if (it.isNotEmpty()) {
                FireStoreUtils.uploadCallLog(it)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DataUploadEvent) {
        event.let {
            if (event.status) {
                updateNotification(
                    R.id.iv_upload_status_text,
                    getString(R.string.uploading_data_success)
                )
            } else {
                updateNotification(
                    R.id.iv_upload_status_text,
                    getString(R.string.uploading_data_failed)
                )
            }
            GlobalScope.launch {
                stopServiceWithDelay()
            }
        }
    }

    private fun updateNotification(viewId: Int, msg: String) {
        notificationView.setTextViewText(viewId, msg)
        notificationManager?.notify(
            AppConstants.NOTIFICATION_ID_FOREGROUND_SERVICE,
            notificationBuilder.build()
        )
    }


    private suspend fun stopServiceWithDelay() {
        delay(2000)
        stopService()
    }

    private fun stopService() {
        notificationManager?.cancel(0)
        stopForeground(true)
        stopSelf()
    }

    private fun prepareNotification(): Notification {
        if (SDK_INT > O && notificationManager?.getNotificationChannel(FOREGROUND_CHANNEL_ID) == null) {
            val name: CharSequence = getString(R.string.notification_name)
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance)
            channel.enableVibration(true)
            notificationManager?.createNotificationChannel(channel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notificationView = RemoteViews(packageName, R.layout.notication_item_layout)

        val stopUploadingEvent = Intent(this, FireStoreUploadService::class.java);
        stopUploadingEvent.putExtra(
            AppConstants.KEY_INTENT_SERVICE_TYPE,
            AppConstants.STOP_UPLOADING
        )

//        notificationView.setOnClickPendingIntent(
//            R.id.iv_close,
//            PendingIntent.getService(
//                this, 2, stopUploadingEvent
//                , PendingIntent.FLAG_UPDATE_CURRENT
//            )
//        )

        if (SDK_INT >= O) {
            notificationBuilder = NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
        } else {
            notificationBuilder = NotificationCompat.Builder(this)
        }
        notificationBuilder
            .setContentTitle(getString(R.string.app_name))
            .setContent(notificationView)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)



        if (SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
        return notificationBuilder.build()
    }

    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }


}