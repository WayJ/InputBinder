package com.tianque.inputbinder.convert.impl;

import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.inf.RequestDataContract;
import com.tianque.inputbinder.item.DateInputItem;
import com.tianque.inputbinder.model.InputItemProfile;

import java.util.Date;

import io.reactivex.Observable;

public abstract class DateInputItemConvert<In> extends ItemTypeConvert<In, DateInputItem> implements RequestDataContract.IObjectDataConvert<In,String> {

    @Override
    public DateInputItem loadProfile(DateInputItem inputItem, InputItemProfile profile) {
        return inputItem;
    }


    public static class AdapterString extends DateInputItemConvert<String>{


        @Override
        public Observable<String> requestConvertValueFromObject(String value) {
            return Observable.just(value);
        }

        @Override
        public String requestObjectValue(String s) {
            return s;
        }
    }

    public static class AdapterDate extends DateInputItemConvert<Date>{
        @Override
        public Observable<String> requestConvertValueFromObject(Date value) {
            return null;
        }

        @Override
        public Date requestObjectValue(String s) {
            return null;
        }
    }
}
