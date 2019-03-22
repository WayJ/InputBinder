package com.tianque.inputbinder.inf;

import android.view.View;

import com.tianque.inputbinder.item.InputItem;

/**
 * 用来让inputItem调用inputbinder的方法
 * Created by way on 2018/3/27.
 */

public interface InputItemHand {
    public InputItem findInputItemByViewName(String viewName);


    public InputItem findInputItemByViewId(int viewId);

    public View findViewById(int id);
}
