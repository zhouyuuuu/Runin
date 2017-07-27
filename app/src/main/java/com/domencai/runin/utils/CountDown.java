package com.domencai.runin.utils;

import android.os.Handler;

/**
 * Created by Domen、on 2016/11/18.
 *
 */

public class CountDown {
    private boolean start;
    private Handler mHandler;
    private Callback mCallback;
    private Runnable mRunnable;

    public CountDown() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (start) {
                    mHandler.postDelayed(this, 1000);
                    if (mCallback != null)
                        mCallback.call();
                }
            }
        };
    }

    public void start() {
        start(null);
    }

    public void start(Callback callback) {
        if (callback != null)
            mCallback = callback;
        start = true;
        mHandler.postDelayed(mRunnable, 1000);
    }

    public void stop() {
        start = false;
    }

    public interface Callback {
        void call();
    }
}
