package com.tianque.inputbinder;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;

import com.tianque.inputbinder.convert.ItemTypeConvert;
import com.tianque.inputbinder.exception.InputItemConvertException;
import com.tianque.inputbinder.inf.InputItemHand;
import com.tianque.inputbinder.inf.InputVerifyFailedException;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.model.BeanReader;
import com.tianque.inputbinder.model.InputReaderInf;
import com.tianque.inputbinder.convert.ItemConvertHelper;
import com.tianque.inputbinder.model.InputItemProfile;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.util.ResourceUtils;
import com.tianque.inputbinder.util.ToastUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InputBinderEngine {
    private final String Tag = "InputBinderEngine";

    private View rootView;
    private Context mContext;

    private Map<String, InputItem> inputItems = new HashMap<>();//用户主动添加的inputItem，主要用户查看表单时候和用户主动控制
    // mAttrs 对象的key是 viewId
//    private LinkedHashMap<Integer, InputItem> itemsPutByViewId;


    //    private Map<String, InputItem> mAutoInputItems;//


    private InputReaderInf inputReader;
//    private CallBack callBack;
    private InputValidateHelper inputValidateHelper = new InputValidateHelper();

    /**
     * The map to store the request parameters.
     */
    private Map<String, String> mRequestParams;
    private HashMap<String, String> mExtraRequestParams = new HashMap<>();

    public InputBinderEngine(Context context) {
        this.mContext = context;
    }

    public void attachView(View rootView) {
        this.rootView = rootView;
    }

    public void start() throws Exception {
        if (rootView == null) {
            throw new RuntimeException("rootview is null");
        }
        if (inputReader == null) {
            throw new RuntimeException("inputReader is null");
        }
//        if (callBack != null)
//            callBack.onStart(this);
        setUp(inputReader.read());
        binderView();
    }

    public void setInputReader(InputReaderInf reader) {
        this.inputReader = reader;
    }

    public InputReaderInf getInputReader() {
        return inputReader;
    }

    private Map<String, String> temRequest;

    public void setTemRequest(Map<String, String> temRequest) {
        this.temRequest = temRequest;
    }

//    public void addInputItems(Map<String, InputItem> itemMap) {
//        this.inputItems.putAll(itemMap);
//    }

    public void addInputItems(List<InputItem> items) {
        if (items != null) {
            for (InputItem item : items) {
                String resName = mContext.getResources().getResourceName(item.getResourceId());
                if (resName.contains("id/")) {
                    resName = resName.substring(resName.lastIndexOf("id/") + 3);
                }
                this.inputItems.put(resName, item);
            }
        }
    }

    public void addInputItem(String key, InputItem inputItem) {
        this.inputItems.put(key, inputItem);
    }

    private void setUp(List<InputItemProfile> profiles) {
        Logging.d(Tag, profiles.toString());
        for (InputItemProfile profile : profiles) {
            profile.viewId = ResourceUtils.findIdByName(mContext, profile.viewName != null ? profile.viewName : profile.key);
            if (profile.viewId <= 0) {
                Logging.e(Tag, "item:" + profile.key + "；viewName:" + profile.viewName + ",无法找到对应视图");
                continue;
            }

            if(profile.getItemTypeConvert()==null){
//                throw new InputItemConvertException("初始化InputItem错误, viewName:"+profile.viewName);
            }else{
                InputItem inputItem = profile.getItemTypeConvert().create(profile.viewId,profile);
                inputItems.put(profile.key,inputItem);
            }
        }
    }

    private void binderView() {
        for (Map.Entry<String, InputItem> entry : inputItems.entrySet()) {
            InputItem item = entry.getValue();
            InputItemProfile profile = item.getInputItemProfile();
            View view = rootView.findViewById(item.getResourceId());
            if (view != null) {
//                execute(view, attr, item);
//                item.refreshView();
                item.setView(view);
                item.setInputItemHand(inputItemHand);
                item.onStart();
                //添加缓存数据
                putRequestParams(item);
            } else {
                ToastUtils.showDebugToast("item:" + profile.key + "；viewName:" + profile.viewName + ",无法找到对应视图");
                Logging.e(Tag, "item:" + profile.key + "；viewName:" + profile.viewName + ",无法找到对应视图");
            }
        }
        if (inputItems != null)
            Logging.d(Tag, inputItems.toString());
    }


//    public InputItem getInputItemByViewId(int viewId) {
//        return itemsPutByViewId.get(viewId);
//    }

//    public InputItem getInputItemByViewAttr(ViewAttribute config) {
//        return inputItems.get(config.key);
//    }

//    /**
//     * 为View设置显示信息
//     *
//     * @param view
//     * @param attr
//     */
//    public void execute(View view, ViewAttribute attr, InputItem item) {
//        if (view == null) {
//            Logging.d("", "View with key " + attr.key + " is not found");
//            return;
//        }
//        if (attr == null) {
//            return;
//        }
//        addValidateData(attr);
//
//        item.refreshView();
//    }

//    /**
//     * 添加表单提交时验证的参数
//     *
//     * @param attr view的相关属性对象
//     */
//    private void addValidateData(ViewAttribute attr) {
//        inputValidateHelper.put(attr.requestKey, attr);
//    }


    public Map<String, String> getRequestParams(boolean rebuild) {
        return (!rebuild && (mRequestParams != null)) ? mRequestParams : getRequestParams();
    }


    public Map<String, String> getRequestParams() {
        if (mRequestParams == null) {
            mRequestParams = new HashMap<>();
        } else {
            mRequestParams.clear();
        }
        Collection<InputItem> items = inputItems.values();
        for (InputItem inputItem : items) {
            if (TextUtils.isEmpty(inputItem.getRequestKey()))
                continue;
            //放数据
//            if (inputItem.getInputType() == BehaviorType.Text || inputItem.getInputType() == BehaviorType.Button) {
//                View view = rootView.findViewById(inputItem.getResourceId());
//                String value;
//                if (view == null) {
//                    continue;
//                }
//                if (view.getVisibility() != View.VISIBLE) continue;
//                if (view instanceof TextView) {
//                    value = ((TextView) view).getText().toString();
//                } else if (view instanceof ItemBoxBase) {
//                    value = ((ItemBoxBase) view).getContent();
//                }
//                inputItem.setRequestValue(value.trim());
//            }
            if(inputItem.getView()==null)
                continue;
            String requestValue = inputItem.getRequestValue();
            String requestKey = inputItem.getRequestKey();
//            //以防止某些参数值为空时不传,导致后台报错,例如出租房的证件类型
//            if (requestValue == null) {
//                requestValue = "";
//                inputItem.setRequestValue(requestValue);
//            }
            if (TextUtils.isEmpty(requestKey)) {
                continue;
            }
            mRequestParams.put(requestKey, requestValue);
        }

        mRequestParams.putAll(mExtraRequestParams);
        /*
         * Remove the parameter which user wants to remove.
         */
//        if (mPendingRemoveParams != null) {
//            for (String requestKey : mPendingRemoveParams) {
//                mRequestParams.remove(requestKey);
//            }
//        }

        return mRequestParams;
    }

    public void setRequestParams(Map<String, String> mRequestParams) {
        this.mRequestParams = mRequestParams;
    }


    /**
     * 将上次的请求参数重新赋值到控件上
     */
    public void putRequestParams(InputItem inputItem) {
        if (temRequest == null || inputItem == null || temRequest.size() == 0)
            return;
        if (temRequest.containsKey(inputItem.getRequestKey())) {
            String value = temRequest.get(inputItem.getRequestKey());
            //Todo fix
//            inputItem.setRequestValue(value);
        }
    }

    public List<InputVerifyFailedException> verifyIsRegular() {
        return inputValidateHelper.validateRequestParams(inputItems.values());
    }

//    /**
//     * 多界面数据集中在同一提交界面提交
//     */
//    public boolean multiValidateRequestParams(Map<String, String> params, String name) {
//        if (mValidateMap == null || mValidateMap.size() == 0) {
//            if (mAttrs == null)
//                mAttrs = mModelLoader.read(mContext, name);
//            Collection<ViewAttribute> items = mAttrs.values();
//            for (ViewAttribute attribute : items) {
//                //当多界面提交，新增时没有进入某一个界面，但其界面还是存在必填项时，根据参数里面是否存不为空的必填
//                String requestKey = attribute.requestKey;
//                String paramsValue = params.get(requestKey);
//                if (attribute.required && (!params.containsKey(requestKey) || paramsValue == null || "".equals(paramsValue))) {
//                    return false;
//                }
//            }
//        } else {
//            return validateRequestParams(params);
//        }
//        return true;
//    }


    //    public void refreshViewById(int viewId) {
//        InputItem item = itemsPutByViewId.get(viewId);
//        item.refreshView();
//    }

    public void refreshView() {
        Iterator<Map.Entry<String, InputItem>> i = inputItems.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, InputItem> entry = i.next();
            InputItem item = entry.getValue();
            item.refreshView();
        }
        rootView.invalidate();
    }

