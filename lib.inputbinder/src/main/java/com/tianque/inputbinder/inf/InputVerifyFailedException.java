package com.tianque.inputbinder.inf;

import com.tianque.inputbinder.item.InputItem;

public class InputVerifyFailedException extends Exception {
    int verifyFailedType;
    InputItem inputItem;

    public InputVerifyFailedException(int verifyFailedType, InputItem inputItem) {
        this.verifyFailedType = verifyFailedType;
        this.inputItem = inputItem;
    }
}
