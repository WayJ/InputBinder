package com.tianque.inputbinder.inf;

import android.view.View;

import com.tianque.inputbinder.item.InputItem;

/**
 * Created by way on 2018/3/26.
 */

public interface ViewObserver {

    void onButtonClick(View view, InputItem inputItem);

    void onCheckedChanged(View view, InputItem inputItem);
}
