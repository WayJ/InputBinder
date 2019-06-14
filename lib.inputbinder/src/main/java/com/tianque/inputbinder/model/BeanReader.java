package com.tianque.inputbinder.model;

import android.text.TextUtils;

import com.tianque.inputbinder.convert.CheckInput;
import com.tianque.inputbinder.convert.ItemConvertHelper;
import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.convert.OptionalInput;
import com.tianque.inputbinder.function.InputObserve;
import com.tianque.inputbinder.function.InputObserveImpl;
import com.tianque.inputbinder.inf.Input;
import com.tianque.inputbinder.inf.InputConvert;
import com.tianque.inputbinder.inf.RequestDataContract;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.item.InputItemType;
import com.tianque.inputbinder.util.Logging;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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
        List<InputItemProfile> inputItemProfiles = new ArrayList<>();
        List<Field> fieldList=new ArrayList<>();
        getFields(targetEntity,fieldList);

        for (Field f : fieldList) {

            //获取字段中包含fieldMeta的注解
            Annotation[] annotations = f.getAnnotations();

            InputItemProfile profile = new InputItemProfile();
            profile.key = f.getName();
            profile.field = f;

            for (Annotation annotation : annotations) {
                readAnnotation(annotation, profile);
            }

//            profile.verifyWarning = input.verifyWarning();
            inputItemProfiles.add(profile);
        }
        return inputItemProfiles;
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
                    Observable observable= null;

                    if(item.getInputItemProfile().getItemTypeConvert() instanceof RequestDataContract.IObjectDataConvert){
                        observable = ((RequestDataContract.IObjectDataConvert) item.getInputItemProfile().getItemTypeConvert()).requestConvertValueFromObject(value);
                    }

                    if(item instanceof RequestDataContract.RequestDataObserver &&observable!=null){
                        ((RequestDataContract.RequestDataObserver)item).postData(observable);
                    }
                }

                //add DataObserver 单项数据绑定  view-》pojo
                if(item instanceof RequestDataContract.RequestDataObservable){
                    ((RequestDataContract.RequestDataObservable)item).observe(new InputObserveImpl(attr.field,obj));
                }


                //添加缓存数据
//                putRequestParams(item);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void writeStore(T obj, Map<String, InputItem> inputItems) {
        for (Map.Entry<String, InputItem> entry : inputItems.entrySet()) {
            InputItem item = entry.getValue();
            InputItemProfile attr = item.getInputItemProfile();

            try {
                if (attr == null || attr.field == null)
                    continue;

                if(item instanceof RequestDataContract.getObjectValueFromInput){
                    Object o = ((RequestDataContract.getObjectValueFromInput)item).requestData();
                    attr.field.set(obj,o);
                }else
                    attr.field.set(obj,item.getContent());

            } catch (Exception e) {
                e.printStackTrace();
                Logging.e(e);
            }

        }
    }

    private void getFields(Class clz,List<Field> fieldList){
        if(clz==null)
            return;
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            //派出$change
            if (field.getName().startsWith("$"))
                continue;
            Input input = field.getAnnotation(Input.class);
            if(input!=null){
                fieldList.add(field);
            }

        }
        if(clz.getSuperclass()!=null){
            getFields(clz.getSuperclass(),fieldList);
        }
    }


    private void readAnnotation(Annotation annotation, InputItemProfile profile) throws Exception {
        if (annotation instanceof Input) {
            Input input = (Input) annotation;
            String convertTo = TextUtils.isEmpty(input.type().getValue()) ? input.typeExt() : input.type().getValue();

            String convertIn=profile.field.getType().getSimpleName();

            if(TextUtils.isEmpty(convertTo)) {
                if (profile.field.getAnnotation(CheckInput.class) != null)
                    convertTo = InputItemType.CheckBox.getValue();
                else if (profile.field.getAnnotation(OptionalInput.class) != null)
                    convertTo = InputItemType.Optional.getValue();
            }


            if(profile.field.getType() == String.class){
                convertIn="String";
                if(TextUtils.isEmpty(convertTo))
                    convertTo = InputItemType.Text.getValue();
            }else if(profile.field.getType() == double.class || profile.field.getType() == Double.class){
                convertIn="Double";
                if(TextUtils.isEmpty(convertTo))
                    convertTo = InputItemType.Text.getValue();
            }else if(profile.field.getType() == long.class || profile.field.getType() == Long.class){
                convertIn="Long";
                if(TextUtils.isEmpty(convertTo))
                    convertTo = InputItemType.Text.getValue();
            }else if(profile.field.getType() == int.class || profile.field.getType() == Integer.class){
                convertIn="Integer";
                if(TextUtils.isEmpty(convertTo))
                    convertTo = InputItemType.Text.getValue();
            }else if(profile.field.getType() == boolean.class || profile.field.getType() == Boolean.class){
                convertIn="Boolean";
                if(TextUtils.isEmpty(convertTo))
                    convertTo = InputItemType.CheckBox.getValue();
            }else if(profile.field.getType() == Date.class){
                convertIn="Date";
                if(TextUtils.isEmpty(convertTo))
                    convertTo = InputItemType.Date.getValue();
            }
            profile.setItemTypeConvert( ItemConvertHelper.getInputItemConvert(convertTo+"-adapter-"+convertIn));

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
