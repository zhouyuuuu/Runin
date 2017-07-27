package com.domencai.runin.listener;

import com.domencai.runin.bean.Location;
import com.domencai.runin.bean.PathRecord;

/**
 * Created by Domen、on 2016/11/18.
 */

public interface LocationListener {
    void onLocationSucceed(Location location);

    void onLocationChanged(Location location);

    void onRunning(PathRecord record);
}
