package com.tianque.inputbinder;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import java.util.Map.Entry;
import com.tianque.inputbinder.inf.InputItemHand;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.model.InputReaderInf;
import com.tianque.inputbinder.model.ViewAttribute;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.util.ResourceUtils;
import com.tianque.inputbinder.util.ToastUtils;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InputBinderEngine {
    private final String Tag = "InputBinderEngine";

    private View rootView;
    private Context mContext;

    private Map<String, InputItem> inputItems;//用户主动添加的inputItem，主要用户查看表单时候和用户主动控制
    // mAttrs 对象的key是 viewId
    private LinkedHashMap<Integer, InputItem> itemsPutByViewId;


    //    private Map<String, InputItem> mAutoInputItems;//
    private InputValidateHelper inputValidateHelper = new InputValidateHelper();

    private InputReaderInf inputReader;
    private CallBack callBack;


    /**
     * The map to store the request parameters.
     */
    private Map<String, String> mRequestParams;
    private HashMap<String, String> mExtraRequestParams = new HashMap<>();

    /**
     * 这里的key是控件对应的requestKey
     */

    private String mTempPrefix;

    public InputBinderEngine(Context context) {
        this(context, null);
    }

    public InputBinderEngine(Context context, String prefix) {
        this.mContext = context;
        if (!TextUtils.isEmpty(prefix))
            this.mTempPrefix = prefix;
        inputItems = new HashMap<>();
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }


    public void start() {
        if (rootView == null) {
            throw new RuntimeException("rootview is null");
        }
        if (inputReader == null) {
            throw new RuntimeException("inputReader is null");
        }

        if (callBack != null)
            callBack.onStart(this);
        binderView(inputReader.read());
    }

    public void setInputReader(InputReaderInf reader) {
        this.inputReader = reader;
    }

    private Map<String, String> temRequest;

    public void setTemRequest(Map<String, String> temRequest) {
        this.temRequest = temRequest;
    }

    public void addInputItems(Map<String, InputItem> itemMap) {
        this.inputItems.putAll(itemMap);
    }

    public void addInputItems(List<InputItem> items) {
        if (items != null) {
            for (InputItem item : items) {
                this.inputItems.put(mContext.getResources().getResourceName(item.getResourceId()), item);
            }
        }
    }

    public void addInputItem(String key, InputItem inputItem) {
        this.inputItems.put(key, inputItem);
    }

    private void setUp(List<ViewAttribute> attrs) {
        for (ViewAttribute attr : attrs) {
            attr.viewId = ResourceUtils.findIdByName(mContext, attr.viewName!=null?attr.viewName:attr.key);
            if(attr.viewId<=0){
                Logging.e(Tag, "item:" + attr.key + "；viewName:" + attr.viewName + ",无法找到对应视图");
                continue;
            }
            inputItems.put(attr.key, handleAttr(attr));
        }
    }

    private void binderView(List<ViewAttribute> attrs) {
        Logging.d(Tag, attrs.toString());
        setUp(attrs);
        for (Map.Entry<String, InputItem> entry : inputItems.entrySet()) {
            InputItem item = entry.getValue();
            ViewAttribute attr = item.getViewAttribute();
            View view = rootView.findViewById(item.getResourceId());
            if (view != null) {
                execute(view, attr, item);
            } else {
                ToastUtils.showDebugToast("item:" + attr.key + "；viewName:" + attr.viewName + ",无法找到对应视图");
                Logging.e(Tag, "item:" + attr.key + "；viewName:" + attr.viewName + ",无法找到对应视图");
            }
            item.setView(view);
            item.setInputItemHand(inputItemHand);
            item.onStart();
            //添加缓存数据
            putRequestParams(item);
        }
        if (inputItems != null)
            Logging.d(Tag, inputItems.toString());
    }

    private InputItem handleAttr(ViewAttribute attr) {
        InputItem item = null;
        //包名+属性key
        String key = attr.key;

//        String key = mModelLoader.getResourcePre() + ":id/" + attr.key;
        if (inputItems != null && inputItems.containsKey(key)) {
            item = inputItems.get(key);
//            if (item.getInputType() != attr.type) {
//                throw new RuntimeException("类型警告：" + key + "控件类型不匹配");
//            }
        } else {
            if (attr.type != null) {
                Class<? extends InputItem> cla = InputBinder.inputTypeStoreMap.get(attr.type);
                if (cla != null) {
                    try {
                        Constructor c = cla.getConstructor(int.class);//获取有参构造
                        item = (InputItem) c.newInstance(attr.viewId);    //通过有参构造创建对象
//                        item = cla.newInstance();
//                        if (item != null) {
//                            item.setResourceId(attr.viewId);
//                        } else {
//                            throw new RuntimeException("类型警告：" + key + " 的控件类型未找到");
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (item == null) {
            return item;
        }
//        item.setResourceName(attr.viewName);
        item.setRequestKey(attr.requestKey);
        item.setViewAttribute(attr);
        return item;

    }


    public InputItem getInputItemByViewId(int viewId) {
        return itemsPutByViewId.get(viewId);
    }

    public InputItem getInputItemByViewAttr(ViewAttribute config) {
        return inputItems.get(config.key);
    }

    /**
     * 为View设置显示信息
     *
     * @param view
     * @param attr
     */
    public void execute(View view, ViewAttribute attr, InputItem item) {
        if (view == null) {
            Logging.d("", "View with key " + attr.key + " is not found");
            return;
        }
        if(attr==null){
            return;
        }
        addValidateData(attr);

        item.refreshView();
    }

    /**
     * 添加表单提交时验证的参数
     *
     * @param attr view的相关属性对象
     */
    private void addValidateData(ViewAttribute attr) {
        inputValidateHelper.put(attr.requestKey, attr);
    }


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
            String requestValue = inputItem.getRequestValue();
            String requestKey = inputItem.getRequestKey();
            //以防止某些参数值为空时不传,导致后台报错,例如出租房的证件类型
            if (requestValue == null) {
                requestValue = "";
                inputItem.setRequestValue(requestValue);
            }
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
            inputItem.setRequestValue(value);
        }
    }

    public boolean validateRequestParams(Map<String, String> params) {
        return inputValidateHelper.validateRequestParams(params);
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


    public void refreshViewById(int viewId) {
        InputItem item = itemsPutByViewId.get(viewId);
        item.refreshView();
    }

    public void refreshView() {
        Iterator<Map.Entry<Integer, InputItem>> i = itemsPutByViewId.entrySet().iterator();
        inputItems = new HashMap<>();
        while (i.hasNext()) {
            Map.Entry<Integer, InputItem> entry = i.next();
            InputItem item = entry.getValue();
            item.refreshView();
        }
    }


    /**
     * If you want the required request parameter to be ignore in some cases,
     * call this method.
     *
     * @param requestKey The request parameter key.
     */
    public void ignoreRequired(String requestKey) {
        if (!inputValidateHelper.ignoreRequired.contains(requestKey))
            inputValidateHelper.ignoreRequired.add(requestKey);
    }

    public boolean restoreRequired(String requestKey) {
        return inputValidateHelper.ignoreRequired.remove(requestKey);
    }


    public SparseArray<Object> getSparseArrayData() {
        return null;
    }

    /**
     * 对所在Activity中所有在view_config注册过的view设置是否可编辑
     *
     * @param isEnable 是否可编辑
     */
    public void setAllViewEnable(boolean isEnable) {
        Iterator<Entry<String, ViewAttribute>> eIterator = InputValidateHelper.getmValidateMap().entrySet().iterator();
        while (eIterator.hasNext()) {
            Entry<String, ViewAttribute> entry = eIterator.next();
            View view = rootView.findViewById(entry.getValue().viewId);
            if (view.getVisibility() != View.VISIBLE)
                continue;
            setViewEnabled(view, isEnable);
        }
    }

    public void setViewEnable(int resourceId, boolean isEnable) {
        View view = rootView.findViewById(resourceId);
        setViewEnabled(view, isEnable);
    }

    public void setViewEnabled(View view, boolean enable) {
        view.setFocusable(enable);
        view.setEnabled(enable);
        view.setClickable(enable);
    }

//    public String getTempPrefix() {
//        return mTempPrefix;
//    }

//    public void setTempPrefix(String tempPrefix) {
//        this.mTempPrefix = tempPrefix;
//    }

//    public String getModelLoaderPrefix() {
//        return mModelLoader.getPrefix();
//    }


    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onStart(InputBinderEngine engine);
    }

    protected InputItemHand inputItemHand=new InputItemHand() {
        @Override
        public InputItem findInputItemByViewName(String viewName) {
            return inputItems.get(viewName);
        }

        @Override
        public View findViewById(int id) {
            return rootView.findViewById(id);
        }
    };
}
