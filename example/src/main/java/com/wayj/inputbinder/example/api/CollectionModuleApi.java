package com.wayj.inputbinder.example.api;

import com.alibaba.fastjson.JSONObject;
import com.wayj.inputbinder.example.entity.GridPage;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by way on 2017/9/20.
 */

public interface CollectionModuleApi {

    /**
     * 获得列表数据
     */
    @GET("{url}")
    Observable<GridPage<JSONObject>> getListData(@Path(value = "url", encoded = true) String url, @QueryMap Map<String, String> map);

    @POST("{url}")
    Observable<String> doCreate(@Path(value = "url", encoded = true) String url, @QueryMap Map<String, String> map);

    @GET("{url}")
    Call<String> doRetrieve(@Path(value = "url", encoded = true) String url, @Query("id") int id, @QueryMap Map<String, String> map);

    @POST("{url}")
    Observable<String> doDelete(@Path(value = "url", encoded = true) String url, @QueryMap Map<String, String> map);

    @POST("{url}")
    Observable<String> doUpdate(@Path(value = "url", encoded = true) String url, @QueryMap Map<String, String> map);

    @POST("{url}")
    Observable<String> varvifyIdDuplication(@Path(value = "url", encoded = true) String url, @QueryMap Map<String, String> map);

    @POST("{url}")
    Observable<String> getoperation(@Path(value = "url", encoded = true) String url, @QueryMap Map<String, String> map);

}
