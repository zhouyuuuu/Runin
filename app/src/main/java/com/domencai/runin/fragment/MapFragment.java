package com.domencai.runin.fragment;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.domencai.runin.bean.Location;
import com.domencai.runin.bean.PathRecord;
import com.domencai.runin.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Domen、on 2016/12/7.
 */

public class MapFragment extends SupportMapFragment {

    private BaiduMap mMap;
    private MapView mMapView;
    private PolylineOptions mOptions;
    private List<List<LatLng>> mRecords;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View v = super.onCreateView(layoutInflater, viewGroup, bundle);
        mMapView = getMapView();
        mMapView.setLogoPosition(LogoPosition.logoPostionleftTop);
        mMapView.showScaleControl(false);
        mMapView.setPadding(0, 0, 0, ScreenUtils.dp2px(getActivity(), 64));

        mMap = getBaiduMap();
        mMap.setMyLocationEnabled(true);
        mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(
                new MapStatus.Builder()
                        .target(new LatLng(23.049952, 113.394371))
                        .zoom(18)
                        .build()));
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setOverlookingGesturesEnabled(false);
        uiSettings.setCompassEnabled(false);
        return v;
    }

    public void setScrollGesturesEnabled(boolean enabled) {
        mMap.getUiSettings().setScrollGesturesEnabled(enabled);
    }

    public void setLocation(Location location) {
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.setMyLocationData(new MyLocationData.Builder().accuracy(location.getRadius())
                .latitude(location.getLatitude()).longitude(location.getLongitude()).build());
    }

    public void setLocationWithAnimation(Location location) {
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        Point point = new Point(mMapView.getWidth() / 2, (int) (mMapView.getHeight() * 0.6));
        mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                .target(location.getLatLng()).targetScreen(point).build()));
        mMap.setMyLocationData(new MyLocationData.Builder().accuracy(location.getRadius())
                .latitude(location.getLatitude()).longitude(location.getLongitude()).build());
    }

    public void drawRealTimePath(PathRecord record) {
        if (record == null)
            return;
        if (mRecords == null)
            mRecords = new ArrayList<>();
        if (mOptions == null)
            mOptions = new PolylineOptions().color(Color.BLUE).width(12);
        record.getAllPaths(mRecords);

        mMap.clear();
        for (List<LatLng> path : mRecords) {
            if (path.size() > 1) {
                mMap.addOverlay(mOptions.points(path));
            }
        }
    }

    public void drawFinishPath(List<Integer> colors, List<LatLng> path) {
        if (colors == null || colors.isEmpty() || path == null || path.size() < 2)
            return;
        mMap.clear();
        int width = (int) (ScreenUtils.getScreenWidth(getActivity()) / 80f);
        mMap.addOverlay(new PolylineOptions().width(width).colorsValues(colors).points(path));
        zoomToSpan(path);
    }

    private void zoomToSpan(List<LatLng> path) {
        if (mMap == null) {
            return;
        }
        if (path.size() < 2) {
            Toast.makeText(getActivity(), "小于2", Toast.LENGTH_SHORT).show();
            return;
        }
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng ll: path) {
            builder.include(ll);
        }
        LatLngBounds bound = builder.build();
        LatLng ne = bound.northeast;
        LatLng sw = bound.southwest;
        double newNorth = ne.latitude * 2 - sw.latitude;
        LatLng l = new LatLng(newNorth, ne.longitude);
        builder.include(l);
        mMap.animateMapStatus(MapStatusUpdateFactory
                .newLatLngBounds(builder.build()));

        double lat = ne.latitude - sw.latitude;
        double lng = ne.longitude - sw.longitude;
        LatLng limitNe = new LatLng(ne.latitude + 6*lat, ne.longitude+ 3*lng);
        LatLng limitSw = new LatLng(ne.latitude - 6*lat, sw.longitude- 3*lng);
        mMap.setMapStatusLimits(builder.include(limitNe).include(limitSw).build());

        mMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {


//                List<LatLng> pts = new ArrayList<>();
//                pts.add(limitNe);
//                pts.add(new LatLng(limitNe.latitude, limitSw.longitude));
//                pts.add(limitSw);
//                pts.add(new LatLng(limitSw.latitude, limitNe.longitude));
//                OverlayOptions polygonOption = new PolygonOptions()
//                        .points(pts)
//                        .fillColor(0x08000000);
//                mMap.addOverlay(polygonOption);
//                drawOverlay();
            }
        });
    }
}
