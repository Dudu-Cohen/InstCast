package com.cdh.instacast.restapi;


import com.cdh.instacast.pojo.InstagramMedia;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Dudu_Cohen on 19/08/2017.
 */
public interface ApiService {

    @GET("media/search/")
    Observable<InstagramMedia> getImageByLocation(@Query("lat") double lat, @Query("lng") double lng, @Query("distance") int distance);
}
