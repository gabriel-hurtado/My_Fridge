package com.hurtado.gabriel.myfridge;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;
import java.util.StringTokenizer;


public class Boot extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Log.e("TAG", "Boot completed");

            SharedPreferences prefs = context.getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
            int notificationNumber = prefs.getInt("notificationNumber", 0);
            int hour = prefs.getInt("hour", 10);
            int min = prefs.getInt("min", 0);
            NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNM.cancelAll();

            for (int i = 0; i < notificationNumber; i++) {
                foodClient.resetAlarmForNotification(i, context);
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("notificationNumber", 0);
            editor.apply();


            DbAdapter dbHelper = new DbAdapter(context);
            dbHelper.open();
            Cursor cursor = dbHelper.fetchAll();
            if (cursor != null && cursor.moveToFirst()) {


                do {


                    String date = cursor.getString(2);
                    if (Character.isDigit(date.charAt(0))) {
                        StringTokenizer tokens = new StringTokenizer(date, "/");
                        String first = tokens.nextToken();// this will contain "Fruit"
                        String second = tokens.nextToken();
                        String third = tokens.nextToken();
                        third = third.replace(" ", "");

                        int yr = Integer.parseInt(third);
                        int monthOfYear = Integer.parseInt(second) - 1;
                        int dayOfMonth = Integer.parseInt(first);
                        Calendar alarm = Calendar.getInstance();


                        alarm.set(yr, monthOfYear, dayOfMonth);

                        alarm.add(Calendar.DAY_OF_MONTH, -1);
                        alarm.set(Calendar.HOUR_OF_DAY, hour);
                        alarm.set(Calendar.MINUTE, min);
                        alarm.set(Calendar.SECOND, 0);
                        long id = Long.parseLong(cursor.getString(0));
                        new AlarmTask(context, alarm, id).run();
                    }
                } while (cursor.moveToNext());

            }
        }
    }
}