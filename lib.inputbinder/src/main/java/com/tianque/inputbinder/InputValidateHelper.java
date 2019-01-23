package com.tianque.inputbinder;

import android.text.TextUtils;
import android.view.View;

import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.model.ViewAttribute;
import com.tianque.inputbinder.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by way on 2018/3/5.
 */

class InputValidateHelper {
    private List<InputItem> mValidateItems = new ArrayList<>();
    protected ArrayList<String> ignoreRequired = new ArrayList<>();

    public void add(InputItem inputItem){
        mValidateItems.add(inputItem);
    }

//    public HashMap<String, ViewAttribute> getValidateMap() {
//        return mValidateMap;
//    }

    public boolean validateRequestParams(Map<String, String> params) {
        for (InputItem inputItem:mValidateItems) {
            String requestKey = inputItem.getRequestKey();

            if (TextUtils.isEmpty(requestKey) || ignoreRequired.contains(requestKey)) continue;

            ViewAttribute attr = inputItem.getViewAttribute();
            View view = inputItem.getView();
            if (view.getVisibility() != View.VISIBLE) continue;

            String validatedValue = params.get(requestKey);
            Boolean isRequired = attr.required;
//            String requiredRemind = attr.requiredRemind;
//            String methodName = attr.validateMethoid;

            if (!TextUtils.isEmpty(validatedValue)) {
//                if (!isLengthLegal(validatedValue, attr)) {
//                    return false;
//                }
//                if (Util.isLegal(methodName)) {
//                    boolean result = execValidateMethod(view, methodName, validatedValue);
//                    if (result)
                        continue;
//                    focusView(attr.viewId);
//                    return false;
//                }
            } else if (isRequired != null && isRequired) {
//                if (!TextUtils.isEmpty(requiredRemind)) {
//                    showTip(requiredRemind);
//                }else {
//                String viewName = inputItem.getView()
                    ToastUtils.show("请填写必填项");
//                }
//                ViewShakeUtil.newInstance(mContext).shake(view);
//                focusView(attr.viewId);
                return false;
            }
        }
        return true;
    }
}
