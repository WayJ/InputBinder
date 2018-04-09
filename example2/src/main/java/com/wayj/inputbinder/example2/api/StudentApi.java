package com.wayj.inputbinder.example2.api;

import com.wayj.inputbinder.example2.Student;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by way on 2018/3/28.
 */

public interface StudentApi {

    @GET("student/getStudent")
    Call<Student> getStudent(@Query("id") int id);


    @POST("student/create")
    Call<Student> create(@QueryMap Map<String, String> map);

    @POST("student/create")
    Call<Student> create2(@Body Student student);
}
