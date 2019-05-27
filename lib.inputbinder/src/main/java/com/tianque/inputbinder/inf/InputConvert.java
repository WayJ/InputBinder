package com.tianque.inputbinder.inf;


import com.tianque.inputbinder.convert.ItemTypeConvert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface InputConvert  {
    Class<? extends ItemTypeConvert> value();
}
