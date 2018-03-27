package com.tianque.inputbinder.item;

import android.text.TextUtils;
import android.view.View;

import com.tianque.inputbinder.model.ViewAttribute;
import com.tianque.inputbinder.inf.ViewProxyInterface;
import com.tianque.inputbinder.util.ContextUtils;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.util.ToastUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
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
    private Map<String,String> configParmMap;//配置的参数的map
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

    public Map<String, String> getConfigParmMap() {
        return configParmMap;
    }

    public void setConfigParmMap(Map<String, String> extMap) {
        this.configParmMap = extMap;
    }

    public void addConfigParm(String key,String value){
        if(configParmMap==null){
            configParmMap=new HashMap<>();
        }
        configParmMap.put(key,value);
    }

    public String getConfigParm(String key){
        if(configParmMap==null)
            return null;
        else
            return configParmMap.get(key);
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
        if(getViewAttribute()!=null&& !TextUtils.isEmpty(getViewAttribute().parm)){
            String  parmStr = getViewAttribute().parm;
            try{
                JSONObject jsonObject=new JSONObject(parmStr);
                Iterator<String> keys =  jsonObject.keys();
                while (keys.hasNext()){
                    String key = keys.next();
                    addConfigParm(key,jsonObject.getString(key));
                }
            }catch (Exception e){
                Logging.e(e);
                ToastUtils.showDebugToast(ContextUtils.getApplicationContext(),"转换JSON出错："+parmStr);
            }
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
