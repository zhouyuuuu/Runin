package com.domencai.runin.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.domencai.runin.custom.ChartView;
import com.domencai.runin.listener.LocationListener;
import com.domencai.runin.R;
import com.domencai.runin.bean.Location;
import com.domencai.runin.bean.PathRecord;
import com.domencai.runin.service.LocationService;
import com.domencai.runin.utils.CountDown;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Domen、on 2016/11/18.
 */

public class RunActivity extends AppCompatActivity implements LocationListener, View
        .OnClickListener {

    private LocationService.MyBinder mBinder;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (LocationService.MyBinder) service;
            mBinder.setLocationListener(RunActivity.this);
            Log.w("RunActivity", "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
            Log.w("RunActivity", "onServiceDisconnected: ");
        }
    };

    private Intent mIntent;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private TextView mSpeed;
    private TextView mDistance;
    private TextView mDuration;

    private TextView mStart;
    private TextView mCountDown;
    private TextView mPause;
    private TextView mResume;
    private TextView mStop;
    private ImageButton mLock;
    private ImageButton mCircleLock;
    private LinearLayout mDataLayout;

    private LinearLayout mFinishLayout;
    private TextView mFinishDate;
    private TextView mFinishDuration;
    private TextView mFinishDescription;
    private ChartView mChartView;
    private TextView mDetailDuration;
    private TextView mDetailDurationTotal;
    private TextView mDetailDistance;
    private TextView mDetailDistanceTotal;
    private TextView mDetailCalorie;
    private TextView mDetailSpeed;
    private TextView mDetailSpeedMax;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        MapView.setMapCustomEnable(true);
        setMapCustomFile(this);
        setContentView(R.layout.activity_run);
        initMap();
