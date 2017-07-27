package com.domencai.runin.utils;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Domen、on 2016/12/7.
 */

public class MapViewUtil {
    public static void setMapCustomFile(Context context) {
        MapView.setMapCustomEnable(true);
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

    public static void zoomToSpan(final BaiduMap map, List<LatLng> path) {
        if (map == null) {
            return;
        }
        if (path.size() < 2) {
            return;
        }
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng ll : path) {
            builder.include(ll);
        }
        map.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds bound = builder.build();
                LatLng ne = bound.northeast;
                LatLng sw = bound.southwest;
                double newNorth = ne.latitude * 2 - sw.latitude;
                LatLng l = new LatLng(newNorth, ne.longitude);
                builder.include(l);
                map.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));

                double lat = ne.latitude - sw.latitude;
                double lng = ne.longitude - sw.longitude;
                LatLng limitNe = new LatLng(ne.latitude + 6 * lat, ne.longitude + 3 * lng);
                LatLng limitSw = new LatLng(ne.latitude - 6 * lat, sw.longitude - 3 * lng);
                map.setMapStatusLimits(builder.include(limitNe).include(limitSw).build());

//                List<LatLng> pts = new ArrayList<>();
//                pts.add(limitNe);
//                pts.add(new LatLng(limitNe.latitude, limitSw.longitude));
//                pts.add(limitSw);
//                pts.add(new LatLng(limitSw.latitude, limitNe.longitude));
//                OverlayOptions polygonOption = new PolygonOptions()
//                        .points(pts)
//                        .stroke(new Stroke(1, 0x00000000))
//                        .fillColor(0x44000000);
//                map.addOverlay(polygonOption);
            }
        });
    }
}
