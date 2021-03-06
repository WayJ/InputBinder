package com.tianque.inputbinder;

import com.tianque.inputbinder.function.ContainerFunc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 用来控制多个编辑页面连结处理，推荐使用fragment来做单个页面，在外层activity中 调用register和unregister
 */
public class LinkedInputBinder {
    private static Map<String,InputBinderEngine> engineMap;

    public void register(){
        engineMap=new HashMap<>();
    }

    public void unregister(){
        engineMap.clear();
        engineMap = null;
    }

    public void addIBHold(String key,InputBinderEngine engine){
        engineMap.put(key,engine);
    }

    public void removeIBHold(String key){
        engineMap.remove(key);
    }

    public void getIBHold(String key){

    }

    public void doPush(ContainerFunc func){
        Map<String,String> map=new HashMap<>();
        if(engineMap!=null&&engineMap.size()>0){
            for (InputBinderEngine inputBinderEngine : engineMap.values()) {
                map.putAll(inputBinderEngine.getRequestParams());
            }
        }
        func.onPutOut(map);
    }




}
