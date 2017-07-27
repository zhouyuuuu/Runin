package com.domencai.runin.network;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Domen、on 2016/4/20.
 */
public interface Api {
    /**********************************************************************************************/
    /**                              账号 Account                                                **/
    /**********************************************************************************************/

    /**
     * 手机登录/注册
     * @param num 电话号码
     * @param captcha 验证码
     * @return token: 用户的token
     *         token_expire_time: 用户的Token过期时间
     *         user_id: 用户ID
     *         no_password: 是否已经设置密码
     */
    @FormUrlEncoded
    @POST("account/login/phone")
    Observable<ResponseBody> login(
            @Field("phone_number") String num,
            @Field("captcha") String captcha);

    /**
     * 修改密码
     * @param oldPassword 原密码 （没有设置过密码的请留空，但是一定要传这个参数）
     * @param newPassword 新的密码
     * @return 无返回
     */
    @FormUrlEncoded
    @POST("account/password/set")
    Observable<ResponseBody> resetPassword(@Header("Token") String token,
            @Field("old_password") String oldPassword,
            @Field("new_password") String newPassword);

    /**
     * 获取Token信息
     * @return token: 用户的token
     *         token_expire_time: 用户的Token过期时间
     *         user_id: 用户ID
     */
    @POST("account/token/info")
    Observable<ResponseBody> getToken(@Header("Token") String token);

    /**
     * 延长Token有效期
     * @return 无返回
     */
    @POST("account/token/keep_alive")
    Observable<ResponseBody> keepAliveToken(@Header("Token") String token);

    /**
     * 退出登录 / 销毁token
     * @return 无返回
     */
    @POST("account/logout")
    Observable<ResponseBody> loginOut(@Header("Token") String token);




    /**********************************************************************************************/
    /**                              手机短信 SMS                                                 **/
    /**********************************************************************************************/

    /**
     * 发送短信验证码
     * @param number 手机号码
     * @return time: 服务器时间（用于同步验证码发送时间），距离 1970-01-01 00:00:00:000 的毫秒数
     */
    @FormUrlEncoded
    @POST("sms/captcha/send")
    Observable<ResponseBody> sendCaptcha(@Field("phone_number") String number);




    /**********************************************************************************************/
    /**                              用户 User                                                   **/
    /**********************************************************************************************/

    /**
     * 获取当前用户信息
     * @return _id 用户ID
     *         avatar: 头像文件名
     *         created_at: 用户创建时间
     *         exp: 经验
     *         level: 等级
     *         nickname: 昵称（假如有）
     *         gender: 性别（假如有）
     *         height: 身高
     *         weight: 体重
     *         birthday: 出生日期（Date格式）
     */
    @POST("user/profile/get_my_info")
    Observable<ResponseBody> getInfo(@Header("Token") String token);

    /**
     * 设置昵称
     * @param nickname 新的昵称
     * @return 无返回
     */
    @FormUrlEncoded
    @POST("user/profile/set_nickname")
    Observable<ResponseBody> setNickname(@Header("Token") String token,
            @Field("nickname") String nickname);

    /**
     * 更新健康资料
     * @param gender 性别（假如有）
     * @param height 身高
     * @param weight 体重
     * @param birthday 出生日期（Date格式）
     * @return 无返回
     */
    @FormUrlEncoded
    @POST("user/profile/set_health_info")
    Observable<ResponseBody> setHealthInfo(@Header("Token") String token,
            @Field("gender") String gender,
            @Field("height") String height,
            @Field("weight") String weight,
            @Field("birthday") String birthday);




    /**********************************************************************************************/
    /**                              跑步记录 RunningRecord                                       **/
    /**********************************************************************************************/

    /**
     * 开始跑步
     * @param lon 经度
     * @param lat 纬度
     * @return running_record_id: 跑步记录ID
     */
    @FormUrlEncoded
    @POST("running/start")
    Observable<ResponseBody> startRunning(@Header("Token") String token,
            @Field("lon") String lon,
            @Field("lat") String lat);

    /**
     * 结束跑步
     * @param recordId 跑步记录ID
     * @param lon 经度
     * @param lat 纬度
     * @param len 跑步距离长
     * @param maxSpeed 最大速度
     * @param avgSpeed 平均速度
     * @param path 路径数组 JSON [{“type”: 0, “lon”: 111, “lat”: 51}]
     * @return 无返回
     */
    @FormUrlEncoded
    @POST("running/finish")
    Observable<ResponseBody> finishRunning(@Header("Token") String token,
            @Field("running_record_id") String recordId,
            @Field("lon") String lon,
            @Field("lat") String lat,
            @Field("len") String len,
            @Field("max_speed") String maxSpeed,
            @Field("avg_speed") String avgSpeed,
            @Field("path") String path);

    /**
     * 获取我的跑步记录列表
     * @param page 页码，默认为1
     * @param count 单页最大数据量（1~20） 默认为5
     * @param order 排序 默认时间倒序 time_desc 时间顺序 time_asc
     * @return list: 跑步记录元数据
     *         page: 当前页码
     *         count: 当前页码最大数据量
     */
    @FormUrlEncoded
    @POST("running/my_list")
    Observable<ResponseBody> getRunningList(@Header("Token") String token,
            @Field("page") String page,
            @Field("count") String count,
            @Field("order") String order);

    /**
     * 获取具体跑步记录
     * @param recordId 跑步记录ID
     * @return user_id 跑步者ID
     *         state 跑步状态：0 running 1 pause 2 finish
     *         start_time 开始跑步时间
     *         start_location 开始地点经纬度 [lon, lat]
     *         last_update_time 最后一次的数据更新时间
     *         end_time 结束跑步时间
     *         end_location 结束跑步地点经纬度 [lon, lat]
     *         len 跑步长度 meter 【支持浮点数】
     *         max_speed 最高速度 m/s 【支持浮点数】
     *         avg_speed 平均速度 m/s  【支持浮点数】
     *         path 跑步路径
     *              type 记录点的跑步状态类型：0 running point, 1 stop point, 2 restart point
     *              lat 纬度
     *              lon 经度
     *              time 记录时间
     *         create_time 跑步记录产生时间
     */
    @FormUrlEncoded
    @POST("running/detail")
    Observable<ResponseBody> getRecordDetail(@Header("Token") String token,
            @Field("running_record_id") String recordId );
}
