package com.tianque.inputbinder.convert.impl;

import android.util.Pair;

import com.tianque.inputbinder.convert.CheckInput;
import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.convert.OptionalInput;
import com.tianque.inputbinder.inf.RequestValueContract;
import com.tianque.inputbinder.item.CheckInputItem;
import com.tianque.inputbinder.model.InputItemProfile;

import java.lang.annotation.Annotation;

import io.reactivex.Observable;

public abstract class CheckInputItemConvert<In> extends ItemTypeConvert<In, CheckInputItem> implements RequestValueContract.RequestValueObservable<In, In>{

    private static final String ParmTag_dependent = "dependent";
    private static final String ParmTag_dependent_inversion = "dependent_inversion";

//    private static final Pair<Integer,Integer> defaultCheckValues1 = "0" + SEPARATOR + "1";
//    private static final String defaultCheckValues2 = "false" + SEPARATOR + "true";
//    private static final String defaultCheckValues3 = "FALSE" + SEPARATOR + "TRUE";
    @Override
    public CheckInputItem loadProfile(CheckInputItem inputItem, InputItemProfile profile) {
        initCheckData(inputItem);
        return inputItem;
    }

    public abstract void initCheckData(CheckInputItem inputItem);

    @Override
    public Observable<In> requestValue(In value) {
        return Observable.just(value);
    }

    public static class AdapterBoolean extends CheckInputItemConvert<Boolean> {

        @Override
        public void initCheckData(CheckInputItem inputItem) {
            CheckInputItem.CheckData<Boolean> checkData=new CheckInputItem.CheckData<>();
            checkData.setCheckData(true,false);
            inputItem.setCheckData(checkData);
        }
    }

    public static class AdapterInt extends CheckInputItemConvert<Integer> {

        @Override
        public void initCheckData(CheckInputItem inputItem) {
            CheckInputItem.CheckData<Integer> checkData=new CheckInputItem.CheckData<>();
            checkData.setCheckData(1,0);

            Annotation annotation = inputItem.getInputItemProfile().getAnnotation(CheckInput.class);
            if (annotation != null) {
                CheckInput checkInput = (CheckInput) annotation;
                String trueValue = checkInput.trueValue();
                String falseValue = checkInput.falseValue();
                checkData.setCheckData(Integer.valueOf(trueValue),Integer.valueOf(falseValue));
            }
            inputItem.setCheckData(checkData);
        }
    }

    public static class AdapterString extends CheckInputItemConvert<String> {

        @Override
        public void initCheckData(CheckInputItem inputItem) {
            CheckInputItem.CheckData<String> checkData=new CheckInputItem.CheckData<>();

            Annotation annotation = inputItem.getInputItemProfile().getAnnotation(CheckInput.class);
            if (annotation != null) {
                CheckInput checkInput = (CheckInput) annotation;
                String trueValue = checkInput.trueValue();
                String falseValue = checkInput.falseValue();
                checkData.setCheckData(trueValue,falseValue);
            }else{
                checkData.setCheckData("true","false");
            }

            inputItem.setCheckData(checkData);
        }
    }

}
