package com.domencai.runin.utils;

import android.util.SparseArray;

import com.baidu.mapapi.utils.DistanceUtil;
import com.domencai.runin.bean.Location;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Domen、on 2016/12/26.
 */

public class SpeedUtils {

//    public static List<Integer> getPathColors(List<LatLng> latLngs) {
//        List<Location> locations = new ArrayList<>();
//        String[] strings = Test.nei.split(";");
//        for (String s : strings) {
//            Location location = new Location(s);
//            locations.add(location);
//            latLngs.add(location.getLatLng());
//        }
////        float avg = dis / (locations.get(size - 1).time - locations.get(0).time) * 1000;
//        return getPathColor(locations);
//    }

    public static List<Integer> getPathColor(List<Location> locations, float avg) {
        if (locations == null || locations.size() < 2)
            return null;
//        StringBuilder builder = new StringBuilder();
//        for (Location location : locations) {
//            builder.append(location).append("\n");
//        }
//        LogUtils.printFile(builder.toString());
        float min = 10, max = 0, dis = 0;
        int size = locations.size(), point = 1, lastIndex = 0;
        long lastTime = locations.get(0).time;
        SparseArray<Float> array = new SparseArray<>();
        for (int i = 0; i < size - 1; i++) {
            dis += (float) DistanceUtil.getDistance(locations.get(i).getLatLng(),
                    locations.get(i + 1).getLatLng());
            if (dis > 40 * point) {
                long time = locations.get(i + 1).time;
                float speed = (dis - (point - 1) * 40) / (time - lastTime) * 1000;
                array.append((i + lastIndex) / 2, speed);
                lastIndex = i;
                if (speed > max)
                    max = speed;
                else if (speed < min)
                    min = speed;

                point++;
                lastTime = locations.get(i).time;
            }
        }
        float speed = (dis - (point - 1) * 40) / (locations.get(size - 1).time - lastTime) * 1000;
        array.append((size + lastIndex) / 2, speed);

//        float avg = dis / (locations.get(size - 1).time - locations.get(0).time) * 1000;
        float high = max - avg, low = avg - min;
        int lastColor = getColor(array.valueAt(0), avg, high, low);

        List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < array.keyAt(0); i++) {
            list.add(lastColor);
        }
        for (int i = 0; i < array.size() - 1; i++) {
            float prevSpeed = array.valueAt(i), nextSpeed = array.valueAt(i + 1);
            int len = array.keyAt(i + 1) - array.keyAt(i);
            for (int j = 0; j < len; j++) {
                float s = prevSpeed + (nextSpeed - prevSpeed) * j / len;
                list.add(getColor(s, avg, high, low));
            }
        }
        return list;
    }

    private static int getColor(float speed, float avg, float high, float low) {
        float f;
        int end;
        avg -= (avg - low) * 0.2f;
        if (speed < avg) {
            f = (avg - speed) / low;
            end = 0xFFFF0000;
        } else {
            f = (speed - avg) / high;
            end = 0xFF00FF00;
        }
        return getColor(f, 0xFFFFFF00, end);
    }

//    public static List<Color> getPathColor(List<Location> locations, float avg) {
//        List<Color> colors = new ArrayList<>(locations.size());
//        Color color;
//        float min = 10;
//        float max = 0;
//        StringBuilder sb = new StringBuilder();
//        sb.append(locations.get(0)).append("\n");
//        for (int i = 0; i < locations.size() - 2; i++) {
//            color = new Color();
//            color.setLatLngs(locations.get(i), locations.get(i + 1));
//            sb.append(locations.get(i+1).toString()).append("\n");
//            if (color.speed > max)
//                max = color.speed;
//            if (color.speed < min)
//                min = color.speed;
//            colors.add(color);
//        }
//
//        float high = max - avg;
//        float low = avg - min;
//
//        for (Color c : colors) {
//            float f;
//            int end;
//            if (c.speed < avg) {
//                f = (avg - c.speed) / low;
//                end = 0xFFFF0000;
//            } else {
//                f = (c.speed - avg) / high;
//                end = 0xFF00FF00;
//            }
//            c.color = getColor(f, 0xFFFFFF00, end);
//            sb.append(c);
//        }
//        LogUtils.printFile(sb.toString());
//        return colors;
//    }

    private static int getColor(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return (startA + (int)(fraction * (endA - startA))) << 24 |
                (startR + (int)(fraction * (endR - startR))) << 16 |
                (startG + (int)(fraction * (endG - startG))) << 8 |
                (startB + (int)(fraction * (endB - startB)));
    }

//    public static class Color {
//        public List<LatLng> mLatLngs;
//        public int color;
//        float speed;
//
//        void setLatLngs(Location first, Location second) {
//            mLatLngs = new ArrayList<>(2);
//            LatLng l1 = first.getLatLng();
//            LatLng l2 = second.getLatLng();
//            mLatLngs.add(l1);
//            mLatLngs.add(l2);
//            float d = (float) DistanceUtil.getDistance(l1, l2);
//            speed = d / (second.getTime() - first.getTime()) * 1000;
//        }
//
//        @Override
//        public String toString() {
//            return "speed:" + speed + ",color:" + color;
//        }
//    }
}
