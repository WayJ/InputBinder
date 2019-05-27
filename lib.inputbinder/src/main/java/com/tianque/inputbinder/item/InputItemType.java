package com.tianque.inputbinder.item;

/**
 * Created by way on 17/5/18. <BR/>
 * Input的类型
 * 简单的配置转换器<BR/>
 * java的基本类型可以全部默认配置，无需设置该项<BR/>
 * Extend: 需要使用@InputConvert 注解设置自己实现的转换器类
 * Recursion: 递归读取配置项，即该项为一个实体类，读取该实体类内部的使用了@input注解的变量
 */
public enum InputItemType {
    NULL(""),Text("text"),Button("button"),CheckBox("checkbox"),Optional("optional"),
    MultiOptional("multioptional"),Extend("extend"),Date("date"),Recursion("recursion");


    private String value;

    InputItemType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static InputItemType get(String type){
        switch (type){
            case "text":
                return Text;
            case "button":
                return Button;
            case "checkbox":
                return CheckBox;
            case "optional":
                return Optional;
            case "multioptional":
                return MultiOptional;
            case "extend":
                return Extend;
            case "date":
                return Date;
            default:
                return Text;
        }
    }


}
