package com.tianque.inputbinder.model;

import android.text.TextUtils;
import android.view.View;

import com.tianque.inputbinder.inf.Input;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.item.InputItemType;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.util.ToastUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by way on 2018/3/5.
 */

public class BeanReader<T> implements InputReaderInf<T> {
    private Class<T> targetEntity;

    public BeanReader(Class<T> entity) {
        this.targetEntity = entity;
    }

    @Override
    public List<ViewAttribute> read() {
        if (targetEntity == null) {
            throw new RuntimeException("read a null Class");
        }
        List<ViewAttribute> attributes = new ArrayList<>();
        Field[] fields = targetEntity.getDeclaredFields();
//
        for (Field f : fields) {
            //获取字段中包含fieldMeta的注解
            Input input = f.getAnnotation(Input.class);
            if (input == null) {
                continue;
            }
            ViewAttribute viewAttribute = new ViewAttribute();
            viewAttribute.key = f.getName();
            viewAttribute.type = TextUtils.isEmpty(input.type().getValue()) ? input.typeExt() : input.type().getValue();
            if (TextUtils.isEmpty(viewAttribute.type)) {
                // set type by field type
                if (f.getType() == String.class || f.getType() == double.class || f.getType() == Double.class
                        || f.getType() == long.class || f.getType() == Long.class
                        || f.getType() == int.class || f.getType() == Integer.class) {
                    viewAttribute.type = InputItemType.Text.getValue();
                } else if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                    viewAttribute.type = InputItemType.CheckBox.getValue();
                } else if (f.getType() == Date.class) {
                    viewAttribute.type = InputItemType.Date.getValue();
                }
            }

            viewAttribute.requestKey = TextUtils.isEmpty(input.requestKey()) ? f.getName() : input.requestKey();
            viewAttribute.viewName = TextUtils.isEmpty(input.viewName()) ? f.getName() : input.viewName();
            viewAttribute.parm = input.parm();
            viewAttribute.required = input.required();
            viewAttribute.field = f;
            attributes.add(viewAttribute);
        }
        return attributes;
    }

    @Override
    public boolean isSafe(T obj) {
        return true;
    }

    public void readStore(T obj, Map<String, InputItem> inputItems, ItemConvertHelper convertHelper) {
        for (Map.Entry<String, InputItem> entry : inputItems.entrySet()) {
            InputItem item = entry.getValue();
            ViewAttribute attr = item.getViewAttribute();

            try {
                if (attr == null || attr.field == null)
                    continue;
                attr.field.setAccessible(true);
                Object value = attr.field.get(obj);
                if (value != null) {
                    if (TextUtils.isEmpty(attr.type) || attr.type.equals(InputItemType.Extend.getValue())) {
                        convertHelper.setExtendItemValue(item, value);
                    } else {
                        item.setRequestValue(value.toString());
                    }
                }
                //添加缓存数据
//                putRequestParams(item);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
