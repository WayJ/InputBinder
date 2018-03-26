package com.tianque.inputbinder.item;

import android.view.View;

import com.tianque.inputbinder.model.ViewAttribute;
import com.tianque.inputbinder.inf.ViewProxyInterface;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by way on 17/5/18.
 */

public abstract class InputItem<T>  {


    private final String GONE = "gone";
    private final String VISIBLE = "visible";
    private final String INVISIBLE = "invisible";

    private int resourceId;

    private ViewProxyInterface<T> viewProxy;
//    private String resourceName;

    private String requestKey;
//    protected ViewBehaviorInterface<T> actualViewBehavior;
    protected ViewAttribute viewAttribute;
    private Map<String,String> extMap;
    private View view;

    public InputItem() {

    }

    public InputItem(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

//    public String getResourceName() {
//        return resourceName;
//    }
//
//    public void setResourceName(String resourceName) {
//        this.resourceName = resourceName;
//    }

    public abstract T getDisplayText();
    public abstract String getRequestValue();
    public abstract void setRequestValue(String value);

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

//    public ViewBehaviorInterface<T> getViewBehavior() {
//        return actualViewBehavior;
//    }
//
//    public void setViewBehavior(ViewBehaviorInterface<T> viewBehavior) {
//        this.actualViewBehavior = viewBehavior;
//    }

    public ViewAttribute getViewAttribute() {
        return viewAttribute;
    }

    public void setViewAttribute(ViewAttribute viewAttribute) {
        this.viewAttribute = viewAttribute;
    }

    //    protected void setInputType(BehaviorType inputType) {
//        this.inputType = inputType;
//    }
//
//    public BehaviorType getInputType() {
//        return inputType;
//    }
//
//
//    public IRefreshViewListener getRefreshViewListener() {
//        return mRefreshViewListener;
//    }
//
//    public void setRefreshViewListener(IRefreshViewListener refreshViewListener) {
//        this.mRefreshViewListener = refreshViewListener;
//    }
//

    public ViewProxyInterface<T> getViewProxy() {
        return viewProxy;
    }

    public void setViewProxy(ViewProxyInterface<T> viewProxy) {
        this.viewProxy = viewProxy;
    }

    public void refreshView(){
        if(getViewProxy()!=null)
            getViewProxy().setContent(getDisplayText());
    }

    public Map<String, String> getExtMap() {
        return extMap;
    }

    public void setExtMap(Map<String, String> extMap) {
        this.extMap = extMap;
    }

    public void addExtConfig(String key,String value){
        if(extMap==null){
            extMap=new HashMap<>();
        }
        extMap.put(key,value);
    }

    public String getExtConfig(String key){
        if(extMap==null)
            return null;
        else
            return extMap.get(key);
    }

    /**
     * 给控件设置显示文字、绑定事件后会调用该方法
     */
    public void onStart(){
        if(getViewAttribute()!=null)
            setViewVisibleStatus(getViewAttribute().visible, view);
        if(viewProxy==null){
            if(getView() instanceof ViewProxyInterface){
                viewProxy=(ViewProxyInterface<T>) getView();
            }else
                viewProxy = initDefaultViewProxy(getView());
        }
    }

    public abstract ViewProxyInterface<T> initDefaultViewProxy(View view);

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
//        if(view instanceof ViewBehaviorInterface){
//            actualViewBehavior = (ViewBehaviorInterface)view;
//        }
    }

    private void setViewVisibleStatus(String visible, View view) {
        if(view==null)
            return;
        if (visible.equals(VISIBLE)) {
            view.setVisibility(View.VISIBLE);
        } else if (visible.equals(INVISIBLE)) {
            view.setVisibility(View.INVISIBLE);
        } else if (visible.equals(GONE)) {
            view.setVisibility(View.GONE);
        }
    }

}
