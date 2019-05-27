package com.tianque.inputbinder.item;

import android.text.TextUtils;
import android.view.View;

import com.tianque.inputbinder.inf.InputItemHand;
import com.tianque.inputbinder.model.InputItemProfile;
import com.tianque.inputbinder.viewer.ViewContentProxy;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.util.ToastUtils;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by way on 17/5/18.<BR/>
 * 这里的InputItem<T>的泛型应该是给View使用来设置显示的值的
 */
public abstract class InputItem<T> {

    public static final String SEPARATOR = ",";
    private final String GONE = "gone";
    private final String VISIBLE = "visible";
    private final String INVISIBLE = "invisible";
    protected boolean isStarted;

    private int resourceId;

    private ViewContentProxy<T> viewProxy;
    private InputItemHand inputItemHand;
//    private String resourceName;

    private String requestKey;
    protected InputItemProfile inputItemProfile;
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

    public abstract T getContent();

    public abstract String getRequestValue();

//    public abstract void setRequestValue(String value);

    public String getRequestKey() {
        return requestKey;
    }

    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }

    public InputItemProfile getInputItemProfile() {
        return inputItemProfile;
    }

    public void setInputItemProfile(InputItemProfile inputItemProfile) {
        this.inputItemProfile = inputItemProfile;
    }

    public ViewContentProxy<T> getViewProxy() {
        return viewProxy;
    }

    public void setViewProxy(ViewContentProxy<T> viewProxy) {
        this.viewProxy = viewProxy;
    }

    public void refreshView(){
        if(getViewProxy()!=null)
            getViewProxy().setContent(getContent());
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
//        if(getViewAttribute()!=null)
//            setViewVisibleStatus(getViewAttribute().visible, view);
        if(viewProxy==null){
            if(getView() instanceof ViewContentProxy){
                viewProxy=(ViewContentProxy<T>) getView();
            }else
                viewProxy = initDefaultViewProxy(getView());
        }
        if(getInputItemProfile()!=null&& !TextUtils.isEmpty(getInputItemProfile().parm)){
            String  parmStr = getInputItemProfile().parm;
            try{
                JSONObject jsonObject=new JSONObject(parmStr);
                Iterator<String> keys =  jsonObject.keys();
                while (keys.hasNext()){
                    String key = keys.next();
                    addConfigParm(key,jsonObject.getString(key));
                }
            }catch (Exception e){
                Logging.e(e);
                ToastUtils.showDebugToast("转换JSON出错："+parmStr);
            }
        }
        isStarted=true;
        refreshView();
    }

    public abstract ViewContentProxy<T> initDefaultViewProxy(View view);

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
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

    public InputItemHand getInputItemHand() {
        return inputItemHand;
    }

    public void setInputItemHand(InputItemHand inputItemHand) {
        this.inputItemHand = inputItemHand;
    }

    public VerifySet verifySet(){
        return new VerifySet();
    }

    public class VerifySet{

        public VerifySet and(int... inputVerifyType) {
            Arrays.asList(inputVerifyType);
            return this;
        }

        public VerifySet and(List<Integer> inputVerifyType) {
            return this;
        }

        public VerifySet or(int... inputVerifyType) {
            Arrays.asList(inputVerifyType);
            return this;
        }
    }
}
