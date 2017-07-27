package com.domencai.runin.utils;

import android.content.ContentValues;
import android.util.Log;

import com.domencai.runin.bean.Account;
import com.domencai.runin.bean.User;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.domencai.runin.activity.LoadingActivity.globalUser;

/**
 * Created by lenovo on 2016/12/29.
 */

public class DbHelper {

    private static ContentValues values = new ContentValues();

    public static int updateUser(User user){
        if (user!=null){
            SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS", Locale.CHINA);
            Date date = new Date();
            String dataStr = toDate.format(date);
            user.setSync_at(dataStr);
            values.put("avatar", user.getAvatar());
            values.put("birthday", user.getBirthday());
            values.put("created_at", user.getCreated_at());
            values.put("exp", user.getExp());
            values.put("gender", user.getGender());
            values.put("height", user.getHeight());
            values.put("intro", user.getIntro());
            values.put("level", user.getLevel());
            values.put("nickname", user.getNickname());
            values.put("weight", user.getWeight());
            values.put("sync_at", user.getSync_at());
        }else {
            Log.d("DBhelper","updateUser user is null");
        }
        return DataSupport.update(User.class, values, globalUser.getId());
    }

    public static int updateAccount(Account oldAccount,Account newAccount){
        values.put("token_expire_time", newAccount.getToken_expire_time());
        values.put("no_password", newAccount.getNo_password());
        return DataSupport.update(Account.class, values, oldAccount.getId());
    }

    public static int updateNickName(String name){
        values.put("nickname", name);
        return DataSupport.update(User.class, values, globalUser.getId());
    }

    public static int updateIntro(String intro){
        values.put("intro", intro);
        return DataSupport.update(User.class, values, globalUser.getId());
    }

    public static int updateAvatar(String avatar_id){
        values.put("avatar", avatar_id);
        return DataSupport.update(User.class, values, globalUser.getId());
    }
}
