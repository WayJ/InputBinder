package com.tianque.inputbinder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.tianque.inputbinder.function.PullMapFunc;
import com.tianque.inputbinder.function.PullObjFunc;
import com.tianque.inputbinder.function.PushMapFunc;
import com.tianque.inputbinder.function.PushObjFunc;
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
import java.util.Iterator;
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

    public void doPull(PullMapFunc func){
        addSavedRequestMap(func.doPull());
    }

    public void doPush(PushMapFunc func){
        Map<String,String> map=getRequestMap();
        if(removedRequestParameters!=null&&removedRequestParameters.size()>0){
            Iterator<String> iterator= removedRequestParameters.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                if(map.containsKey(key))
                    map.remove(key);
            }
        }
        if(addedRequestParameters!=null && addedRequestParameters.size()>0){
            map.putAll(addedRequestParameters);
        }
        func.doUpdate(map);
    }


    private Map<String,String> addedRequestParameters;
    public void addRequestParameter(String key, String value) {
        if(addedRequestParameters==null)
            addedRequestParameters=new HashMap<>();
        addedRequestParameters.put(key,value);
    }

    private Map<String,String> removedRequestParameters;
    public void removeRequestParameter(String key) {
        if(addedRequestParameters==null)
            addedRequestParameters=new HashMap<>();
        removedRequestParameters.put(key,null);
    }

    public boolean validateInputs() {
        return getEngine().validateRequestParams();
    }
}
