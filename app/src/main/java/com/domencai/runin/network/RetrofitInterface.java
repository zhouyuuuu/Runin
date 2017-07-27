package com.domencai.runin.network;



import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface RetrofitInterface {
    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/account/login/phone")
    Call<ResponseBody> onLoading(@Body RequestBody requestBody);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/sms/captcha/send")
    Call<ResponseBody> onSendMes(@Body RequestBody requestBody);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/account/token/info")
    Call<ResponseBody> onRequestToken(@Header("Token") String token);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/account/password/set")
    Call<ResponseBody> onSetPassword(@Header("Token") String token,
                                     @Body RequestBody requestBody);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/account/token/keep_alive")
    Call<ResponseBody> onTokenKeepAlive(@Header("Token") String token);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/account/logout")
    Call<ResponseBody> onLogout(@Header("Token") String token);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/user/profile/get_my_info")
    Call<ResponseBody> onGetUserInfo(@Header("Token") String token);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/user/profile/set_nickname")
    Call<ResponseBody> onSetNickname(@Header("Token") String token,
                                     @Body RequestBody requestBody);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/user/profile/set_health_info")
    Call<ResponseBody> onSetHealthInfo(@Header("Token") String token,
                                       @Body RequestBody requestBody);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/user/profile/set_user_info")
    Call<ResponseBody> onSetUserIntro(@Header("Token") String token,
                                      @Body RequestBody requestBody);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: image/png",
            "Content-Length: 0"})
    @Multipart
    @POST("/user/profile/set_avatar")
    Call<ResponseBody> onSetAvatar(@Header("Token") String token,
                                   @Part MultipartBody.Part part);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/running/start")
    Call<ResponseBody> onRunningStart(@Header("Token") String token,
                                      @Body RequestBody requestBody);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/running/finish")
    Call<ResponseBody> onRunningFinish(@Header("Token") String token,
                                       @Body RequestBody requestBody);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/running/finish/unfinished")
    Call<ResponseBody> onFinishUnfinished(@Header("Token") String token,
                                          @Body RequestBody requestBody);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type:application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/running/my_list")
    Call<ResponseBody> onGetRunningList(@Header("Token") String token,
                                        @Body RequestBody requestBody);

    @Headers({
            "Host: api.runin.everfun.me",
            "Connection: keep-alive",
            "User-Agent: runin_client andriod 1.0",
            "Content-Type:application/x-www-form-urlencoded",
            "Content-Length: 0"})
    @POST("/running/detail")
    Call<ResponseBody> onGetRunningDetail(@Header("Token") String token,
                                          @Body RequestBody requestBody);
}
