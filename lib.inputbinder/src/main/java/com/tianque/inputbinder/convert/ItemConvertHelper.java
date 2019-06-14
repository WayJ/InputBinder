package com.tianque.inputbinder.convert;

import com.tianque.inputbinder.convert.impl.ButtonInputConvert;
import com.tianque.inputbinder.convert.impl.CheckInputItemConvert;
import com.tianque.inputbinder.convert.impl.DateInputItemConvert;
import com.tianque.inputbinder.convert.impl.MultiOptionalInputItemConvert;
import com.tianque.inputbinder.convert.impl.OptionalInputItemConvert;
import com.tianque.inputbinder.convert.impl.TextInputConvert;
import com.tianque.inputbinder.item.InputItemType;

import java.util.HashMap;
import java.util.Map;

public final class ItemConvertHelper {
    private ItemConvertHelper() {
    }

    private static Map<String, Class<? extends ItemTypeConvert>> inputTypeConvertStore;

    static {
        inputTypeConvertStore = new HashMap<>();

        inputTypeConvertStore.put(InputItemType.Text.getValue()+"-adapter-Integer",  TextInputConvert.AdapterInt.class);
        inputTypeConvertStore.put(InputItemType.Text.getValue()+"-adapter-Long",  TextInputConvert.AdapterInt.class);
        inputTypeConvertStore.put(InputItemType.Text.getValue()+"-adapter-Double",  TextInputConvert.AdapterDouble.class);
        inputTypeConvertStore.put(InputItemType.Text.getValue()+"-adapter-String",  TextInputConvert.AdapterString.class);

        inputTypeConvertStore.put(InputItemType.Button.getValue()+"-adapter-String",  ButtonInputConvert.AdapterString.class);

        inputTypeConvertStore.put(InputItemType.Date.getValue()+"-adapter-String",  DateInputItemConvert.AdapterString.class);
        inputTypeConvertStore.put(InputItemType.Date.getValue()+"-adapter-Date",  DateInputItemConvert.AdapterDate.class);

        inputTypeConvertStore.put(InputItemType.Optional.getValue()+"-adapter-String",  OptionalInputItemConvert.AdapterString.class);
        inputTypeConvertStore.put(InputItemType.Optional.getValue()+"-adapter-Integer",  OptionalInputItemConvert.AdapterInt.class);

        inputTypeConvertStore.put(InputItemType.MultiOptional.getValue()+"-adapter-String",  MultiOptionalInputItemConvert.AdapterString.class);
        inputTypeConvertStore.put(InputItemType.MultiOptional.getValue()+"-adapter-Integer",  MultiOptionalInputItemConvert.AdapterInt.class);

        inputTypeConvertStore.put(InputItemType.CheckBox.getValue()+"-adapter-String",  CheckInputItemConvert.AdapterString.class);
        inputTypeConvertStore.put(InputItemType.CheckBox.getValue()+"-adapter-Integer",  CheckInputItemConvert.AdapterInt.class);
        inputTypeConvertStore.put(InputItemType.CheckBox.getValue()+"-adapter-Boolean",  CheckInputItemConvert.AdapterBoolean.class);
    }

    public static ItemTypeConvert getInputItemConvert(String convertName){
        try {
            Class clz = inputTypeConvertStore.get(convertName);
            if(clz!=null)
                return inputTypeConvertStore.get(convertName).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param fieldTypeName 复杂类型，自己的类型，传递SimpleName，如PropertyDict
     */
    public static void addDefaultItemConvert(String  fieldTypeName,Class<? extends ItemTypeConvert> clz){
        ItemConvertHelper.inputTypeConvertStore.put("-adapter-"+fieldTypeName,  clz);
    }

}
