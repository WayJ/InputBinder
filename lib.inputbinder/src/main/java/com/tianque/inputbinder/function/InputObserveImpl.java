package com.tianque.inputbinder.function;

import com.tianque.inputbinder.util.Logging;

import java.lang.reflect.Field;

public class InputObserveImpl extends InputObserve {
    private Field field;
    private Object pojo;

    public InputObserveImpl(Field field, Object pojo) {
        this.field = field;
        this.pojo = pojo;
    }

    @Override
    public void onNext(Object o) {
        try {
            field.set(pojo,o);
        } catch (Exception e) {
            Logging.e(e);
        }
    }
}
