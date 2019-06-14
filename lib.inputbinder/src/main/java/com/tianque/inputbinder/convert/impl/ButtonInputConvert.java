package com.tianque.inputbinder.convert.impl;

import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.inf.RequestDataContract;
import com.tianque.inputbinder.item.ButtonInputItem;
import com.tianque.inputbinder.model.InputItemProfile;

import io.reactivex.Observable;

public abstract class ButtonInputConvert<In> extends ItemTypeConvert<In, ButtonInputItem> implements RequestDataContract.IObjectDataConvert<In, String> {

    @Override
    public ButtonInputItem loadProfile(ButtonInputItem inputItem, InputItemProfile profile) {
        return inputItem;
    }

    public static class AdapterInt extends ButtonInputConvert<Integer> {

        @Override
        public Observable<String> requestConvertValueFromObject(Integer value) {
            return Observable.just(value.toString());
        }

        @Override
        public Integer requestObjectValue(String s) {
            return Integer.valueOf(s);
        }
    }

    public static class AdapterString extends ButtonInputConvert<String> {

        @Override
        public Observable<String> requestConvertValueFromObject(String value) {
            return Observable.just(value);
        }

        @Override
        public String requestObjectValue(String s) {
            return s;
        }
    }

    public static class AdapterLong extends ButtonInputConvert<Long> {

        @Override
        public Observable<String> requestConvertValueFromObject(Long value) {
            return Observable.just(value.toString());
        }

        @Override
        public Long requestObjectValue(String s) {
            return Long.valueOf(s);
        }
    }

    public static class AdapterDouble extends ButtonInputConvert<Double> {

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
