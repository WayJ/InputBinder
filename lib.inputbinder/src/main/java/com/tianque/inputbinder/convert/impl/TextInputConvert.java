package com.tianque.inputbinder.convert.impl;

import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.inf.RequestDataContract;
import com.tianque.inputbinder.item.TextInputItem;
import com.tianque.inputbinder.model.InputItemProfile;

import io.reactivex.Observable;

public abstract class TextInputConvert<In> extends ItemTypeConvert<In, TextInputItem> implements RequestDataContract.IObjectDataConvert<In, String> {

    @Override
    public TextInputItem loadProfile(TextInputItem inputItem, InputItemProfile profile) {
        return inputItem;
    }

    public static class AdapterInt extends TextInputConvert<Integer> {

        @Override
        public TextInputItem loadProfile(TextInputItem inputItem, InputItemProfile profile) {
            inputItem.setEditType(1);
            return super.loadProfile(inputItem, profile);
        }

        @Override
        public Observable<String> requestConvertValueFromObject(Integer value) {
            return Observable.just(value.toString());
        }

        @Override
        public Integer requestObjectValue(String s) {
            return Integer.valueOf(s);
        }

    }

    public static class AdapterString extends TextInputConvert<String> {

        @Override
        public Observable<String> requestConvertValueFromObject(String value) {
            return Observable.just(value);
        }

        @Override
        public String requestObjectValue(String s) {
            return s;
        }
    }

    public static class AdapterLong extends TextInputConvert<Long> {

        @Override
        public TextInputItem loadProfile(TextInputItem inputItem, InputItemProfile profile) {
            inputItem.setEditType(1);
            return super.loadProfile(inputItem, profile);
        }

        @Override
        public Observable<String> requestConvertValueFromObject(Long value) {
            return Observable.just(value.toString());
        }

        @Override
        public Long requestObjectValue(String s) {
            return Long.valueOf(s);
        }
    }

    public static class AdapterDouble extends TextInputConvert<Double> {

        @Override
        public TextInputItem loadProfile(TextInputItem inputItem, InputItemProfile profile) {
            inputItem.setEditType(1);
            return super.loadProfile(inputItem, profile);
        }

        @Override
        public Observable<String> requestConvertValueFromObject(Double value) {
            return Observable.just(value.toString());
        }

        @Override
        public Double requestObjectValue(String s) {
            return Double.valueOf(s);
        }
    }


}
