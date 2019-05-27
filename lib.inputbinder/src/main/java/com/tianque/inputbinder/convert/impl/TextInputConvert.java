package com.tianque.inputbinder.convert.impl;

import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.inf.RequestValueContract;
import com.tianque.inputbinder.item.TextInputItem;
import com.tianque.inputbinder.model.InputItemProfile;

import io.reactivex.Observable;

public abstract class TextInputConvert<In> extends ItemTypeConvert<In, TextInputItem> implements RequestValueContract.RequestValueObservable<In, String> {

    @Override
    public TextInputItem loadProfile(TextInputItem inputItem, InputItemProfile profile) {
        return inputItem;
    }

    public static class AdapterInt extends TextInputConvert<Integer> {

        @Override
        public Observable<String> requestValue(Integer value) {
            return Observable.just(value.toString());
        }
    }

    public static class AdapterString extends TextInputConvert<String> {

        @Override
        public Observable<String> requestValue(String value) {
            return Observable.just(value);
        }
    }

    public static class AdapterLong extends TextInputConvert<Long> {

        @Override
        public Observable<String> requestValue(Long value) {
            return Observable.just(value.toString());
        }
    }

    public static class AdapterDouble extends TextInputConvert<Double> {

        @Override
        public Observable<String> requestValue(Double value) {
            return Observable.just(value.toString());
        }
    }


}
