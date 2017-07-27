package com.domencai.runin.network;



import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiUtil {
    /**
     * 登陆请求
     * @param phone_number
     * @param captcha
     */
    public static void onLogin(Handler mHandler,String phone_number,String captcha){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder().add("phone_number",phone_number).add("captcha",captcha).build();
        Call<ResponseBody> call = retrofitInterface.onLoading(requestBody);
        onCallEnqueue(mHandler,call);
    }

    /**
     * 验证码请求
     * @param phone_number
     */
    public static void onSms(Handler mHandler,String phone_number){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder().add("phone_number",phone_number).build();
        Call<ResponseBody> call = retrofitInterface.onSendMes(requestBody);
        onCallEnqueue(mHandler,call);
    }

    /**
     * Token过期时间
     * @param token
     */
    public static void onRequestToken(Handler mHandler,String token){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        Call<ResponseBody> call = retrofitInterface.onRequestToken(token);
        onCallEnqueue(mHandler,call);
    }

    /**
     * 修改密码请求
     * @param token
     * @param old_password
     * @param new_password
     */
    public static void onSetPassword(Handler mHandler,String token,String old_password,String new_password){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder().add("old_password",old_password).add("new_password",new_password).build();
        Call<ResponseBody> call = retrofitInterface.onSetPassword(token,requestBody);
        onCallEnqueue(mHandler,call);
    }

    /**
     * 延长Token到期时间
     * @param token
     */
    public static void onTokenKeepAlive(Handler mHandler,String token){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        Call<ResponseBody> call = retrofitInterface.onTokenKeepAlive(token);
        onCallEnqueue(mHandler,call);
    }

    public static void onLogout(Handler mHandler,String token){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        Call<ResponseBody> call = retrofitInterface.onLogout(token);
        onCallEnqueue(mHandler,call);
    }

    public static void onGetUserInfo(Handler mHandler,String token){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        Call<ResponseBody> call = retrofitInterface.onGetUserInfo(token);
        onCallEnqueue(mHandler,call);
    }

    public static void onSetNickname(Handler mHandler,String token,String new_nickname){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder().add("nickname",new_nickname).build();
        Call<ResponseBody> call = retrofitInterface.onSetNickname(token,requestBody);
        onCallEnqueue(mHandler,call);
    }

    public static void onSetHealthInfo(Handler mHandler,String token,String gender,String height,String weight,String birthday){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder().add("gender",gender).add("height",height).add("weight",weight).add("birthday",birthday).build();
        Call<ResponseBody> call = retrofitInterface.onSetHealthInfo(token,requestBody);
        onCallEnqueue(mHandler,call);
    }

    public static void onSetUserIntro(Handler mHandler,String token,String Intro){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder().add("intro",Intro).build();
        Call<ResponseBody> call = retrofitInterface.onSetUserIntro(token,requestBody);
        onCallEnqueue(mHandler,call);
    }

    public static void onSetAvatar(Handler mHandler, String token, Bitmap bitmap){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Log.e("---------------------",byteArray+"");
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),byteArray);
        MultipartBody.Part imageBodyPart = MultipartBody.Part.createFormData("avatar", "123.png", requestBody);
        Call<ResponseBody> call = retrofitInterface.onSetAvatar(token,imageBodyPart);
        onCallEnqueue(mHandler,call);
    }

    public static void onRunningStart(Handler mHandler,String token,double latitude,double longitude){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder().add("lon",longitude+"").add("lat",latitude+"").build();
        Call<ResponseBody> call = retrofitInterface.onRunningStart(token,requestBody);
        onCallEnqueue(mHandler,call);
    }

    public static void onRunningFinish(Handler mHandler,String token,double latitude,double longitude,String record_id,String start_time,String end_time,String pause_time,String len,String max_speed,String kilo_calorie,String path){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder()
                .add("lon",longitude+"")
                .add("lat",latitude+"")
                .add("running_record_id",record_id)
                .add("start_time",start_time)
                .add("end_time",end_time)
                .add("pause_time",pause_time)
                .add("len",len)
                .add("max_speed",max_speed)
                .add("kilo_calorie",kilo_calorie)
                .add("path",path)
                .build();
        Call<ResponseBody> call = retrofitInterface.onRunningFinish(token,requestBody);
        onCallEnqueue(mHandler,call);
    }

    public static void onFinishUnfinished(Handler mHandler,String token,String record_id){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder().add("running_record_id",record_id).build();
        Call<ResponseBody> call = retrofitInterface.onFinishUnfinished(token,requestBody);
        onCallEnqueue(mHandler,call);
    }

    public static void onGetRunningList(Handler mHandler,String token,String page,String count,String order){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder().add("page",page).add("count",count).add("order",order).build();
        Call<ResponseBody> call = retrofitInterface.onGetRunningList(token,requestBody);
        onCallEnqueue(mHandler,call);
    }

    public static void onGetRunningDetail(Handler mHandler,String token,String record_id){
        RetrofitInterface retrofitInterface = getRetrofitInterfaceInstance();
        RequestBody requestBody = new FormBody.Builder().add("running_record_id",record_id).build();
        Call<ResponseBody> call = retrofitInterface.onGetRunningDetail(token,requestBody);
        onCallEnqueue(mHandler,call);
    }

    /*************************************************************************************/

    /**
     * 生成RetrofitInterface接口实例
     * @return
     */
    private static RetrofitInterface getRetrofitInterfaceInstance(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.runin.everfun.me/")
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                //增加返回值为Oservable<T>的支持
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);//这里采用的是Java的动态代理模式
        return retrofitInterface;
    }

    /**
     * call执行异步请求
     * @param call
     */
    private static void onCallEnqueue(final Handler mHandler, Call<ResponseBody> call){
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    System.out.println(response.body().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                String jsonString = null;
                try {
                    jsonString = new String(response.body().bytes(), "utf-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("Upload jsonString", jsonString);
                Log.e("===","return:"+ response.raw().toString());
                Message message = new Message();
                message.obj = response.message();
                mHandler.sendMessage(message);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("===","失败");
            }
        });
    }
}
