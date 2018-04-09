package com.wayj.inputbinder.example2.api;

import android.support.v7.app.AppCompatActivity;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import retrofit2.Retrofit;

/**
 * Created by way on 17/2/16.
 */

public abstract class BaseApi<T extends Object> {
    public T api;
    public AppCompatActivity appCompatActivity;

    private static Retrofit mRetrofit;//简单的放在这里，实际项目自行处理

    public BaseApi() {
//        this.api = getRetrofit().create((Class<T>)getSuperClassGenricType(getClass(), 0));
    }

    public BaseApi(AppCompatActivity activity) {
        this.appCompatActivity = activity;
//        this.api = getRetrofit().create((Class<T>)getSuperClassGenricType(getClass(), 0));
    }

    public Retrofit getRetrofit(){
        if(mRetrofit==null){
//            mRetrofit = new Retrofit.Builder()
//                    .baseUrl("")
////                    .addConverterFactory(ApiGsonConverterFactory.create())
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
////                    .client(OkHttpUtil.getOkHttpClient())
//                    .build();
        }
        return mRetrofit;
    }

    public T getApi() {
        return api;
    }

    public void setApi(T api) {
        this.api = api;
    }

    /**
     * 通过反射, 获得定义Class时声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
     *
     *@param clazz
     *            clazz The class to introspect
     * @param index
     *            the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be
     *         determined
     */
    @SuppressWarnings("unchecked")
    public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {

        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }



    public void setActivity(AppCompatActivity activity){
        this.appCompatActivity = activity;
    }

    public AppCompatActivity getActivity(){
        return appCompatActivity;
    }
}
