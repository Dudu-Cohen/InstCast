package com.cdh.instacast.api.rest;


import com.cdh.instacast.utils.Consts;

import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dudu_Cohen on 19/08/2017.
 */

public class RestManager {

    private static RestManager mInstance;
    private ApiService mApiService;

    public static RestManager get() {
        if (mInstance == null) {
            mInstance = new RestManager();
        }
        return mInstance;
    }

    public void initRestClient() {

        // setup the log interceptor.
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(chain -> {
                            Request original = chain.request();

                            HttpUrl originalHttpUrl = original.url();

                            HttpUrl url = originalHttpUrl.newBuilder()
                                    .addQueryParameter("access_token", Consts.ACCESS_TOKEN)
                                    .addQueryParameter("count", Consts.RESULTS_COUNT)
                                    .build();

                            // Request customization: add request headers
                            Request.Builder requestBuilder = original.newBuilder()
                                    .url(url);

                            Request request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                )

                .build();


        // setup the client.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Consts.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        mApiService = retrofit.create(ApiService.class);
    }

    public ApiService api() {
        return mApiService;
    }
}