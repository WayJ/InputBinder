package com.tianque.inputbinder.convert;

import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.model.ViewAttribute;

import java.lang.reflect.ParameterizedType;

public abstract class ItemTypeConvert<In,Item extends InputItem> {
    public ItemTypeConvert() {
    }

    public boolean isThisItem(Class obj) {
        // 获取当前new的对象的泛型的父类类型
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取第一个类型参数的真实类型
        Class clazz = (Class<In>) pt.getActualTypeArguments()[0];
        if(clazz==obj)
            return true;
        else
            return false;
    }



    public abstract Item newInputItem(int resId, ViewAttribute viewAttribute);

//    public void setExtendItemValue(InputItem item, Object value){
//        setItemValue(((Item)item),(In)value);
//    }


    public abstract void setItemValue(Item item,In value);
}
