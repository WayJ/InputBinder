package com.tianque.inputbinder.function;

import com.tianque.inputbinder.inf.InputVerifyFailedException;
import com.tianque.inputbinder.item.InputItem;

import java.util.List;
import java.util.Map;

public interface ContainerFunc {
    void onPutOut(Map<String,String> map);

    void onVerifyFailed(List<InputVerifyFailedException> inputItems);
}
