package com.gm.us30alert;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
public class MonitorService extends Service {
    @Override public void onCreate() { super.onCreate(); }
    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Nullable @Override public IBinder onBind(Intent intent) { return null; }
}
