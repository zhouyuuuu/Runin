package com.domencai.runin.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.domencai.runin.R;
import com.domencai.runin.bean.Location;
import com.domencai.runin.bean.PathRecord;
import com.domencai.runin.custom.DataView;
import com.domencai.runin.custom.LockView;
import com.domencai.runin.custom.RulerView;
import com.domencai.runin.fragment.MapFragment;
import com.domencai.runin.listener.LocationListener;
import com.domencai.runin.service.LocationService;
import com.domencai.runin.utils.GPSUtils;
import com.domencai.runin.utils.MapViewUtil;
import com.domencai.runin.utils.ScreenUtils;

/**
 * Created by Domen、on 2016/12/5.
 */

public class StartActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnTouchListener, LocationListener{

    public final static int STATE_START = 0;
    public final static int STATE_RUN = 1;
    public final static int STATE_PAUSE = 2;
    public final static int STATE_STOP = 3;
    public final static int STATE_LOCK = 4;

    private LinearLayout mStartLayout;
    private FrameLayout mDataLayout;
    private TextView mDistanceButton;
    private TextView mDurationButton;
    private TextView mCalorieButton;
    private TextView mSelectText;
    private TextView mUnitText;
    private RelativeLayout mSelector;
    private TextView mCurrentSelect;
    private RulerView mRulerView;
    private TextView mStart;
    private TextView mScroll;
    private LinearLayout mBottomLayout;
    private LinearLayout mPauseLayout;
    private TextView mResume;
    private TextView mStop;
    private TextView mCountDown;
    private FrameLayout mLockLayout;
    private LockView mLockView;
    private DataView mDataView;
    private LinearLayout mFinishLayout;
    private DataView mFinishData;

    private MapFragment mMap;
    private float mDownX;
    private int mWidth;

    private int mState;

    private LocationService.MyBinder mBinder;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (LocationService.MyBinder) service;
            mBinder.setLocationListener(StartActivity.this);
            Log.w("RunActivity", "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
            Log.w("RunActivity", "onServiceDisconnected: ");
        }
    };

    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SDKInitializer.initialize(getApplicationContext());
        MapViewUtil.setMapCustomFile(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
        initListener();
        initMap();
        mWidth = ScreenUtils.getScreenWidth(this);
        mIntent = new Intent(StartActivity.this, LocationService.class);
        startService(mIntent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        if (!GPSUtils.isOPen(this) ) {
            GPSUtils.openGPSSettings(this);
        }
    }

    private void initView() {
        mStartLayout = (LinearLayout) findViewById(R.id.start_layout);
        mDataLayout = (FrameLayout) findViewById(R.id.data_layout);
        mDistanceButton = (TextView) findViewById(R.id.distance_button);
        mDurationButton = (TextView) findViewById(R.id.duration_button);
        mCalorieButton = (TextView) findViewById(R.id.calorie_button);
        mSelectText = (TextView) findViewById(R.id.select_text);
        mUnitText = (TextView) findViewById(R.id.unit_text);
        mSelector = (RelativeLayout) findViewById(R.id.selector);
        mRulerView = (RulerView) findViewById(R.id.ruler);
        mStart = (TextView) findViewById(R.id.start);
        mScroll = (TextView) findViewById(R.id.scroll);
        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mPauseLayout = (LinearLayout) findViewById(R.id.pause_layout);
        mResume = (TextView) findViewById(R.id.resume);
        mStop = (TextView) findViewById(R.id.stop);
        mCountDown = (TextView) findViewById(R.id.count_down);
        mLockLayout = (FrameLayout) findViewById(R.id.lock_layout);
        mLockView = (LockView) findViewById(R.id.lock_view);
        mDataView = (DataView) findViewById(R.id.data_view);
        mFinishLayout = (LinearLayout) findViewById(R.id.finish_layout);
        mFinishData = (DataView) findViewById(R.id.finish_data);
    }

    private void initListener() {
        mSelector.setOnClickListener(this);
        mStart.setOnClickListener(this);
        mResume.setOnClickListener(this);
        mRulerView.setOnSelectChangedListener(new RulerView.OnSelectChangedListener() {
            @Override
            public void onSelectChanged(String num) {
                mSelectText.setText(num);
            }
        });
        mScroll.setOnTouchListener(this);
        mLockLayout.setOnTouchListener(this);
        mStop.setOnClickListener(this);
    }

    private void initMap() {
        FragmentManager manager = getSupportFragmentManager();
        mMap = (MapFragment) manager.findFragmentById(R.id.map_content);
        if (mMap == null) {
            mMap = MapFragment.newInstance();
            manager.beginTransaction().add(R.id.map_content, mMap, "map_fragment").commit();
        }
    }


    public void changeGoal(View view) {
        mCurrentSelect = (TextView) view;
        mSelector.setVisibility(View.VISIBLE);
        switch (view.getId()) {
            case R.id.distance_button:
                setColor(mDistanceButton, mDurationButton, mCalorieButton);
                mUnitText.setText("km");
                mRulerView.setRange(0.1f, 200);
                break;
            case R.id.duration_button:
                setColor(mDurationButton, mDistanceButton, mCalorieButton);
                mUnitText.setText("min");
                mRulerView.setRange(1, 120);
                break;
            case R.id.calorie_button:
                setColor(mCalorieButton, mDistanceButton, mDurationButton);
                mUnitText.setText("Cal");
                mRulerView.setRange(10, 100);
                break;
        }
    }

    private void setColor(TextView... views) {
        views[0].setTextColor(0xFF498EE0);
        views[1].setTextColor(0xFF757575);
        views[2].setTextColor(0xFF757575);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selector:
                mSelector.setVisibility(View.GONE);
                mCurrentSelect.setTextColor(0xFF757575);
                break;
            case R.id.start:
                checkGPS();
                break;
            case R.id.scroll:
                mStart.setScaleX(1);
                mStart.setScaleY(1);
                mStart.setTranslationY(0);
                mStart.setVisibility(View.VISIBLE);
                mBottomLayout.setVisibility(View.GONE);
                mScroll.setVisibility(View.GONE);
                break;
            case R.id.resume:
                mState = STATE_RUN;
                hidePauseLayout();
                break;
            case R.id.stop:
                mState = STATE_STOP;
                mBinder.getLocationClient().stop();
                mMap.setScrollGesturesEnabled(true);
                mPauseLayout.animate().scaleX(0).setDuration(250)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                gone(mPauseLayout, mDataLayout);
                                visible(mFinishLayout);
                                PathRecord record = mBinder.getPathRecord();
                                mFinishData.setRunType(DataView.FINISH);
                                String s = "d:" + record.getDuration() + ", " + record.getTimeString() + ", dis:" + record.getDistance() +
                                        ", speed:" + record.getSpeed() + ", " + record.getSpeed(record.getDuration());
                                mFinishData.setDatas(record.getDistance(), record.getSpeed(),
                                        record.getTimeString(), record.getCal());
                                mMap.drawFinishPath(mBinder.getPathColor(), mBinder.getPathRecord().getPath());
                                Toast.makeText(StartActivity.this, s, Toast.LENGTH_LONG).show();
                            }
                        }).start();
                mDataLayout.animate().translationY(-mDataLayout.getHeight()).setDuration(200).start();
                break;
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.w("StartActivity", "onResume: ");
//        showFinishData();
//    }

