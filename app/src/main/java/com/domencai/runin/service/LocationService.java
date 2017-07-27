package com.domencai.runin.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.domencai.runin.R;
import com.domencai.runin.activity.RunActivity;
import com.domencai.runin.bean.Location;
import com.domencai.runin.bean.PathRecord;
import com.domencai.runin.listener.LocationListener;
import com.domencai.runin.utils.SpeedUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Domen、on 2016/11/18.
 */

public class LocationService extends Service implements BDLocationListener {

    private MyBinder mBinder;
    private LocationClient mLocationClient;
    private LocationListener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new MyBinder(this);
        Log.w("LocationService", "onCreate: ");
        startForeground(110, getNotification());
        initLocationClient();
    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        //获取一个Notification构造器
        Intent nfIntent = new Intent(this, RunActivity.class);

        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap
                        .ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("下拉列表中的Title") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("要显示的内容") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        return builder.build(); // 获取构建好的Notification
    }

    private void initLocationClient() {
        mLocationClient = new LocationClient(getApplicationContext());

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIgnoreKillProcess(false);
        option.setOpenAutoNotifyMode(2000, 5, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//        option.setScanSpan(2000);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(this);
        mLocationClient.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (mLocationClient != null)
            mLocationClient.stop();
        Log.w("LocationService", "onDestroy: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w("LocationService", "onBind: ");
        return mBinder;
    }

    private boolean isFirst = true;
    private boolean isStart;
    private PathRecord mRecord;
    private List<Location> mLocations;

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (mListener == null)
            return;
        Location location = new Location(bdLocation);
        location.time = System.currentTimeMillis();
        mListener.onLocationChanged(location);
        if (isFirst) {
            mListener.onLocationSucceed(location);
            isFirst = false;
        }
        if (isStart) {
            mRecord.addPoint(location.getLatLng());
            mListener.onRunning(mRecord);
            mLocations.add(location);
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    public static class MyBinder extends Binder {
        private LocationService mService;

        MyBinder(LocationService service) {
            mService = service;
        }

        public LocationClient getLocationClient() {
            return mService.mLocationClient;
        }

        public void start() {
            mService.isStart = true;
            if (mService.mRecord == null)
                mService.mRecord = new PathRecord();
            mService.mLocations = new ArrayList<>();
        }

        public void pause() {
            mService.isStart = false;
            mService.mRecord.addPoint(null);
        }

        public void setLocationListener(LocationListener listener) {
            mService.mListener = listener;
        }

        public PathRecord getPathRecord() {
            return mService.mRecord;
        }

        public BDLocation getLastKnowLocation() {
            return mService.mLocationClient.getLastKnownLocation();
        }

        public List<Integer> getPathColor() {
            PathRecord record = mService.mRecord;
            float avg = record.getDis() / record.getDuration();
            return SpeedUtils.getPathColor(mService.mLocations, avg);
        }
    }
}
