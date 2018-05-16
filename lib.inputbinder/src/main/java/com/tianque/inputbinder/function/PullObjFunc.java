package com.tianque.inputbinder.function;

@FunctionalInterface
public interface PullObjFunc<Out> {
    Out doQuery();
}
