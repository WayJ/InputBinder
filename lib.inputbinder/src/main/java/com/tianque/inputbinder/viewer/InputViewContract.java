package com.tianque.inputbinder.viewer;

import com.tianque.inputbinder.inf.InputVerifyFailedException;

public interface InputViewContract {

    void onVerifyFailed(InputVerifyFailedException verifyError);
}
