package com.tianque.inputbinder.convert;

import com.tianque.inputbinder.convert.impl.CheckInputItemConvert;
import com.tianque.inputbinder.convert.impl.MultiOptionalInputItemConvert;
import com.tianque.inputbinder.convert.impl.OptionalInputItemConvert;
import com.tianque.inputbinder.convert.impl.TextInputConvert;
import com.tianque.inputbinder.item.InputItemType;

import java.util.HashMap;
import java.util.Map;

public final class ItemConvertHelper {
    private ItemConvertHelper() {
    }

    public static Map<String, Class<? extends ItemTypeConvert>> inputTypeConvertStore;

    static {
        inputTypeConvertStore = new HashMap<>();

        inputTypeConvertStore.put(InputItemType.Text.getValue()+" adapter Integer",  TextInputConvert.AdapterInt.class);
        inputTypeConvertStore.put(InputItemType.Text.getValue()+" adapter Long",  TextInputConvert.AdapterInt.class);
        inputTypeConvertStore.put(InputItemType.Text.getValue()+" adapter Double",  TextInputConvert.AdapterDouble.class);
        inputTypeConvertStore.put(InputItemType.Text.getValue()+" adapter String",  TextInputConvert.AdapterString.class);

        inputTypeConvertStore.put(InputItemType.Button.getValue()+" adapter String",  TextInputConvert.AdapterInt.class);

        inputTypeConvertStore.put(InputItemType.Optional.getValue()+" adapter String",  OptionalInputItemConvert.AdapterString.class);
        inputTypeConvertStore.put(InputItemType.Optional.getValue()+" adapter Integer",  OptionalInputItemConvert.AdapterInt.class);

        inputTypeConvertStore.put(InputItemType.MultiOptional.getValue()+" adapter String",  MultiOptionalInputItemConvert.AdapterString.class);
        inputTypeConvertStore.put(InputItemType.MultiOptional.getValue()+" adapter Integer",  MultiOptionalInputItemConvert.AdapterInt.class);

        inputTypeConvertStore.put(InputItemType.CheckBox.getValue()+" adapter String",  CheckInputItemConvert.AdapterString.class);
        inputTypeConvertStore.put(InputItemType.CheckBox.getValue()+" adapter Integer",  CheckInputItemConvert.AdapterInt.class);
        inputTypeConvertStore.put(InputItemType.CheckBox.getValue()+" adapter Boolean",  CheckInputItemConvert.AdapterBoolean.class);
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

//    List<ItemTypeConvert> itemTypeConverts;

//    public void addTypeConvert(ItemTypeConvert itemTypeConvert) {
//        if (itemTypeConverts == null)
//            itemTypeConverts = new ArrayList<>();
//        itemTypeConverts.add(itemTypeConvert);
//    }

//    private InputItem convert(InputItemProfile attr, InputItem inputItem) throws InputItemConvertException {
//        if (inputItem != null) {
////            if (item.getInputType() != attr.type) {
////                throw new RuntimeException("类型警告：" + key + "控件类型不匹配");
////            }
//        } else {
//            if (attr.type != null) {
//                Class<? extends InputItem> cla = InputBinder.inputTypeConvertStore.get(attr.type);
//                if (cla != null) {
//                    try {
//                        Constructor c = cla.getConstructor(int.class);//获取有参构造
//                        inputItem = (InputItem) c.newInstance(attr.viewId);    //通过有参构造创建对象
////                        item = cla.newInstance();
////                        if (item != null) {
////                            item.setResourceId(attr.viewId);
////                        } else {
////                            throw new RuntimeException("类型警告：" + key + " 的控件类型未找到");
////                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    if (itemTypeConverts != null && itemTypeConverts.size() > 0) {
//                        for (ItemTypeConvert itemTypeConvert : itemTypeConverts) {
//                            if (itemTypeConvert.isThisItem(attr.field.getType())) {
//                                inputItem = itemTypeConvert.newInputItem(attr.viewId, attr);
//                                break;
//                            }
//                        }
//                    }
//                    if(inputItem==null)
//                        throw new InputItemConvertException("初始化InputItem错误,未找到对于的转换器, viewName:"+attr.viewName);
//                }
//            }
//        }
//        if (inputItem != null) {
////        item.setResourceName(attr.viewName);
//            inputItem.setRequestKey(attr.requestKey);
//            inputItem.setInputItemProfile(attr);
//            return inputItem;
//        }else
//            throw new InputItemConvertException("初始化InputItem错误, viewName:"+attr.viewName);
//
//    }

//    public void setExtendItemValue(InputItem item, Object value) {
//        if (itemTypeConverts != null && itemTypeConverts.size() > 0)
//            for (ItemTypeConvert itemTypeConvert : itemTypeConverts) {
//                if(itemTypeConvert.isThisItem(item.getInputItemProfile().field.getType())){
//                    itemTypeConvert.setItemValue(item,value);
//                    break;
//                }
//            }
//    }
}
