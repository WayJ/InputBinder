package com.tianque.inputbinder.util;

import android.content.Context;
import android.widget.Toast;

import com.tianque.inputbinder.BuildConfig;

/**
 * Created by way on 2018/3/27.
 */

public class ToastUtils {
    public static void showDebugToast(Context context,String message){
        if(BuildConfig.DEBUG)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
