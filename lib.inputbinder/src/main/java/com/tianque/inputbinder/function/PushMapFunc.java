package com.tianque.inputbinder.function;

import java.util.Map;

@FunctionalInterface
public interface PushMapFunc {
    void doUpdate(Map<String,String> map);
}
