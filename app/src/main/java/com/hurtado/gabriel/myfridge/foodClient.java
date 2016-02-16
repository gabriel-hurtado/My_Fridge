package com.hurtado.gabriel.myfridge;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.Calendar;

class foodClient {

    private static foodService mBoundService;
    private final Context context;
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((foodService.ServiceBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };
    private boolean mIsBound;

    public foodClient(Context context) {
        this.context = context;
    }

    public static void setAlarmForNotification(Calendar c, Long id){

        mBoundService.setAlarm(c, id);
    }

    public static void resetAlarmForNotification(int uniqueId,Context mContext) {
        if(mContext==null) {
            mContext = mBoundService;
        }
        ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getService(mContext, uniqueId, new Intent(mContext, Notifier.class), 0));
    }

    public void doBindService() {
        context.bindService(new Intent(context, foodService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    public void doUnbindService() {
        if (mIsBound) {
            context.unbindService(mConnection);
            mIsBound = false;
        }
    }
}
