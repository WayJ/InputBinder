package com.tianque.inputbinder.function;

@FunctionalInterface
public interface UpdateObjFunc<In> {
    void doUpdate(In in);
}
