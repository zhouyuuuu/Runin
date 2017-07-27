package com.domencai.runin.utils;

import android.util.Log;

import com.domencai.runin.bean.Account;
import com.domencai.runin.bean.User;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by lenovo on 2016/12/10.
 */

public class JsonUtil {

    //处理登录时服务器端返回的Json字符串
    public static Account handleLoginResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            String msg = jsonObject.getString("msg");
            Gson gson = new Gson();
            Account account = gson.fromJson(data.toString(),Account.class);
            Log.d("JSONUtil", "token is " + account.getToken());
            Log.d("JSONUtil", "token_expire_time is " + account.getToken_expire_time());
            Log.d("JSONUtil", "user_id is " + account.getUser_id());
            Log.d("JSONUtil", "no_password is "+ account.getNo_password());
            if (msg.equals("ok")){
                return account;
            }else {
                return null;
            }
        } catch (Exception e) {
            Log.d("JSONUtil","catch exception in handleLoginResponse");
            e.printStackTrace();
            return null;
        }
    }

    //处理获取动态码时服务器端返回的Json字符串
    public static boolean handleDynamicCodeResponse(String response) {
        try {
            Log.d("JSONUtil",response);
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            String msg = jsonObject.getString("msg");
            String time = data.getString("time");
            Log.d("JSONUtil", "time is " + time);
            return msg.equals("ok");
        } catch (Exception e) {
            Log.d("JSONUtil","catch exception in handleDynamicCodeResponse");
            e.printStackTrace();
            return false;
        }
    }

    //处理登录时服务器端返回的Json字符串
    public static User handleUserInfoResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            String msg = jsonObject.getString("msg");
            Gson gson = new Gson();
            User user = gson.fromJson(data.toString(),User.class);
            String user_id = data.getString("_id");
            user.setUser_id(user_id);
            Log.d("JSONUtil", "user_id is " + user.getUser_id());
            Log.d("JSONUtil", "nickname is " + user.getNickname());
            Log.d("JSONUtil", "avatar is " + user.getAvatar());
            Log.d("JSONUtil", "gender is " + user.getGender());
            Log.d("JSONUtil", "level is " + user.getLevel());
            Log.d("JSONUtil", "exp is "+ user.getExp());
            Log.d("JSONUtil", "height is " + user.getHeight());
            Log.d("JSONUtil", "weight is " + user.getWeight());
            Log.d("JSONUtil", "birthday is "+ user.getBirthday());
            Log.d("JSONUtil", "created_at is "+ user.getCreated_at());
            if (msg.equals("ok")){
                return user;
            }else {
                return null;
            }
        } catch (Exception e) {
            Log.d("JSONUtil","catch exception in handleUserInfoResponse");
            e.printStackTrace();
            return null;
        }
    }
    //处理无返回时的Json字符串
    public static String handleNoReturnResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String msg = jsonObject.getString("msg");
            Log.d("JSONUtil", "msg is " + msg);
            if (msg.equals("ok")){
                return "ok";
            }else {
                return msg;
            }
        } catch (Exception e) {
            Log.d("JSONUtil","catch exception in handleUserInfoResponse");
            e.printStackTrace();
            return "JSON error";
        }
    }
    //处理上传头像时服务器端返回的Json字符串
    public static String handleAvatarResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            String msg = jsonObject.getString("msg");
            String avatar_id = data.getString("avatar_id");
            String url = data.getString("url");
            Log.d("JSONUtil", "avatar_id is " + avatar_id);
            Log.d("JSONUtil", "uri is " + url);
            if (msg.equals("ok")){
                return avatar_id;
            }else {
                return null;
            }
        } catch (Exception e) {
            Log.d("JSONUtil","catch exception in handleLoginResponse");
            e.printStackTrace();
            return null;
        }
    }
}
