package com.tianque.inputbinder.convert.impl;

import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.convert.OptionalInput;
import com.tianque.inputbinder.inf.RequestValueContract;
import com.tianque.inputbinder.item.OptionalInputItem;
import com.tianque.inputbinder.model.InputItemProfile;

import java.lang.annotation.Annotation;

import io.reactivex.Observable;

public abstract class OptionalInputItemConvert<In> extends ItemTypeConvert<In, OptionalInputItem> implements RequestValueContract.RequestValueObservable<In, In> {

    @Override
    public OptionalInputItem loadProfile(OptionalInputItem inputItem, InputItemProfile profile) {
        inputItem.setOptionalData(initOptionalData(inputItem));
        return inputItem;
    }

    protected abstract OptionalInputItem.OptionalData<In> initOptionalData(OptionalInputItem optionalInputItem);

    @Override
    public Observable<In> requestValue(In value) {
        return Observable.just(value);
    }

    public static class AdapterInt extends OptionalInputItemConvert<Integer> {

        @Override
        protected OptionalInputItem.OptionalData<Integer> initOptionalData(OptionalInputItem optionalInputItem) {

            OptionalInputItem.OptionalData<Integer> dataHold = new OptionalInputItem.OptionalData<>();
            Annotation annotation = optionalInputItem.getInputItemProfile().getAnnotation(OptionalInput.class);
            if (annotation != null) {
                OptionalInput optional = (OptionalInput) annotation;
                String[] optionalKeys = optional.titles();
                String[] optionalValues = optional.values();
                for (int i = 0; i < optionalKeys.length; i++) {
                    dataHold.add(optionalKeys[i], Integer.valueOf(optionalValues[i]));
                }
            }
            return dataHold;
        }

    }

    public static class AdapterString extends OptionalInputItemConvert<String> {

        protected OptionalInputItem.OptionalData<String> initOptionalData(OptionalInputItem optionalInputItem) {
            OptionalInputItem.OptionalData<String> dataHold = new OptionalInputItem.OptionalData<>();

            Annotation annotation = optionalInputItem.getInputItemProfile().getAnnotation(OptionalInput.class);
            if (annotation != null) {
                OptionalInput optional = (OptionalInput) annotation;
                String[] optionalKeys = optional.titles();
                String[] optionalValues = optional.values();
                for (int i = 0; i < optionalKeys.length; i++) {
                    dataHold.add(optionalKeys[i], optionalValues[i]);
                }
            }
            return dataHold;
        }
    }
}
