package com.domencai.runin.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by lenovo on 2016/12/10.
 */

public class Account extends DataSupport {
    private int id;
    private String token;
    private String token_expire_time;
    private  String user_id;
    private boolean no_password;

    public void setId(int id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setToken_expire_time(String token_expire_time) {
        this.token_expire_time = token_expire_time;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setNo_password(boolean no_password) {
        this.no_password = no_password;
    }

    public int getId() {
        return id;
    }

    public boolean getNo_password() {
        return no_password;
    }

    public String getToken() {
        return token;
    }

    public String getToken_expire_time() {
        return token_expire_time;
    }

    public String getUser_id() {
        return user_id;
    }


}
