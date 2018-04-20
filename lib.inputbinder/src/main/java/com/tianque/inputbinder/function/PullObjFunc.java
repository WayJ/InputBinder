package com.tianque.inputbinder.function;

@FunctionalInterface
public interface QueryObjFunc<Out> {
    Out doQuery();
}
