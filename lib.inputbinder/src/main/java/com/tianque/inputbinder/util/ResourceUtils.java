package com.tianque.inputbinder.util;

import android.content.Context;

/**
 * Created by way on 2018/3/7.
 */

public class ResourceUtils {
    public static int findIdByName(Context context, String name) {
//        if(mResourcePre!=null)
//            return context.getResources().getIdentifier(mResourcePre+":id/"+name,null,null);
//        else
            return context.getResources().getIdentifier(name,"id",context.getPackageName());
    }
}
