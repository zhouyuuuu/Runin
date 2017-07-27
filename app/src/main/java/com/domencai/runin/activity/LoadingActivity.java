package com.domencai.runin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.domencai.runin.R;
import com.domencai.runin.bean.Account;
import com.domencai.runin.bean.User;
import com.domencai.runin.utils.DbHelper;
import com.domencai.runin.utils.HttpUtil;
import com.domencai.runin.utils.JsonUtil;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoadingActivity extends AppCompatActivity {

    public static User globalUser;
    public static Account globalAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Connector.getDatabase();
        //loading图片显示2秒
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //直接使用数据库中最后一个account作为上一次登录的account
                globalAccount = DataSupport.findLast(Account.class);
                if (globalAccount == null) {
//                    当前无账户记录则进入登录界面
                    Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String token_expire_time = globalAccount.getToken_expire_time();
                    SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA);
                    Date targetDate = new Date();
                    Date todayDate = new Date();
                    targetDate.setTime(Long.valueOf(token_expire_time));
                    token_expire_time = toDate.format(targetDate);
                    String today = toDate.format(todayDate);
                    double diffVal = ((targetDate.getTime() - todayDate.getTime() * 0.01 * 100) / (1000 * 60 * 60 * 24));
                    Log.d("LoadingActivity", "token_expire_time:" + token_expire_time);
                    Log.d("LoadingActivity", "today:" + today);
                    Log.d("LoadingActivity", "token_expire_time-today:" + diffVal);
                    if (diffVal >= 0) {//表示过期时间大于今天，即token还没过期
                        //当前登录用户token还没过期则直接进入主界面
                        //token延期网络请求
                        String uri = "http://api.runin.everfun.me/account/token/keep_alive";
                        RequestBody requestBody = new FormBody.Builder().build();
                        Log.d("LoadingActivity", uri + requestBody.toString());
                        HttpUtil.sendOkHttpRequest(uri, requestBody, globalAccount.getToken(), new okhttp3.Callback() {

                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("LoadingActivity", "token延期onFailure");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData = response.body().string();
                                    Log.d("LoadingActivity", "responseData:" + responseData);
                                    String result = JsonUtil.handleNoReturnResponse(responseData);
                                    Log.d("LoadingActivity", "LoadingActivity result:"+result);
//                                        DbHelper.updateAccount(globalAccount);
                                }
                            }
                        );
                        globalUser = DataSupport.findLast(User.class);
                        if (globalUser!=null) {
                            String sync_at = globalUser.getSync_at();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                            String updateTime = sdf.format(new Date());//每天的更新时间
                            updateTime = updateTime + " 04:00:00 000";
                            Log.d("LoadingActivity", "updateTime=" + updateTime);
                            Date sync_date = null;
                            Date update_date = null;
                            try {
                                Log.d("LoadingActivity", "sync_at:" + sync_at);
                                sync_date = toDate.parse(sync_at);
                                update_date = toDate.parse(updateTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            diffVal = 0;
                            if (sync_date != null && update_date != null) {
                                Log.d("LoadingActivity", "sync_date.getTime():" + sync_date.getTime());
                                Log.d("LoadingActivity", "today_date.getTime():" + update_date.getTime());
                                diffVal = ((sync_date.getTime() - update_date.getTime() * 0.01 * 100) / (1000 * 60 * 60 * 24));

                            }
                            Log.d("LoadingActivity", "diffVal=" + diffVal);
                            if (diffVal <= 0) {//sync_at过期
                                Log.d("LoadingActivity", "diffVal <= 0");
                                uri = "http://api.runin.everfun.me/user/profile/get_my_info";
                                requestBody = new FormBody.Builder().build();
                                Log.d("LoadingActivity", uri + requestBody.toString());
                                HttpUtil.sendOkHttpRequest(uri, requestBody, globalAccount.getToken(), new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Log.d("LoadingActivity", "response onFailure");
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String responseData = response.body().string();
                                            Log.d("LoadingActivity", "response info:" + response);
                                            Log.d("LoadingActivity", "responseData info:" + responseData);
                                            User syncUser = JsonUtil.handleUserInfoResponse(responseData);
                                            int result = DbHelper.updateUser(syncUser);
                                            if (result > 0) {
                                                Log.d("LoadingActivity", "updateUser success");
                                            } else {
                                                Log.d("LoadingActivity", "updateUser fail");
                                            }
                                        }
                                    }
                                );
                            } else {//sync_at未过期
                                Log.d("LoadingActivity", "diffVal>0");
                            }
                        }
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //token过期则进入登录界面
                        Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }, 2000);
        /**
         * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
             String d1 = sdf.format(new Date());//第一个时间
             String d2 = sdf.format(new Date());//第二个时间
             System.out.println(d1.equals(d2));//判断是否是同一天
         */
    }
}
