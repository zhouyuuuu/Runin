package com.domencai.runin.listener;

/**
 * Created by Domen、on 2016/12/14.
 */

public interface RequestListener<T> {
    void onSucceed(T t);

    void onFail(String s);
}
