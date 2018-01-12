package com.ycxy.ymh.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class LongRunningService extends Service {
    private long time = 0;
    private AlarmManager manager;
    private PendingIntent pendingIntent;
    private Intent intent;
    private static final String TAG = "LongRunningService";
    public void setTime(long time) {

        if (manager != null && pendingIntent != null) {
            manager.cancel(pendingIntent);
        }

        this.time = time;
        long mTime = time * 60 * 1000;
        long setTime = SystemClock.elapsedRealtime() + mTime;
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        intent = new Intent("android.yoyhm.action.TIME_TO");
        pendingIntent = PendingIntent.getBroadcast(this,
                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, setTime, pendingIntent);
    }

    public LongRunningService() {
    }


    public class LongTimeIBinder extends Binder {
        LongRunningService service = LongRunningService.this;

        public void setTime(int time) {
            service.setTime(time);
        }

        public void cancel() {
            service.cancel();
        }
    }

    private void cancel() {
        manager.cancel(pendingIntent);
        Log.d(TAG, "cancel: -------------------");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new LongTimeIBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
