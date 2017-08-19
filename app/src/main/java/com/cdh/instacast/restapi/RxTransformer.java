package com.cdh.instacast.restapi;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Dudu_Cohen on 22/02/2017.
 */

public class RxTransformer {
    public static <T> Observable.Transformer<T, T> applyIOSchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
