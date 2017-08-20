package com.cdh.instacast;

import android.app.Application;

import com.cdh.instacast.restapi.RestManager;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Dudu_Cohen on 19/08/2017.
 */

public class InstaCast extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // init rest api
        RestManager.get().initRestClient();

        Fresco.initialize(this);

    }
}
