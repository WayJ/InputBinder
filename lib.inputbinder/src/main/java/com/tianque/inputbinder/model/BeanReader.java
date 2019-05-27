package com.tianque.inputbinder.model;

import android.text.TextUtils;

import com.tianque.inputbinder.convert.ItemConvertHelper;
import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.inf.Input;
import com.tianque.inputbinder.inf.InputConvert;
import com.tianque.inputbinder.inf.RequestValueContract;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.item.InputItemType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Created by way on 2018/3/5.
 */

public class BeanReader<T> implements InputReaderInf<T> {
    private Class<T> targetEntity;

    public BeanReader(Class<T> entity) {
        this.targetEntity = entity;
    }

    @Override
    public List<InputItemProfile> read() throws Exception {
        if (targetEntity == null) {
            throw new RuntimeException("read a null Class");
        }
        List<InputItemProfile> attributes = new ArrayList<>();
        Field[] fields = targetEntity.getDeclaredFields();
//
        for (Field f : fields) {
            //派出$change
            if(f.getName().startsWith("$"))
                continue;

            //获取字段中包含fieldMeta的注解
            Annotation[] annotations = f.getAnnotations();

            InputItemProfile profile = new InputItemProfile();
            profile.key = f.getName();
            profile.field = f;

            for (Annotation annotation : annotations) {
                readAnnotation(annotation, profile);
            }


//            profile.verifyWarning = input.verifyWarning();
            attributes.add(profile);
        }
        return attributes;
    }

    @Override
    public boolean isSafe(T obj) {
        return true;
    }

    public void readStore(T obj, Map<String, InputItem> inputItems) {
        for (Map.Entry<String, InputItem> entry : inputItems.entrySet()) {
            InputItem item = entry.getValue();
            InputItemProfile attr = item.getInputItemProfile();

            try {
                if (attr == null || attr.field == null)
                    continue;
                attr.field.setAccessible(true);
                Object value = attr.field.get(obj);
                if (value != null) {
//                    if (TextUtils.isEmpty(attr.type) || attr.type.equals(InputItemType.Extend.getValue())) {
//                        convertHelper.setExtendItemValue(item, value);
//                    } else {
                    Observable observable= null;

                    if(item.getInputItemProfile().getItemTypeConvert() instanceof RequestValueContract.RequestValueObservable){
                        observable = ((RequestValueContract.RequestValueObservable) item.getInputItemProfile().getItemTypeConvert()).requestValue(value);
                    }

                    if(item instanceof RequestValueContract.RequestValueObserver &&observable!=null){
                        ((RequestValueContract.RequestValueObserver)item).onRequestValue(observable);
                    }

                }
                //添加缓存数据
//                putRequestParams(item);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private void readAnnotation(Annotation annotation, InputItemProfile profile) throws Exception {
        if (annotation instanceof Input) {
            Input input = (Input) annotation;
            String conver2type = TextUtils.isEmpty(input.type().getValue()) ? input.typeExt() : input.type().getValue();

            String convertIn="";
            String convertTo = conver2type;

            if(profile.field.getType() == String.class){
                convertIn="String";
                if(TextUtils.isEmpty(conver2type))
                    convertTo = InputItemType.Text.getValue();
            }else if(profile.field.getType() == double.class || profile.field.getType() == Double.class){
                convertIn="Double";
                if(TextUtils.isEmpty(conver2type))
                    convertTo = InputItemType.Text.getValue();
            }else if(profile.field.getType() == long.class || profile.field.getType() == Long.class){
                convertIn="Long";
                if(TextUtils.isEmpty(conver2type))
                    convertTo = InputItemType.Text.getValue();
            }else if(profile.field.getType() == int.class || profile.field.getType() == Integer.class){
                convertIn="Integer";
                if(TextUtils.isEmpty(conver2type))
                    convertTo = InputItemType.Text.getValue();
            }else if(profile.field.getType() == boolean.class || profile.field.getType() == Boolean.class){
                convertIn="Boolean";
                if(TextUtils.isEmpty(conver2type))
                    convertTo = InputItemType.CheckBox.getValue();
            }else if(profile.field.getType() == Date.class){
                convertIn="Date";
                if(TextUtils.isEmpty(conver2type))
                    convertTo = InputItemType.Date.getValue();
            }
            profile.setItemTypeConvert( ItemConvertHelper.getInputItemConvert(convertTo+" adapter "+convertIn));

            profile.requestKey = TextUtils.isEmpty(input.requestKey()) ? profile.field.getName() : input.requestKey();
            profile.viewName = TextUtils.isEmpty(input.viewName()) ? profile.field.getName() : input.viewName();
            profile.parm = input.parm();
            profile.verify = input.verify();
        } else if (annotation instanceof InputConvert) {
            InputConvert inputConvert = (InputConvert) annotation;
            Class convertClz = inputConvert.value();

            ItemTypeConvert itemTypeConvert = (ItemTypeConvert) convertClz.newInstance();
            profile.setItemTypeConvert(itemTypeConvert);

        }
    }

}
