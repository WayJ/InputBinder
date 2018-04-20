package com.tianque.inputbinder.function;

import java.util.Map;

@FunctionalInterface
public interface UpdateMapFunc {
    void doUpdate(Map<String,String> map);
}
