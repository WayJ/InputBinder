package com.tianque.inputbinder;

import android.text.TextUtils;
import android.view.View;

import com.tianque.inputbinder.inf.Input;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.model.ViewAttribute;
import com.tianque.inputbinder.util.ToastUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by way on 2018/3/5.
 */

class InputValidateHelper {
//    private List<InputItem> mValidateItems = new ArrayList<>();
//    protected ArrayList<String> ignoreRequired = new ArrayList<>();
//
//    public void add(InputItem inputItem){
//        mValidateItems.add(inputItem);
//    }
//
////    public HashMap<String, ViewAttribute> getValidateMap() {
////        return mValidateMap;
////    }

    public boolean validateRequestParams(Collection<InputItem> items) {

        for (InputItem inputItem : items) {
            if (TextUtils.isEmpty(inputItem.getRequestKey()))
                continue;
            if(inputItem.getView()==null)
                continue;
            View view = inputItem.getView();
            if (view.getVisibility() != View.VISIBLE) continue;
            String requestValue = inputItem.getRequestValue();

            ViewAttribute attr = inputItem.getViewAttribute();

            if(attr.verify!= Input.Verify_AllowNull){
                if (TextUtils.isEmpty(requestValue))
                    return false;
            }

        }
        return true;
    }
}
