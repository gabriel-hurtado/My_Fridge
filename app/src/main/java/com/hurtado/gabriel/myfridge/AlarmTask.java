package com.hurtado.gabriel.myfridge;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;


class AlarmTask implements Runnable{
    private final Calendar date;
    private final AlarmManager am;
    private final Context context;
    private final Long id;
    private final boolean notify = true;

    public AlarmTask(Context context, Calendar date, Long id) {
        this.context = context;
        this.am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
        this.id = id;
    }



    @Override
    public void run() {
        Calendar dateComp=date;
        dateComp.add(Calendar.DAY_OF_MONTH,1);
        long time=dateComp.getTimeInMillis();
        long time2=Calendar.getInstance().getTimeInMillis();
        if(time>time2) {
            Intent intent = new Intent(context, Notifier.class);
            intent.putExtra(Notifier.INTENT_NOTIFY, this.notify);
            intent.putExtra("id", this.id);

            SharedPreferences prefs = context.getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
            int notificationNumber = prefs.getInt("notificationNumber", 0);
            SharedPreferences.Editor editor = prefs.edit();
            PendingIntent pendingIntent = PendingIntent.getService(context, notificationNumber, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationNumber++;
            editor.putInt("notificationNumber", notificationNumber);
            editor.apply();
            am.set(AlarmManager.RTC, date.getTimeInMillis(), pendingIntent);
        }
    }


}