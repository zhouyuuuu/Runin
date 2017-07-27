package com.domencai.runin.model;

import android.util.Log;

import com.domencai.runin.network.Network;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Domen、on 2016/12/14.
 */

public class AccountModel {
    public static void login(String number, String captcha) {
        Network.getInstance().getApi()
                .login(number, captcha)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResponseBody>() {
                    @Override
                    public void call(ResponseBody responseBody) {
                        try {
                            Log.w("StartActivity", "call: " + responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.w("StartActivity", "call: " + throwable);
                    }
                });
    }
}
