package com.tianque.inputbinder.model;

import java.util.List;

/**
 * Created by way on 2018/3/26.
 */

public interface InputReaderInf <T>{
    List<ViewAttribute> read();

    boolean isSafe(T obj);
}