//    private void showFinishData() {
//        mFinishData.setRunType(DataView.FINISH);
//        mFinishData.setDatas("12.5", "04'56\"", "15:56", "358");
//        List<LatLng> path = new ArrayList<>();
////        List<Integer> colors = SpeedUtils.getPathColors(path);
//        Log.w("StartActivity", "showFinishData: " + path.size());
////        Log.w("StartActivity", "showFinishData: " + colors.size());
////        mMap.drawFinishPath(colors, path);
//    }

    private void checkGPS() {
        final BDLocation b = mBinder.getLastKnowLocation();
        if (b.getGpsAccuracyStatus() == BDLocation.GPS_ACCURACY_GOOD) {
            countDown(3);
            return;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.bad_GPS)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        countDown(3);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void countDown(final int num) {
        if (num == 3) {
            hideStartLayout();
        }
        mCountDown.setVisibility(View.VISIBLE);
        mCountDown.setText(num == 0 ? "GO" : num + "");
        int time = num == 0 ? 500 : 900;
        mCountDown.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (num == 0) {
                    mCountDown.setVisibility(View.GONE);
                    showStartAnimation();
                    showDataLayout();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    mDataView.setDate(System.currentTimeMillis());
                    mState = STATE_RUN;
                    mBinder.start();
                } else {
                    countDown(num - 1);
                }
            }
        }, time);
    }

    private void hideStartLayout() {
        mStartLayout.animate().translationY(-mStartLayout.getHeight()).setDuration(300).start();
        mDataLayout.setVisibility(View.VISIBLE);
        mDataLayout.setAlpha(0);
    }

    private void showDataLayout() {
        mDataLayout.setAlpha(1);
        mDataLayout.setTranslationY(-mDataLayout.getHeight());
        mStartLayout.setVisibility(View.GONE);
        mDataLayout.animate().translationY(0).setDuration(400).start();
    }

    private void showStartAnimation() {
        mBottomLayout.setVisibility(View.VISIBLE);
        int goal = ScreenUtils.dp2px(this, 80);
        float xOffset = ScreenUtils.dp2px(this, 80) * 1f / mStart.getWidth();
        mBottomLayout.setAlpha(0);
        mBottomLayout.setScaleX(goal*1f /ScreenUtils.getScreenWidth(this));
        ViewCompat.animate(mStart)
                .scaleX(xOffset)
                .scaleY(56f / 40)
                .translationY(ScreenUtils.dp2px(this, 48))
                .setDuration(250)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mStart.setVisibility(View.GONE);
                        mScroll.setVisibility(View.VISIBLE);
                        ViewCompat.animate(mBottomLayout)
                                .alpha(1)
                                .scaleX(1)
                                .setDuration(300)
                                .start();
                    }
                })
                .start();
    }

    private void hidePauseLayout() {
        mBinder.start();
        mDataView.setDate(System.currentTimeMillis());
        visible(mScroll, mBottomLayout);
        mPauseLayout.animate().scaleX(0).setDuration(250)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        gone(mPauseLayout);
                        mBottomLayout.animate().scaleX(1).setDuration(300).start();
                        mScroll.animate().alpha(1).scaleX(1).setDuration(300).start();
                    }
                }).start();
    }

    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.getParent().requestDisallowInterceptTouchEvent(true);
                mDownX = event.getRawX();
                if(v.getId() == R.id.scroll)
                    mBottomLayout.animate().alpha(0.4f).setDuration(250).start();
                break;
            case MotionEvent.ACTION_MOVE:
                float move = event.getRawX() - mDownX;
                if (v.getId() == R.id.lock_layout && move < 0)
                    move = 0;
                v.setTranslationX(move);
                break;
            case MotionEvent.ACTION_UP:
                if (v.getId() == R.id.scroll) {
                    int edge = (mWidth - mScroll.getWidth()) / 2;
                    mBottomLayout.animate().alpha(1).setDuration(200).start();
                    mScroll.animate().translationX(0).setDuration(200).start();
                    if (mDownX - event.getRawX() > edge) {
                        showLockLayout();
                    } else if (event.getRawX() - mDownX > edge) {
                        showPauseLayout();
                    }
                } else {
                    if (v.getTranslationX() > mWidth * 0.4f) {
                        v.animate().translationX(mWidth).setDuration(200)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        visible(mDataLayout, mBottomLayout, mScroll);
                                        gone(v);
                                    }
                                }).setInterpolator(new DecelerateInterpolator()).start();
                        mLockView.hide();
                        mState = STATE_RUN;
