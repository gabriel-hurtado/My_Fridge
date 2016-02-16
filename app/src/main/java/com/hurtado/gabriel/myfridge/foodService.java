package com.hurtado.gabriel.myfridge;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Calendar;

public class foodService extends Service {

    private final IBinder mBinder = new ServiceBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setAlarm(Calendar c, Long id) {
        new AlarmTask(this, c,id).run();
    }

    public class ServiceBinder extends Binder {
        foodService getService() {
            return foodService.this;
        }
    }
}