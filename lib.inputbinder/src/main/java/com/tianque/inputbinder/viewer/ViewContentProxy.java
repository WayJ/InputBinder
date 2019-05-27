package com.tianque.inputbinder.viewer;

import com.tianque.inputbinder.inf.InputVerifyFailedException;

/**
 * Created by way on 2018/3/5.
 */

public interface ViewContentProxy<T> {

    T getContent();

    void setContent(T content);

}
