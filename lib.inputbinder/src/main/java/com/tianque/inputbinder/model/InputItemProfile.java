package com.tianque.inputbinder.model;


import com.tianque.inputbinder.convert.ItemTypeConvert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by way on 2018/3/5.
 */

public class InputItemProfile {

    /**
     * 对应的实体类中的变量名
     */
    public String key = null;//name

    public String viewName = null;
    public Integer viewId;//resource id

    public int verify = 0;
//    public String verifyWarning = "";
    public String requestKey = null;
//    public String requestDefault = null;

//    public String type;
//    public Boolean optionalCallback = false;
//    public String requiredRemind = "";
    public String validateMethod = "";
//    public Boolean addClearSelection = false;
//    public String visible = "";
//    public Integer maxLength;
//    public Integer minLength;
    public String dependent;//依赖的控件，目前只做了checkbox的依赖，例如checkA选中后显示EditB，则为editB依赖了checkA
    public String parm;//扩展参数配置
//    public HashMap<String, HashMap<String, String>> method = new HashMap<String, HashMap<String, String>>();

//    public Class fieldClz; //从bean转换过来的，对应了item的变量类型
    public Field field; //从bean转换过来的，对应了item的变量

    private ItemTypeConvert itemTypeConvert;

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof InputItemProfile) {
            if (((InputItemProfile) o).key.equals(key)) {
                return true;
            }
        }

        return false;
    }

    public ItemTypeConvert getItemTypeConvert() {
        return itemTypeConvert;
    }

    public void setItemTypeConvert(ItemTypeConvert itemTypeConvert) {
        this.itemTypeConvert = itemTypeConvert;
    }

    public Annotation getAnnotation(Class clz){
        return field.getAnnotation(clz);
    }
}
