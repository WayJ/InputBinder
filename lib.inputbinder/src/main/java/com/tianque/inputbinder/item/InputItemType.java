package com.tianque.inputbinder.item;

/**
 * Created by way on 17/5/18.
 */

public enum InputItemType {
    NULL(""),Text("text"),Button("button"),CheckBox("checkbox"),Optional("optional"),MultiOptional("multioptional"),Extend("extend"),Date("date");


    private String value;

    InputItemType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

//    public static InputItemType get(String type){
//        switch (type){
//            case "text":
//                return Text;
//            case "button":
//                return Button;
//            case "checkbox":
//                return CheckBox;
//            case "optional":
//                return Optional;
//            case "multioptional":
//                return MultiOptional;
//            case "extend":
//                return Extend;
//            default:
//                return Text;
//        }
//    }


}
