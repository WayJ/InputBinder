package com.tianque.inputbinder.function;

import java.util.Map;

@FunctionalInterface
public interface PullMapFunc {
    Map<String,String> doQuery();
}
