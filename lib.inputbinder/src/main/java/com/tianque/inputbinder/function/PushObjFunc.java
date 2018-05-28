package com.tianque.inputbinder.function;

@FunctionalInterface
public interface PushObjFunc<In> {
    void doUpdate(In in);
}
