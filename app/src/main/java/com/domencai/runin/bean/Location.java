package com.domencai.runin.bean;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by Domen、on 2016/11/21.
 */

public class Location {
    private LatLng mLatLng;
    private float mRadius;
    public long time;

    public Location(BDLocation bdLocation) {
        mLatLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
        mRadius = bdLocation.getRadius();
    }

    public Location(String s) {
        String[] l = s.split(",");
        mLatLng = new LatLng(Double.parseDouble(l[0].substring(l[0].indexOf(":") + 1)),
                Double.parseDouble(l[1].substring(l[1].indexOf(":") + 1)));
        time = Long.parseLong(l[2].substring(l[2].indexOf(":") + 1));
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public float getRadius() {
        return mRadius;
    }

    public double getLatitude() {
        return mLatLng.latitude;
    }

    public double getLongitude() {
        return mLatLng.longitude;
    }

    @Override
    public String toString() {
        return "lat:" + getLatitude() + ",lng:" + getLongitude() + ",time:" + time + ";";
    }
}
