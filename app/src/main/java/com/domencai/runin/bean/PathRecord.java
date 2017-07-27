package com.domencai.runin.bean;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 用于记录一条轨迹，包括起点、终点、轨迹中间点、距离、耗时、平均速度、时间
 * 已知体重、速度和时间
 * 跑步热量（kCal）＝体重（kg）×运动时间（分钟）×指数K
 * 一小时8公里 K＝0.1355
 * 一小时12公里 K＝0.1797
 * 一小时15公里 K＝0.1875
 * Created by Domen、on 2016/11/10.
 */

public class PathRecord {

    private final List<LatLng> mPath;
    private final List<Integer> mPause;
    private final String date;
    private float mDistance;
    private float mTotalDistance;
    private long mLastStartTime;
    private long mLastTime;
    private float mCal;
    private long mTotalTime;
    private LatLng lastPoint;

    public PathRecord() {
        mPath = new ArrayList<>();
        mPause = new ArrayList<>();
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        date = formatter.format(currentTime);
    }

    public void addPoint(LatLng latLng) {
        long current = System.currentTimeMillis();
        if (latLng == null) {
            if (lastPoint != null) {
                mPause.add(mPath.size());
                lastPoint = null;
                mTotalTime += (current - mLastStartTime);
            }
            return;
        }
        if (lastPoint == null) {
            mLastStartTime = current;
            lastPoint = latLng;
            mLastTime = current;
            if (mPath.size() != 0) {
                float distance = (float) DistanceUtil.getDistance(latLng, mPath.get(getPathSize() - 1));
                mTotalDistance += distance;
            }
            return;
        }
        float distance = (float) DistanceUtil.getDistance(lastPoint, latLng);
        if (isTheSame(lastPoint, latLng) || distance < 0.5)
            return;
        mPath.add(getMiddlePoint(lastPoint, latLng));
        mDistance += distance;
        mTotalDistance += distance;
        setCal(distance, (current - mLastTime) / 60000f);
        lastPoint = latLng;
        mLastTime = current;
    }

    private void setCal(float f, float time) {
        System.out.println("time:" + time);
        float speed = f * 3600;
        if (speed < 10) {
            mCal += 0.1355 * 66 * time;
        } else if (speed < 14) {
            mCal += 0.1797 * 66 * time;
        } else {
            mCal += 0.1875 * 66 * time;
        }
    }

    private LatLng getMiddlePoint(LatLng l1, LatLng l2) {
        double lat = (l1.latitude + l2.latitude) / 2;
        double lng = (l1.longitude + l2.longitude) / 2;
        return new LatLng(lat, lng);
    }

    private boolean isTheSame(LatLng l1, LatLng l2) {
        return l1 != null && l2 != null &&
                l1.longitude == l2.longitude && l1.latitude == l2.latitude;
    }

    public String getTotalDistance() {
        return String.format(Locale.getDefault(), "%.2f", mTotalDistance /1000);
    }

    public int getDuration() {
        return (int) (mTotalTime / 1000);
    }

    public String getTimeString() {
        int time = getDuration();
        Log.w("PathRecord", "getTimeString: " + time);
        int m = time / 60, s = time % 60;
        return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s) + " min";
    }

    public float getDis() {
        return mDistance;
    }

    public String getDistance() {
        return String.format(Locale.getDefault(), "%.2f", mDistance /1000);
    }

    public String getCal() {
        return String.valueOf((int)mCal);
    }

    public String getSpeed() {
        return getSpeed((int) mTotalTime);
    }

    public String getSpeed(int time) {
        int speed = mDistance == 0 ? 0 : (int) (time * 1f / mDistance);
        int m = speed / 60, s = speed % 60;
        return (m > 9 ? m : "0" + m) + "\'" + (s > 9 ? s : "0" + s) + "\"";
    }

    public List<LatLng> getLastPath() {
        if (mPause.size() == 0)
            return mPath;
        else
            return mPath.subList(mPause.get(mPause.size() - 1), mPath.size());
    }

    public void getAllPaths(List<List<LatLng>> allPaths) {
        if (allPaths == null)
            return;
        allPaths.clear();
        int start = 0;
        for (int i = 0; i < mPause.size();i ++) {
            allPaths.add(mPath.subList(start, mPause.get(i)));
            start = mPause.get(i);
        }
        allPaths.add(mPath.subList(start, mPath.size()));
    }

    public List<LatLng> getPath() {
        return mPath;
    }

    private int getPathSize() {
        return mPath.size();
    }
}
