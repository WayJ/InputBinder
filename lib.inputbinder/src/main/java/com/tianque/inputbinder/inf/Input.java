package com.tianque.inputbinder.inf;

import com.tianque.inputbinder.item.InputItemType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by way on 2018/3/7.
 */
@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.FIELD})//定义注解的作用目标**作用范围字段、枚举的常量/方法
public @interface Input {
    /**
     * input类型
     * @return
     */
    InputItemType type() default InputItemType.NULL;

    /**
     * input扩展类型
     * @return
     */
    String typeExt()  default "";
    /**
     * 字段名称
     * @return
     */
    String viewName() default "";

    int viewId() default 0;

    String requestKey() default "";

    /**
     * 附加参数，jsonObj格式，这里会进行转换成jsonObj
     * @return
     */
    String parm() default "";

    /**
     * 该项参数是否必填
     * @return
     */
    boolean required() default false;
}
