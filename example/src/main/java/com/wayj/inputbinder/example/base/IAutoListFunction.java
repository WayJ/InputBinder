package com.wayj.inputbinder.example.base;

/**
 * Created by way on 2017/9/20.
 */

public interface IAutoListFunction {
    public static final String KEY_ACTION_TYPE = "action";
    public static final String KEY_MODULE_NAME = "moduleName";
    public static final String IntentKey_ModuleCard = "IntentKey_ModuleCard";
    public String getListApi();


    public String getListData();
}