//        initView();

        mIntent = new Intent(RunActivity.this, LocationService.class);
        startService(mIntent);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    private void initMap() {
        mMapView = (MapView) findViewById(R.id.map_view);
        mMapView.showScaleControl(false);  // 不显示地图上比例尺
        mMapView.showZoomControls(false);  // 不显示地图缩放控件（按钮控制栏）
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.showMapPoi(true);

        UiSettings uiSettings = mBaiduMap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setOverlookingGesturesEnabled(false);
        uiSettings.setCompassEnabled(false);
    }

    private void initView() {
//        mSpeed = (TextView) findViewById(R.id.speed);
//        mDistance = (TextView) findViewById(R.id.distance);
//        mDuration = (TextView) findViewById(R.id.duration);
//
//        mStart = (TextView) findViewById(R.id.start);
//        mCountDown = (TextView) findViewById(R.id.count_down);
//        mPause = (TextView) findViewById(R.id.pause_button);
//        mResume = (TextView) findViewById(R.id.resume);
//        mStop = (TextView) findViewById(R.id.stop);
//        mLock = (ImageButton) findViewById(R.id.lock_button);
//        mCircleLock = (ImageButton) findViewById(R.id.circle_lock);
//        mDataLayout = (LinearLayout) findViewById(R.id.data_layout);

//        mFinishLayout = (LinearLayout) findViewById(R.id.finish_layout);
//        mFinishDate = (TextView) findViewById(R.id.finish_date);
//        mFinishDuration = (TextView) findViewById(R.id.finish_duration);
//        mFinishDescription = (TextView) findViewById(R.id.finish_description);
//        mChartView = (ChartView) findViewById(R.id.chart);
//        mDetailDuration = (TextView) findViewById(R.id.detail_duration);
//        mDetailDurationTotal = (TextView) findViewById(R.id.detail_duration_total);
//        mDetailDistance = (TextView) findViewById(R.id.detail_distance);
//        mDetailDistanceTotal = (TextView) findViewById(R.id.detail_distance_total);
//        mDetailCalorie = (TextView) findViewById(R.id.detail_calorie);
//        mDetailSpeed = (TextView) findViewById(R.id.detail_speed);
//        mDetailSpeedMax = (TextView) findViewById(R.id.detail_speed_max);

//        mStart.setOnClickListener(this);
//        mPause.setOnClickListener(this);
//        mResume.setOnClickListener(this);
//        mStop.setOnClickListener(this);
//        mLock.setOnClickListener(this);
//        mCircleLock.setOnClickListener(this);
    }

    private void setMapCustomFile(Context context) {
        FileOutputStream out = null;
        InputStream inputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets().open("custom_config.txt");
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            moduleName = context.getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/" + "custom_config.txt");
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MapView.setCustomMapStylePath(moduleName + "/custom_config.txt");
    }

    private int count;
    private long mStartTime;

    private void countDown() {
        final CountDown c = new CountDown();
        count = 3;
        mStart.setClickable(false);
        c.start(new CountDown.Callback() {
            @Override
            public void call() {
                count--;
                mCountDown.setText(String.valueOf(count));
                Log.w("RunActivity", "call: " + count);
                if (count == 0) {
                    mCountDown.setText("GO!");
                } else if (count < 0) {
                    c.stop();
                    gone(mCountDown, mStart);
                    visible(mPause, mLock);
                    mBinder.start();
                    mStartTime = System.currentTimeMillis();
                    keepTime();
                }
            }
        });
    }

    private int time;
    private CountDown countDown;
    private CountDown.Callback callback = new CountDown.Callback() {
        @Override
        public void call() {
            time++;
            int h = time / 3600, m = time / 60 % 60, s = time % 60;
            String t = h + ":" + (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
            mDuration.setText(t);
        }
    };

    private void keepTime() {
        countDown.start(callback);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
//                if (mLocationClient.getLastKnownLocation().getRadius() > 20) {
//                    Toast.makeText(this, "等待定位", Toast.LENGTH_SHORT).show();
//                } else {
//                    mCountDown.setVisibility(View.VISIBLE);
//                    countDown(2);
//                }
                countDown = new CountDown();
                visible(mCountDown);
                countDown();
                mBinder.start();
                break;
            case R.id.pause_button:
                mBinder.pause();
                countDown.stop();
                visible(mResume, mStop);
                gone(mPause, mLock);
                break;
            case R.id.resume:
                countDown.start();
                gone(mResume, mStop);
                visible(mPause, mLock);
                mBinder.start();
                break;
            case R.id.stop:
                visible(mFinishLayout);
                setFinishLayoutData();
                gone(mStop, mResume, mDataLayout);
                unbindService(mConnection);
                stopService(mIntent);
                break;
            case R.id.lock_button:
//                gone(mPause, mLock);
//                visible(mCircleLock);
                break;
            case R.id.circle_lock:
                gone(mCircleLock);
                visible(mPause, mLock);
                break;
        }
    }

    private void setFinishLayoutData() {
        PathRecord record = mBinder.getPathRecord();
        int duration = record.getDuration();
        String distance = record.getDistance() + "km";
        String speed = record.getSpeed(duration) + "min/km";

//        mFinishDate.setText(record.getDate());
        mFinishDuration.setText(formatTime(duration));
        mFinishDescription.setText(distance + "\t" + speed);
//        mChartView.setPath(record.getSpeeds());
        mDetailDuration.setText(formatTime(duration));
        mDetailDurationTotal.setText(formatTime((int) (System.currentTimeMillis() - mStartTime) / 1000));
        mDetailDistance.setText(distance);
        mDetailDistanceTotal.setText("" + record.getTotalDistance() + "km");
//        mDetailCalorie.setText();
        mDetailSpeed.setText(speed);
//        mDetailSpeedMax.setText();
    }

    private String formatTime(int time) {
        int h = time / 3600, m = time / 60 % 60, s = time % 60;
        return h + ":" + (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s);
    }

    private void gone(View... views) {
        for (View view : views)
            view.setVisibility(View.GONE);
    }

    private void visible(View... views) {
        for (View view : views)
            view.setVisibility(View.VISIBLE);
    }

    private PolylineOptions mOptions;

    @Override
    public void onLocationSucceed(Location location) {
        setLocation(location);
        Point point = new Point(mMapView.getWidth() / 2, (mMapView.getHeight() + mDataLayout
                .getHeight()) / 2);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                .target(location.getLatLng()).targetScreen(point).zoom(19).build()));
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocation(location);
    }

    private void setLocation(Location location) {
        mBaiduMap.setMyLocationData(new MyLocationData.Builder().accuracy(location.getRadius())
                .latitude(location.getLatitude()).longitude(location.getLongitude()).build());
    }

    @Override
    public void onRunning(PathRecord record) {
        Log.w("RunActivity", "onRunning: " + record.getDistance());
        mBaiduMap.clear();
        mDistance.setText(record.getDistance());
        mSpeed.setText(record.getSpeed(time));
        drawRealTimePath(record);
    }

    private List<List<LatLng>> mRecords;

    private void drawRealTimePath(PathRecord record) {
        if (record == null)
            return;
        if (mRecords == null)
            mRecords = new ArrayList<>();
        if (mOptions == null)
            mOptions = new PolylineOptions().color(Color.BLUE).width(12);
        record.getAllPaths(mRecords);

        for (List<LatLng> path : mRecords) {
            if (path.size() > 1) {
                mBaiduMap.addOverlay(mOptions.points(path));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(mIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        unbindService(mConnection);
        Log.w("RunActivity", "onDestroy: ");
    }
}
