package com.domencai.runin.network;

import android.util.Log;

import com.domencai.runin.utils.LogUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Domen、on 2016/4/20.
 */
public class Network {

    private Network() {
    }

    private static class NetworkHelper {
        private static final Network INSTANCE = new Network();
    }

    /**
     * 通过静态内部类实现单例
     */
    public static Network getInstance() {
        return NetworkHelper.INSTANCE;
    }

//    private static final String mWorkBaseUrl = "http://123.59.81.126:80/";
    private static final String mWorkBaseUrl = "http://api.runin.everfun.me/";


    private static final String mCurrentUrl = mWorkBaseUrl;

    private Api mApi;

    private Interceptor mInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("User-Agent", "runin_client andriod 1.0")
                    .build();
            Log.w("Network", String.format("Sending request %s %n%s",
                    request.url(), request.headers()));

            long startNs = System.nanoTime();
            Response response = chain.proceed(request);
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();
            Log.w("Network", "intercept: <-- " + response.code() + ' ' + response.message() +
                    " (" + tookMs + "ms)\n" + response.headers());

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.buffer();
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null)
                charset = contentType.charset(Charset.forName("UTF-8"));
            if (contentLength != 0)
                LogUtils.printJson(buffer.clone().readString(charset));

            return response;
        }
    };
    private OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .addInterceptor(mInterceptor).build();
    private Converter.Factory mGsonConverterFactory = GsonConverterFactory.create();

    private CallAdapter.Factory mRxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();


    public Api getApi() {
        if (mApi == null) {
            synchronized (Network.class) {
                if (mApi == null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(mCurrentUrl)
                            .addConverterFactory(mGsonConverterFactory)
                            .addCallAdapterFactory(mRxJavaCallAdapterFactory)
                            .client(mOkHttpClient)
                            .build();
                    mApi = retrofit.create(Api.class);
                }
            }
        }
        return mApi;
    }
}
