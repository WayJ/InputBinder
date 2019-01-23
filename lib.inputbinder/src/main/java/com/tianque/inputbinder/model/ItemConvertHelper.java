package com.tianque.inputbinder.model;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.item.InputItem;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ItemConvertHelper {

    List<ItemTypeConvert> itemTypeConverts;

    public void addTypeConvert(ItemTypeConvert itemTypeConvert) {
        if (itemTypeConverts == null)
            itemTypeConverts = new ArrayList<>();
        itemTypeConverts.add(itemTypeConvert);
    }

    public InputItem convert(ViewAttribute attr, InputItem inputItem) {
        if (inputItem != null) {
//            if (item.getInputType() != attr.type) {
//                throw new RuntimeException("类型警告：" + key + "控件类型不匹配");
//            }
        } else {
            if (attr.type != null) {
                Class<? extends InputItem> cla = InputBinder.inputTypeStoreMap.get(attr.type);
                if (cla != null) {
                    try {
                        Constructor c = cla.getConstructor(int.class);//获取有参构造
                        inputItem = (InputItem) c.newInstance(attr.viewId);    //通过有参构造创建对象
//                        item = cla.newInstance();
//                        if (item != null) {
//                            item.setResourceId(attr.viewId);
//                        } else {
//                            throw new RuntimeException("类型警告：" + key + " 的控件类型未找到");
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (itemTypeConverts != null && itemTypeConverts.size() > 0)
                        for (ItemTypeConvert itemTypeConvert : itemTypeConverts) {
                            if(itemTypeConvert.isThisItem(attr.field.getType())){
                                inputItem = itemTypeConvert.newInputItem(attr.viewId,attr);
                                break;
                            }
                        }
                }
            }
        }
        if (inputItem != null) {
//        item.setResourceName(attr.viewName);
            inputItem.setRequestKey(attr.requestKey);
            inputItem.setViewAttribute(attr);
        }
        return inputItem;

    }

    public void setExtendItemValue(InputItem item, Object value) {
        if (itemTypeConverts != null && itemTypeConverts.size() > 0)
            for (ItemTypeConvert itemTypeConvert : itemTypeConverts) {
                if(itemTypeConvert.isThisItem(item.getViewAttribute().field.getType())){
                    itemTypeConvert.setItemValue(item,value);
                    break;
                }
            }
    }
}
