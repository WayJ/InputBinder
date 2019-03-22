package com.tianque.inputbinder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.function.ContainerFunc;
import com.tianque.inputbinder.inf.InputBinderStyleAction;
import com.tianque.inputbinder.inf.InputVerifyFailedException;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by way on 2018/3/5.
 */

public class InputBinder {
    public static Map<String, Class<? extends InputItem>> inputTypeStoreMap;
    private static InputBinderStyleAction inputBinderStyleAction;

    static {
        inputTypeStoreMap = new HashMap<>();
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

    private InputBinder(Activity activity) {
        this.context = activity;
        engine = new InputBinderEngine(context);
        attachView(activity);
        inputItems = new ArrayList<>();
    }

    private InputBinder(Context context) {
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

    public InputBinder addInputItems(List<InputItem> items, Map<String, String> request) {
        addInputItems(items);
        engine.setTemRequest(request);
        return this;
    }

    public InputBinder addInputItems(List<InputItem> items) {
        for (InputItem inputItem : items) {
            addInputItem(inputItem);
        }
        return this;
    }

    public InputBinder addInputItem(InputItem inputItem) {
        inputItems.add(inputItem);
        return this;
    }

    public InputBinder addSavedRequestMap(Map<String, String> request) {
        engine.setTemRequest(request);
        return this;
    }

    private InputBinder bindXml(int redId, String nodeName) {
        engine.setInputReader(new XmlReader(redId, nodeName));
        return this;
    }

    private InputBinder bindBean(Class modelCls) {
        engine.setInputReader(new BeanReader(modelCls));
        return this;
    }

    public void start() {
        engine.addInputItems(inputItems);
        engine.start();
    }

    public Map<String, String> getRequestMap() {
        return engine.getRequestParams();
    }

    public InputBinderEngine getEngine() {
        return engine;
    }

    private InputBinder attachView(Activity activity) {
        return attachView(activity.getWindow());
    }

    private InputBinder attachView(Window window) {
        return attachView(window.getDecorView());
    }

    private InputBinder attachView(View rootView) {
        engine.attachView(rootView);
        return this;
    }

    public static InputBinderStyleAction getInputBinderStyleAction() {
        return inputBinderStyleAction;
    }

    public static void setInputBinderStyleAction(InputBinderStyleAction inputBinderStyleAction) {
        InputBinder.inputBinderStyleAction = inputBinderStyleAction;
    }

    public <T> void putIn(T obj){
        try {
            if(getEngine().getInputReader().isSafe(obj)){
                getEngine().readStore(obj);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void putIn(Map<String,String> map) {
        addSavedRequestMap(map);
    }


    public void putOut(ContainerFunc func) {
        List<InputVerifyFailedException> exceptionList=verifyIsRegular();
        if(exceptionList!=null&&exceptionList.size()>0) {
          func.onVerifyFailed(exceptionList);
            return;
        }

        Map<String, String> map = getRequestMap();
        if (removedRequestParameters != null && removedRequestParameters.size() > 0) {
            Iterator<String> iterator = removedRequestParameters.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (map.containsKey(key))
                    map.remove(key);
            }
        }
        if (addedRequestParameters != null && addedRequestParameters.size() > 0) {
            map.putAll(addedRequestParameters);
        }
        func.onPutOut(map);
    }


    private Map<String, String> addedRequestParameters;

    public void addRequestParameter(String key, String value) {
        if (addedRequestParameters == null)
            addedRequestParameters = new HashMap<>();
        addedRequestParameters.put(key, value);
    }

    private Map<String, String> removedRequestParameters;

    public void removeRequestParameter(String key) {
        if (removedRequestParameters == null)
            removedRequestParameters = new HashMap<>();
        removedRequestParameters.put(key, null);
    }


    public void updateView(){
        getEngine().refreshView();
    }

    public List<InputVerifyFailedException> verifyIsRegular() {
        return getEngine().verifyIsRegular();
    }


    public InputItem findInputByViewId(int viewId){
        return getEngine().inputItemHand.findInputItemByViewId(viewId);
    }
    public InputItem findInputByViewName(String viewName){
        return getEngine().inputItemHand.findInputItemByViewName(viewName);
    }

    public static class Build {
        Context context;
        View view;
        InputBinder inputBinder;


        public Build(Activity activity) {
            this(activity.getWindow().getDecorView());
        }

        public Build(View view) {
            this.context = view.getContext();
            this.view = view;
            inputBinder = new InputBinder(context)
                    .attachView(view);
        }

        public Build addTypeConvert(ItemTypeConvert itemTypeConvert) {
            inputBinder.getEngine().addTypeConvert(itemTypeConvert);
            return this;
        }


        public Build bindXml(int redId, String nodeName) {
            inputBinder.bindXml(redId, nodeName);
            return this;
        }

        public Build bindBean(Class modelCls) {
            inputBinder.bindBean(modelCls);
            return this;
        }

        public InputBinder create() {
            return inputBinder;
        }
    }
}
