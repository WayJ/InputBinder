/*
 *  Copyright 2017 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.tianque.inputbinder.util;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Method;


public class ContextUtils {
    private static volatile Context applicationContext;

    private static synchronized void initialize() {
        try {
            if(applicationContext==null) {
                Class activityThread = Class.forName("android.app.ActivityThread");
                Method m = activityThread.getMethod("currentApplication", new Class[0]);
                m.setAccessible(true);
                ContextUtils.applicationContext = (Application) m.invoke((Object) null, new Object[0]);
            }
        } catch (Throwable throwable) {
            throw new RuntimeException("Failed to get current application!");
        }
    }

    public static Context getApplicationContext() {
        if(applicationContext==null){
            ContextUtils.initialize();
        }
        return applicationContext;
    }
}