//    public void ignoreRequired(String requestKey) {
//        if (!inputValidateHelper.ignoreRequired.contains(requestKey))
//            inputValidateHelper.ignoreRequired.add(requestKey);
//    }
//
//    public boolean restoreRequired(String requestKey) {
//        return inputValidateHelper.ignoreRequired.remove(requestKey);
//    }

    public SparseArray<Object> getSparseArrayData() {
        return null;
    }

    /**
     * 对所在Activity中所有在view_config注册过的view设置是否可编辑
     *
     * @param isEnable 是否可编辑
     */
    public void setAllViewEnable(boolean isEnable) {
        Iterator<Map.Entry<String, InputItem>> i = inputItems.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, InputItem> entry = i.next();
            InputItem item = entry.getValue();
            setViewEnabled(item.getView(), isEnable);
        }
    }

//    public void setViewEnable(int resourceId, boolean isEnable) {
//        View view = rootView.findViewById(resourceId);
//        setViewEnabled(view, isEnable);
//    }

    public void setViewEnabled(View view, boolean enable) {
        if(view==null)
            return;
        view.setFocusable(enable);
        view.setEnabled(enable);
        view.setClickable(enable);
    }

//    public void addTypeConvert(ItemTypeConvert itemTypeConvert) {
//        convertHelper.addTypeConvert(itemTypeConvert);
//    }

    public <T> void readStore(T obj) {
        if (getInputReader() instanceof BeanReader) {
            ((BeanReader) getInputReader()).readStore(obj, inputItems);
        }
    }


    protected InputItemHand inputItemHand = new InputItemHand() {
        @Override
        public InputItem findInputItemByViewName(String viewName) {
            return inputItems.get(viewName);
        }

        @Override
        public InputItem findInputItemByViewId(int viewId) {
            for (InputItem item : inputItems.values()) {
                if(item.getResourceId()==viewId)
                    return item;
            }
            return null;
        }

        @Override
        public View findViewById(int id) {
            return rootView.findViewById(id);
        }
    };


}
