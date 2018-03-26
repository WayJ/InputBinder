package com.tianque.inputbinder;

import android.text.TextUtils;
import android.view.View;

import com.tianque.inputbinder.model.ViewAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by way on 2018/3/5.
 */

public class InputValidateHelper {
    private HashMap<String, ViewAttribute> mValidateMap = new HashMap<>();
    protected ArrayList<String> ignoreRequired = new ArrayList<>();

    public void put(String key,ViewAttribute attr){
        mValidateMap.put(key,attr);
    }

    public boolean validateRequestParams(Map<String, String> params) {
//        for (Map.Entry<String, ViewAttribute> entry : mValidateMap.entrySet()) {
//            String requestKey = entry.getKey();
//
//            if (TextUtils.isEmpty(requestKey) || ignoreRequired.contains(requestKey)) continue;
//
//            ViewAttribute attr = entry.getValue();
//            View view = rootView.findViewById(attr.viewId);
//            if (view.getVisibility() != View.VISIBLE) continue;
//
//            String validatedValue = params.get(requestKey);
//            Boolean isRequired = attr.required;
//            String requiredRemind = attr.requiredRemind;
//            String methodName = attr.validateMethoid;
//
//            if (Util.isLegal(validatedValue)) {
//                if (!isLengthLegal(validatedValue, attr)) {
//                    return false;
//                }
//                if (Util.isLegal(methodName)) {
//                    boolean result = execValidateMethod(view, methodName, validatedValue);
//                    if (result)
//                        continue;
//                    focusView(attr.viewId);
//                    return false;
//                }
//            } else if (isRequired != null && isRequired) {
//                if (!TextUtils.isEmpty(requiredRemind)) {
//                    showTip(requiredRemind);
//                }else {
//                    showTip("请填写所有必填项");
//                }
//                ViewShakeUtil.newInstance(mContext).shake(view);
//                focusView(attr.viewId);
//                return false;
//            }
//        }
        return true;
    }
}
