package com.domencai.runin.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by lenovo on 2016/12/10.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String uri,RequestBody requestBody,String token,okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request;
        if (token.equals("")){
            request = new Request.Builder().url(uri)
                    .header("User-Agent", "runin_client andriod 1.0")
                    .post(requestBody).build();
        }else {
            request = new Request.Builder().url(uri)
                    .header("User-Agent", "runin_client andriod 1.0")
                    .header("token",token)
                    .post(requestBody).build();
        }
        client.newCall(request).enqueue(callback);
    }

}
