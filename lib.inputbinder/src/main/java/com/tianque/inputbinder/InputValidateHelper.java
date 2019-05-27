package com.tianque.inputbinder;

import android.text.TextUtils;
import android.view.View;

import com.tianque.inputbinder.inf.Input;
import com.tianque.inputbinder.inf.InputVerifyFailedException;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.model.InputItemProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public List<InputVerifyFailedException> validateRequestParams(Collection<InputItem> items) {
        List<InputVerifyFailedException> exceptionList=new ArrayList<>();
        for (InputItem inputItem : items) {
            if (TextUtils.isEmpty(inputItem.getRequestKey()))
                continue;
            if(inputItem.getView()==null)
                continue;
            View view = inputItem.getView();
            if (view.getVisibility() != View.VISIBLE) continue;

            InputItemProfile attr = inputItem.getInputItemProfile();

            InputVerifyFailedException exception=verifyInput(attr.verify,inputItem);
            if(exception!=null)
                exceptionList.add(exception);
        }
        return exceptionList;
    }

    private static boolean checkEqual(int target,int checkStandard){
        return ((target&checkStandard) ^ checkStandard)==0;
    }

    private static final String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
    private static final String RULE_MOBILE = "^1(3|4|5|7|8)\\d{9}$";



    private static InputVerifyFailedException verifyInput(int verifyTarget,InputItem inputItem){
        String requestValue = inputItem.getRequestValue();
        if(checkEqual(verifyTarget,Input.Verify_AllowNull)&&TextUtils.isEmpty(requestValue)){
            return null;
        }
        if(checkEqual(verifyTarget,Input.Verify_NotNull)&&TextUtils.isEmpty(requestValue)) {
            return new InputVerifyFailedException(Input.Verify_NotNull,inputItem);
        }
        if(checkEqual(verifyTarget,Input.Verify_Email)) {
            if(!matched(requestValue,RULE_EMAIL))
                return new InputVerifyFailedException(Input.Verify_Email,inputItem);
        }

        if(checkEqual(verifyTarget,Input.Verify_Mobile)) {
            if(!matched(requestValue,RULE_MOBILE))
                return new InputVerifyFailedException(Input.Verify_Mobile,inputItem);
        }

        if(checkEqual(verifyTarget,Input.Verify_IDCard18)) {
        }

        return null;
    }


    private static boolean matched(String target,String rule){
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(target);
        return m.matches();
    }
}
