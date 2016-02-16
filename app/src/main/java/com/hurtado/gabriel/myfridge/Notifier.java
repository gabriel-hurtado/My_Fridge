package com.hurtado.gabriel.myfridge;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;


public class Notifier extends Service {

    public static final String INTENT_NOTIFY = "com.hurtado.gabriel.myfridge.INTENT_NOTIFY";
    private final IBinder mBinder = new ServiceBinder();
    private NotificationManager nm;
    private DbAdapter dbHelper;

    @Override
    public void onCreate() {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification(intent.getLongExtra("id",0));

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification(Long id) {

        SharedPreferences prefs = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        int notificationNumber = prefs.getInt("notificationNumber", 0);

        PendingIntent contentIntent = PendingIntent.getActivity(this, notificationNumber, new Intent(this, MainActivity.class), 0);
        dbHelper = new DbAdapter(this);
        dbHelper.open();
        String name=dbHelper.fetchName(id);
        dbHelper.close();
        if(name.equals(""))
        {
            name= getString(R.string.food);
        }

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("My Fridge ")
                .setContentText(getString(R.string.Your)+name+getString(R.string.Will))
                .setSmallIcon(R.drawable.ic_event_note_white_24dp)
                .setContentIntent(contentIntent)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;




        nm.notify(notificationNumber, notification);


        stopSelf();
    }

    private class ServiceBinder extends Binder {
    }
}