package com.tianque.inputbinder.model;

import android.text.TextUtils;

import com.tianque.inputbinder.inf.Input;
import com.tianque.inputbinder.item.InputItemType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by way on 2018/3/5.
 */

public class BeanReader implements InputReaderInf{
    private Class targetEntity;

    public BeanReader(Class entity) {
        this.targetEntity = entity;
    }

    @Override
    public List<ViewAttribute> read(){
        if(targetEntity==null){
            throw new RuntimeException("read a null Class");
        }
        List<ViewAttribute> attributes = new ArrayList<>();
        Field[] fields = targetEntity.getDeclaredFields();
//
        for(Field f : fields){
            //获取字段中包含fieldMeta的注解
            Input input = f.getAnnotation(Input.class);
            if(input!=null){
                ViewAttribute viewAttribute=new ViewAttribute();
                viewAttribute.key = f.getName();
                viewAttribute.type = TextUtils.isEmpty(input.type().getValue())?input.typeExt():input.type().getValue();
                if(TextUtils.isEmpty(viewAttribute.type)){
                    // set type by field type
                    if(f.getType() == String.class){
                        viewAttribute.type = InputItemType.Text.getValue();
                    }else if(f.getType() == boolean.class||f.getType() == Boolean.class){
                        viewAttribute.type = InputItemType.CheckBox.getValue();
                    }else if(f.getType() == Date.class){
                        viewAttribute.type = InputItemType.Date.getValue();
                    }
                }

                viewAttribute.requestKey = TextUtils.isEmpty(input.requestKey())?f.getName():input.requestKey();
                viewAttribute.viewName = TextUtils.isEmpty(input.viewName())?f.getName():input.viewName();
                viewAttribute.parm = input.parm();
                attributes.add(viewAttribute);
            }
        }
        return attributes;
    }

}
