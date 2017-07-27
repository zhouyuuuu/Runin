package com.domencai.runin.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.domencai.runin.activity.MyLockScreenActivity;

/**
 * Created by Domen、on 2016/11/18.
 */

public class LockService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("LockService", "onCreate: ");
        IntentFilter mScreenOnFilter = new IntentFilter();
        mScreenOnFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenOnFilter.addAction(Intent.ACTION_SCREEN_ON);
        LockService.this.registerReceiver(mScreenActionReceiver, mScreenOnFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenActionReceiver);
    }

    private BroadcastReceiver mScreenActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                Log.w("LockService", "onReceive: screen on");
                Intent LockIntent = new Intent(LockService.this,MyLockScreenActivity.class);
                LockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(LockIntent);
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                Log.w("LockService", "onReceive: screen off");
            }
        }
    };
}
