package com.tianque.inputbinder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.tianque.inputbinder.inf.ViewObserver;
import com.tianque.inputbinder.item.ButtonInputItem;
import com.tianque.inputbinder.item.CheckInputItem;
import com.tianque.inputbinder.item.DateInputItem;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.item.InputItemType;
import com.tianque.inputbinder.item.TextInputItem;
import com.tianque.inputbinder.model.ModelReader;
import com.tianque.inputbinder.model.XmlReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by way on 2018/3/5.
 */

public class InputBinder {
    public static Map<String,Class<? extends InputItem>> inputTypeStoreMap;
    static{
        inputTypeStoreMap=new HashMap<>();
        inputTypeStoreMap.put(InputItemType.Text.getValue(), TextInputItem.class);
        inputTypeStoreMap.put(InputItemType.Button.getValue(), ButtonInputItem.class);
        inputTypeStoreMap.put(InputItemType.CheckBox.getValue(), CheckInputItem.class);
        inputTypeStoreMap.put(InputItemType.Date.getValue(), DateInputItem.class);
    }

    private Context context;
    private InputBinderEngine engine;
    Map<String, InputItem> inputItemMap;

    public InputBinder(Activity activity) {
        this.context = activity;
        engine = new InputBinderEngine(context);
        setRootView(activity);
        inputItemMap = new HashMap<>();
    }

    public InputBinder(Context context) {
        this.context = context;
        engine = new InputBinderEngine(context);
        inputItemMap = new HashMap<>();
    }

    public InputBinder addInputItems(InputItem... items) {
        for (InputItem inputItem : items) {
            addInputItem(inputItem);
        }
        return this;
    }

    public InputBinder addInputItems(List<InputItem> items, Map<String,String> request) {
        addInputItems(items);
        engine.setTemRequest(request);
        return this;
    }

    public InputBinder addInputItems(List<InputItem> items){
        for (InputItem inputItem : items) {
            addInputItem(inputItem);
        }
        return this;
    }

    public InputBinder addInputItem(InputItem inputItem) {
        inputItemMap.put(context.getResources().getResourceName(inputItem.getResourceId()), inputItem);
        return this;
    }

    public InputBinder addSavedRequestMap(Map<String,String> request){
        engine.setTemRequest(request);
        return this;
    }

    public InputBinder setRelationXmlNode(int redId,String nodeName){
        engine.setInputReader(new XmlReader(redId,nodeName));
        return this;
    }

    public InputBinder setRelationEntity(Class modelCls){
        engine.setInputReader(new ModelReader(modelCls));
        return this;
    }

    public void start(){
        engine.setStoreInputItems(inputItemMap);
        engine.start();
    }

    public Map<String, String> getRequestMap() {
        return engine.getRequestParams();
    }

    public InputBinderEngine getEngine() {
        return engine;
    }

    public InputBinder setRootView(Activity activity){
        return setRootView(activity.getWindow());
    }
    public InputBinder setRootView(Window window){
        return setRootView(window.getDecorView());
    }
    public InputBinder setRootView(View rootView){
        engine.setRootView(rootView);
        return this;
    }

    public void setViewObserver(ViewObserver viewObserver) {
        engine.setViewObserver(viewObserver);
    }
}
