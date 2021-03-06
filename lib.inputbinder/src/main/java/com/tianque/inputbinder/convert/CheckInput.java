package com.tianque.inputbinder.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.FIELD})
public @interface CheckInput {
    String trueValue();
    String falseValue();
}
