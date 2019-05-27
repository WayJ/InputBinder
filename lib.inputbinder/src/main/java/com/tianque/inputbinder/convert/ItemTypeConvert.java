package com.tianque.inputbinder.convert;

import com.tianque.inputbinder.exception.InputItemConvertException;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.model.InputItemProfile;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 输入项 类型转换器<BR/>
 * <p>
 * 两个泛型
 *
 * @param <In>  vo中的变量的类型
 * @param <Out> 这个输入项的 Inputitem 类型，必须是InputItem 的子类
 */
public abstract class ItemTypeConvert<In, Out extends InputItem> {
    public ItemTypeConvert() {
    }

    public boolean isThisItem(Class obj) {
        // 获取当前new的对象的泛型的父类类型
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        // 获取第一个类型参数的真实类型
        Class clazz = (Class<In>) pt.getActualTypeArguments()[0];
        if (clazz == obj)
            return true;
        else
            return false;
    }


    public Out create(int resId, InputItemProfile profile) throws InputItemConvertException {
        Out inputItem = null;
        try {
            Type type = null;
            boolean flag = false;
            Class clz = getClass();
            while (!flag && type == null) {
                if (clz.getSuperclass().getName().equals(ItemTypeConvert.class.getName())) {
                    type = clz.getGenericSuperclass();
                } else {
                    clz = clz.getSuperclass();
                    if(clz==null)
                        flag=true;
                }
            }
            ParameterizedType pType = (ParameterizedType) type;

            Type claz = pType.getActualTypeArguments()[1];
            if (claz instanceof Class) {
                Class<Out> clazz = (Class<Out>) claz;
                Constructor c = clazz.getConstructor(int.class);
                inputItem = (Out) c.newInstance(resId);
                inputItem.setRequestKey(profile.requestKey);
                inputItem.setInputItemProfile(profile);
                inputItem = loadProfile(inputItem, profile);
            }
            if (inputItem == null)
                throw new InputItemConvertException("初始化InputItem错误, viewName:" + profile.viewName);
        } catch (Exception e) {
            throw new InputItemConvertException("初始化InputItem错误, viewName:" + profile.viewName, e);
        }
        return inputItem;
    }

    public abstract Out loadProfile(Out inputItem, InputItemProfile profile);

//    public void setExtendItemValue(InputItem item, Object value){
//        setItemValue(((Item)item),(In)value);
//    }

}
