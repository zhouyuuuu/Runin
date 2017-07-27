package com.domencai.runin.utils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 线程转换
 * 使用.subscribeOn(Schedulers.io()) .observeOn(AndroidSchedulers.mainThread())
 * 的地方都可以使用.compose(RxSchedulersHelper.io_main())代替。
 * Created by Domen、on 2016/12/14.
 */

public class RxSchedulersHelper {
    public static <T> Observable.Transformer<T, T> io_main() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
