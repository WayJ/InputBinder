package com.tianque.inputbinder.convert.impl;

import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.convert.OptionalInput;
import com.tianque.inputbinder.inf.RequestValueContract;
import com.tianque.inputbinder.item.MultiOptionalInputItem;
import com.tianque.inputbinder.item.OptionalInputItem;
import com.tianque.inputbinder.model.InputItemProfile;

import java.lang.annotation.Annotation;
import java.util.List;

import io.reactivex.Observable;

public abstract class MultiOptionalInputItemConvert<In> extends ItemTypeConvert<In, MultiOptionalInputItem> implements RequestValueContract.RequestValueObservable<In, In> {

    @Override
    public MultiOptionalInputItem loadProfile(MultiOptionalInputItem inputItem, InputItemProfile profile) {
        inputItem.setMultiOptionalData(initMultiOptionalData(inputItem));
        return inputItem;
    }

    protected abstract MultiOptionalInputItem.MultiOptionalData<In> initMultiOptionalData(MultiOptionalInputItem optionalInputItem);

    @Override
    public Observable<In> requestValue(In value) {
        return Observable.just(value);
    }

    public static class AdapterInt extends MultiOptionalInputItemConvert<Integer> {

        @Override
        protected MultiOptionalInputItem.MultiOptionalData<Integer> initMultiOptionalData(MultiOptionalInputItem inputItem) {
            MultiOptionalInputItem.MultiOptionalData<Integer> multiOptionalData = new MultiOptionalInputItem.MultiOptionalData<>();

            Annotation annotation = inputItem.getInputItemProfile().getAnnotation(OptionalInput.class);
            if (annotation != null) {
                OptionalInput optional = (OptionalInput) annotation;
                String[] optionalKeys = optional.titles();
                String[] optionalValues = optional.values();
                for (int i = 0; i < optionalKeys.length; i++) {
                    multiOptionalData.add(optionalKeys[i], Integer.valueOf(optionalValues[i]));
                }
                multiOptionalData.setSelectedIndexs(new boolean[optionalKeys.length]);
            }
            return multiOptionalData;
        }

    }

    public static class AdapterString extends MultiOptionalInputItemConvert<String> {

        @Override
        protected MultiOptionalInputItem.MultiOptionalData<String> initMultiOptionalData(MultiOptionalInputItem inputItem) {
            MultiOptionalInputItem.MultiOptionalData<String> multiOptionalData = new MultiOptionalInputItem.MultiOptionalData<>();

            Annotation annotation = inputItem.getInputItemProfile().getAnnotation(OptionalInput.class);
            if (annotation != null) {
                OptionalInput optional = (OptionalInput) annotation;
                String[] optionalKeys = optional.titles();
                String[] optionalValues = optional.values();
                for (int i = 0; i < optionalKeys.length; i++) {
                    multiOptionalData.add(optionalKeys[i], optionalValues[i]);
                }
                multiOptionalData.setSelectedIndexs(new boolean[optionalKeys.length]);
            }
            return multiOptionalData;
        }
    }
}
