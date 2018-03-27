package com.tianque.inputbinder.util;

import android.content.Context;
import android.widget.Toast;

import com.tianque.inputbinder.BuildConfig;

/**
 * Created by way on 2018/3/27.
 */

public class ToastUtils {
    public static void showDebugToast(String message) {
        if (BuildConfig.DEBUG)
            Toast.makeText(ContextUtils.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    public static void show(int stringResId) {
        Toast.makeText(ContextUtils.getApplicationContext(), ContextUtils.getApplicationContext().getResources().getString(stringResId), Toast.LENGTH_SHORT).show();
    }

    public static void show(String message) {
        Toast.makeText(ContextUtils.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
