package com.tianque.inputbinder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.tianque.inputbinder.function.QueryMapFunc;
import com.tianque.inputbinder.function.QueryObjFunc;
import com.tianque.inputbinder.function.UpdateMapFunc;
import com.tianque.inputbinder.function.UpdateObjFunc;
import com.tianque.inputbinder.inf.InputBinderStyleAction;
import com.tianque.inputbinder.item.ButtonInputItem;
import com.tianque.inputbinder.item.CheckInputItem;
import com.tianque.inputbinder.item.DateInputItem;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.item.InputItemType;
import com.tianque.inputbinder.item.MultiOptionalInputItem;
import com.tianque.inputbinder.item.OptionalInputItem;
import com.tianque.inputbinder.item.TextInputItem;
import com.tianque.inputbinder.model.BeanReader;
import com.tianque.inputbinder.model.XmlReader;
import com.tianque.inputbinder.util.Logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by way on 2018/3/5.
 */

public class InputBinder {
    public static Map<String,Class<? extends InputItem>> inputTypeStoreMap;
    private static InputBinderStyleAction inputBinderStyleAction;
    static{
        inputTypeStoreMap=new HashMap<>();
        inputTypeStoreMap.put(InputItemType.Text.getValue(), TextInputItem.class);
        inputTypeStoreMap.put(InputItemType.Button.getValue(), ButtonInputItem.class);
        inputTypeStoreMap.put(InputItemType.CheckBox.getValue(), CheckInputItem.class);
        inputTypeStoreMap.put(InputItemType.Date.getValue(), DateInputItem.class);
        inputTypeStoreMap.put(InputItemType.Optional.getValue(), OptionalInputItem.class);
        inputTypeStoreMap.put(InputItemType.MultiOptional.getValue(), MultiOptionalInputItem.class);
    }

    private Context context;
    private InputBinderEngine engine;
    List<InputItem> inputItems;

    public InputBinder(Activity activity) {
        this.context = activity;
        engine = new InputBinderEngine(context);
        setRootView(activity);
        inputItems = new ArrayList<>();
    }

    public InputBinder(Context context) {
        this.context = context;
        engine = new InputBinderEngine(context);
        inputItems = new ArrayList<>();
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
        inputItems.add(inputItem);
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
        engine.setInputReader(new BeanReader(modelCls));
        return this;
    }

    public void start(){
        engine.addInputItems(inputItems);
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

    public static InputBinderStyleAction getInputBinderStyleAction() {
        return inputBinderStyleAction;
    }

    public static void setInputBinderStyleAction(InputBinderStyleAction inputBinderStyleAction) {
        InputBinder.inputBinderStyleAction = inputBinderStyleAction;
    }

    public void doQuery(QueryMapFunc func){
        addSavedRequestMap(func.doQuery());
    }

    public void doQuery(QueryObjFunc func){
        Object object = func.doQuery();
        Logging.d(object.toString());
//        addSavedRequestMap(queryFunction.doQuery());
    }

    public void doUpdate(UpdateMapFunc func){
        func.doUpdate(getRequestMap());
    }

    public void doUpdate(UpdateObjFunc func){
        func.doUpdate(getRequestMap());
    }

}
