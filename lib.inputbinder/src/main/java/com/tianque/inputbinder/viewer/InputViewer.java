package com.tianque.inputbinder.viewer;

/**
 * Created by way on 2018/3/5.
 */

public interface InputViewer<T> {

    T getContent();

    void setContent(T content);

}