//                        mDataLayout.setAlpha(0);
//                        mDataLayout.setScaleX(0);
//                        mDataLayout.setScaleY(0);
//                        mBottomLayout.setAlpha(0);
//                        mScroll.setAlpha(0);
//                        mDataLayout.animate().scaleX(1).scaleY(1).setDuration(150).start();
//                        mBottomLayout.animate().alpha(1).setDuration(300).start();
//                        mScroll.animate().alpha(1).setDuration(300).start();
                    } else {
                        v.animate().translationX(0).setDuration(200).start();
                    }
                }
                break;
        }
        return true;
    }

    private void showLockLayout() {
        mState = STATE_LOCK;
        mLockView.setTotalTime(mDataView.getTotalTime());
        mLockView.setDatas(mDataView.getDatas());
        mLockLayout.setTranslationX(mWidth);
        visible(mLockLayout);
        mLockLayout.animate().translationX(0).setDuration(400).start();
        gone(mDataLayout, mBottomLayout, mScroll);
    }

    private void showPauseLayout() {
        mState = STATE_PAUSE;
        mBinder.pause();
        mDataView.pause();
        visible(mPauseLayout);
        mPauseLayout.setScaleX(0);
        mBottomLayout.animate().scaleX(0).setDuration(200)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        gone(mScroll, mBottomLayout);
                        ViewCompat.animate(mPauseLayout)
                                .scaleX(1)
                                .setDuration(350)
                                .start();
                    }
                }).start();
        mScroll.animate().alpha(0).scaleX(0).setDuration(200).start();
    }

    private void visible(View... views) {
        for (View v: views)
            v.setVisibility(View.VISIBLE);
    }

    private void gone(View... views) {
        for (View v: views)
            v.setVisibility(View.GONE);
    }

    @Override
    public void onLocationSucceed(Location location) {
        mMap.setLocationWithAnimation(location);
        mMap.setScrollGesturesEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mState == STATE_START || mState == STATE_PAUSE) {
            mMap.setLocation(location);
        } else {
            mMap.setLocationWithAnimation(location);
        }
    }

    @Override
    public void onRunning(PathRecord record) {
        mDataView.setOtherData(record.getDistance(), record.getSpeed(mDataView.getTotalTime()),
                record.getCal());
        mLockView.setDatas(mDataView.getDatas());
        mMap.drawRealTimePath(record);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(mIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }
}
