package com.gm.us30alert;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.PendingIntent;
import android.content.Context;
import android.app.Notification;
import android.os.Vibrator;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private static final String CHANNEL_ID = "us30_alerts";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new JsBridge(this), "Android");

        // Load the local tradingview page which initializes the widget
        webView.loadUrl("file:///android_asset/tradingview.html");

        // Optionally start service to keep app running at boot (if enabled)
        startService(new Intent(this, MonitorService.class));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "US30 Alerts";
            String description = "Channel for US30 MACD alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static class JsBridge {
        Context ctx;
        public JsBridge(Context c) { ctx = c; }
        @JavascriptInterface
        public void sendMacd(String macdValue) {
            try {
                double v = Double.parseDouble(macdValue);
                if (v >= 20.0 || v <= -20.0) {
                    NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle("MACD Alert")
                        .setContentText("MACD = " + macdValue)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);
                    NotificationManagerCompat.from(ctx).notify((int)System.currentTimeMillis(), b.build());
                    Vibrator vb = (Vibrator)ctx.getSystemService(Context.VIBRATOR_SERVICE);
                    if (vb != null) vb.vibrate(200);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
}
