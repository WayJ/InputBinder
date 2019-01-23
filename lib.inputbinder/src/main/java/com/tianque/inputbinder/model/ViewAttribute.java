package com.tianque.inputbinder.model;


import java.lang.reflect.Field;

/**
 * Created by way on 2018/3/5.
 */

public class ViewAttribute {

    /**
     * 对应的实体类中的变量名
     */
    public String key = null;//name

    public String viewName = null;
    public Integer viewId;//resource id

    public Boolean required = false;
    public String requestKey = null;
    public String requestDefault = null;

    public String type;
    public Boolean optionalCallback = false;
    public String requiredRemind = "";
    public String validateMethoid = "";
    public Boolean addClearSelection = false;
    public String visible = "";
    public Integer maxLength;
    public Integer minLength;
    public String dependent;//依赖的控件，目前只做了checkbox的依赖，例如checkA选中后显示EditB，则为editB依赖了checkA
    public String parm;//扩展参数配置
//    public HashMap<String, HashMap<String, String>> method = new HashMap<String, HashMap<String, String>>();

//    public Class fieldClz; //从bean转换过来的，对应了item的变量类型
    public Field field; //从bean转换过来的，对应了item的变量


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof ViewAttribute) {
            if (((ViewAttribute) o).key.equals(key)) {
                return true;
            }
        }

        return false;
    }
}
