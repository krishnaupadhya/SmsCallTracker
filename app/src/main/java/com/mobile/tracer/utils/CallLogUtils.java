package com.mobile.tracer.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;

import androidx.core.app.ActivityCompat;

import com.mobile.tracer.MobileTracerApplication;
import com.mobile.tracer.model.CustomCallLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Krishna Upadhya on 08/05/20.
 */
public class CallLogUtils {

    public static List<CustomCallLog> getCallDetails(Long inboxFilterDate) {

        if (ActivityCompat.checkSelfPermission(MobileTracerApplication.applicationInstance, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        List<CustomCallLog> callLogList = new ArrayList<>();
        String filter = "date>=" + inboxFilterDate;
        Cursor managedCursor = MobileTracerApplication.applicationInstance.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, filter, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = GenericUtils.INSTANCE.getCallDuration(managedCursor.getString(duration));
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            CustomCallLog log = new CustomCallLog(phNumber + "", dir + "", callDuration + "", callDayTime + "");
            callLogList.add(log);
            Logger.INSTANCE.log("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
        }
        managedCursor.close();
        return callLogList;
    }
}
