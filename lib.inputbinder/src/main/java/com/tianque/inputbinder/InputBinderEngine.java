package com.tianque.inputbinder;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.tianque.inputbinder.inf.ViewObserver;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.model.InputReaderInf;
import com.tianque.inputbinder.model.ModelReader;
import com.tianque.inputbinder.model.ViewAttribute;
import com.tianque.inputbinder.model.XmlReader;
import com.tianque.inputbinder.util.ResourceUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InputBinderEngine {
    private final String Tag = "InputBinderEngine";
    private final String GONE = "gone";
    private final String VISIBLE = "visible";
    private final String INVISIBLE = "invisible";

    private View rootView;
    private Context mContext;

    private Map<String, InputItem> mStoreInputItems;//用户主动添加的inputItem，主要用户查看表单时候和用户主动控制
    // mAttrs 对象的key是 viewId
    private LinkedHashMap<Integer, InputItem> itemsPutByViewId;

    private ViewObserver viewObserver;

    private Map<String, InputItem> mAutoInputItems;//
    private InputValidateHelper inputValidateHelper = new InputValidateHelper();

    private InputReaderInf inputReader;
    private CallBack callBack;


    /**
     * The map to store the request parameters.
     */
    private Map<String, String> mRequestParams;
    private HashMap<String, String> mExtraRequestParams = new HashMap<>();
//    private ArrayList<String> mPendingRemoveParams;
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
    }

    public void setRootView(View rootView){
        this.rootView =rootView;
    }


    public void start(){
        if(rootView==null){
            throw new RuntimeException("rootview is null");
        }
        if(inputReader==null){
            throw new RuntimeException("inputReader is null");
        }

        if(callBack!=null)
            callBack.onStart(this);
        binderView(inputReader.read());
    }

    public void setInputReader(InputReaderInf reader){
        this.inputReader = reader;
    }

    private Map<String, String> temRequest;

    public void setTemRequest(Map<String, String> temRequest) {
        this.temRequest = temRequest;
    }

    public void setStoreInputItems(Map<String, InputItem> itemMap) {
        this.mStoreInputItems = itemMap;
    }

    public void setStoreInputItem(String key, InputItem inputItem) {
        if (mStoreInputItems == null) {
            mStoreInputItems = new HashMap<>();
        }
        mStoreInputItems.put(key, inputItem);
    }

    private void binderView(List<ViewAttribute> attrs) {
        if(isDebug())
            Log.d(Tag,attrs.toString());
        mAutoInputItems = new HashMap<>();
        for (ViewAttribute attr : attrs) {
            attr.viewId = ResourceUtil.findIdByName(mContext, attr.viewName);
            View view = rootView.findViewById(attr.viewId);
            try {
                if (view != null) {
                    setViewVisibleStatus(attr.visible, view);
                    handleView(attr, view);
                }else{
                    if(isDebug())
                        Log.d(Tag,"item:"+attr.key+"；viewName:"+attr.viewName+",无法找到对应视图");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(isDebug())
            Log.d(Tag,mStoreInputItems.toString());
    }

    private void handleView(ViewAttribute attr, View view) {
        InputItem item = null;
        //包名+属性key
        String key = attr.key;
//        String key = mModelLoader.getResourcePre() + ":id/" + attr.key;
        if (mStoreInputItems != null && mStoreInputItems.containsKey(key)) {
            item = mStoreInputItems.get(key);
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
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (item == null) {
            return;
        }
//        item.setResourceName(attr.viewName);
        item.setRequestKey(attr.requestKey);
        item.setViewAttribute(attr);
        item.setView(view);
        //添加缓存数据
        putRequestParams(item);
        mAutoInputItems.put(attr.key, item);
        executeBehavior(view, attr, item);

    }


    public InputItem getInputItemByViewId(int viewId) {
        return itemsPutByViewId.get(viewId);
    }

    public InputItem getInputItemByViewAttr(ViewAttribute config) {
        return mAutoInputItems.get(config.key);
    }

    /**
     * 为View设置显示信息
     *
     * @param view
     * @param attr
     */
    public void executeBehavior(View view, ViewAttribute attr, InputItem item) {
        if (view == null) {
            Log.d("", "View with key " + attr.key + " is not found");
            return;
        }
        addValidateData(attr);

        if (!TextUtils.isEmpty(attr.extmap)) {
            String[] extmap;
            if (attr.extmap.contains(";")) {
                extmap = attr.extmap.split(";");
            } else {
                extmap = new String[1];
                extmap[0] = attr.extmap;
            }
            for (String mapStr : extmap) {
                if (!mapStr.contains(":")) {
//                    TQLogUtils.e("extmap 配置的key和value必须以 : 符号间隔，多个配置之间以 ; 符号分割");
                    continue;
                }
                String[] keyValue = mapStr.split(":");
                item.addExtConfig(keyValue[0], keyValue[1]);
            }
        }

//        if (!TextUtils.isEmpty(attr.dependent)) {
//            String viewName = attr.dependent;
//            InputItem inputItem;
//            if (viewName.startsWith("-")) {
//                inputItem = mAutoInputItems.get(viewName.substring(1));
//            } else {
//                inputItem = mAutoInputItems.get(viewName);
//            }
//            if (inputItem == null) {
//                throw new RuntimeException("R.id." + attr.key + " 控件所依赖的 R.id." + attr.dependent + "未找到，必须先定义被依赖控件，请检查layout文件和viewconfig文件");
//            } else if (!(inputItem instanceof CheckInputItem)) {
//                throw new RuntimeException("R.id." + attr.key + " 控件所依赖的 R.id." + attr.dependent + " 只能是CheckInputItem");
//            }
//            if (viewName.startsWith("-")) {
//                ((CheckInputItem) inputItem).addDependedCloseView(view);
//                view.setVisibility(((CheckInputItem) inputItem).isChecked() ? View.GONE : View.VISIBLE);
//            } else {
//                ((CheckInputItem) inputItem).addDependedView(view);
//                view.setVisibility(((CheckInputItem) inputItem).isChecked() ? View.VISIBLE : View.GONE);
//            }
//        }

        item.refreshView();
    }


    private void setViewVisibleStatus(String visible, View view) {
        if (visible.equals(VISIBLE)) {
            view.setVisibility(View.VISIBLE);
        } else if (visible.equals(INVISIBLE)) {
            view.setVisibility(View.INVISIBLE);
        } else if (visible.equals(GONE)) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 添加表单提交时验证的参数
     *
     * @param attr view的相关属性对象
     */
    private void addValidateData(ViewAttribute attr) {
        inputValidateHelper.put(attr.requestKey, attr);
    }

    /**
     * Get the request parameters.The difference from
     * {@link #getRequestParams()} is this method supports get the request
     * parameters without rebuilding if you pass true into the method.
     *
     * @param rebuild true to return the parameters map if it has built previous.
     * @return
     */
    public Map<String, String> getRequestParams(boolean rebuild) {
        return (!rebuild && (mRequestParams != null)) ? mRequestParams : getRequestParams();
    }

    /**
     * Get the request parameters.
     */
    public Map<String, String> getRequestParams() {
        if (mRequestParams == null) {
            mRequestParams = new HashMap<>();
        } else {
            mRequestParams.clear();
        }
        Collection<InputItem> items = mAutoInputItems.values();
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
//            if (inputItem instanceof OptionalInputItem) {
//                OptionalInputItem optionalInputItem = (OptionalInputItem) inputItem;
//                optionalInputItem.setRequestValue(value);
//            } else if (inputItem instanceof MultiOptionalInputItem) {
//                MultiOptionalInputItem multiOptionalInputItem = (MultiOptionalInputItem) inputItem;
//                multiOptionalInputItem.setRequestValue(value);
//            } else if (inputItem instanceof CheckInputItem) {
//                CheckInputItem checkInputItem = (CheckInputItem) inputItem;
//                checkInputItem.setRequestValue(value);
//            } else {
            inputItem.setRequestValue(value);
//            }
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

//    private boolean isLengthLegal(String str, ViewAttribute attr) {
//        boolean result = true;
//        if (str.length() > attr.maxLength) {
//            showTip(String.format(mContext.getString(R.string.text_length_warning_more_msg), attr.maxLength));
//            result = false;
//        } else if (str.length() < attr.minLength) {
//            showTip(String.format(mContext.getString(R.string.text_length_warning_less_msg), attr.minLength));
//            result = false;
//        }
//        if (!result) {
//            focusView(attr.viewId);
//            View view = rootView.findViewById(attr.viewId);
//            ViewShakeUtil.newInstance(mContext).shake(view);
//        }
//        return result;
//    }

//    private boolean execValidateMethod(View view, String name, String validatedString) {
//        try {
//            Method m = Validate.class.getDeclaredMethod(name, String.class);
//            Result result = (Result) m.invoke(Validate.class.newInstance(), validatedString);
//            if (!result.isLegal()) {
//                Tip.show(result.getError());
//                ViewShakeUtil.newInstance(mContext).shake(view);
//            }
//            return result.isLegal();
//        } catch (Exception e) {
//            TQLogUtils.e("Can not found the method to validate the data, the method name is " + name);
//            TQLogUtils.e(e);
//        }
//        return true;
//    }

//    private void focusView(int viewId) {
//        View view = rootView.findViewById(viewId);
//        if (view != null) {
//            if (view instanceof EditText) {
//                view.requestFocus();
//            } else if (view instanceof EditItemBox) {
//                ((EditItemBox) view).getEditText().requestFocus();
//            }
//        }
//    }

//    public void addRequestParameter(String key, String value) {
//        mExtraRequestParams.put(key, value);
//    }
//
//    public void addRequestParameter(int viewId, String value) {
//        ViewAttribute attr = mAttrs.get(viewId);
//        if (attr != null && Util.isLegal(attr.requestKey)) {
//            mExtraRequestParams.put(attr.requestKey, value);
//        }
//    }

//    /**
//     * Clear the pending removing parameter queue.
//     */
//    public void resetPendingRemoveQueue() {
//        mPendingRemoveParams.clear();
//    }
//
//    public void removeFromPendingRemoveQueue(int viewId) {
//        if (mPendingRemoveParams != null) {
//            ViewAttribute attr = mAttrs.get(viewId);
//            if (attr != null && attr.requestKey != null) {
//                mPendingRemoveParams.remove(attr.requestKey);
//            }
//        }
//    }
//
//    public void removeFromPendingRemoveQueue(String requestKey) {
//        if (mPendingRemoveParams != null) {
//            mPendingRemoveParams.remove(requestKey);
//        }
//    }

    /**
     * The same as the {@link #removeRequestParameter(String)}.
     *
     * @param viewID The id to find the ViewAttribute to find the key of the
     *               request parameter.
     */
//    public void removeRequestParameter(int viewID) {
//        ViewAttribute config = mAttrs.get(viewID);
//
//        if (config != null && Util.isLegal(config.requestKey)) {
//            removeRequestParameter(config.requestKey);
//        }
//    }

    /**
     * Remove the request parameter from the map, if the request parameter has
     * not been put into the map, the key of the request parameter will be put
     * into a pending remove queue, which will be a refer to remove the request
     * while the final request parameter map has been generated.
     *
     * @param key The key of the request parameter.
     */
//    public void removeRequestParameter(String key) {
//        if (mExtraRequestParams.remove(key) == null) {
//            mPendingRemoveParams = mPendingRemoveParams == null ? new ArrayList<String>()
//                    : mPendingRemoveParams;
//            mPendingRemoveParams.add(key);
//        }
//    }

//    /**
//     * Get the key of the request parameter by the given view id in current
//     * configuration.
//     *
//     * @param viewID
//     * @return
//     */
//    public String getRequestKey(int viewID) {
//        ViewAttribute config = mAttrs.get(viewID);
//        return config == null ? null : config.requestKey;
//    }

//    /**
//     * Get the key of the request parameter by the given view id in the special
//     * configuration.
//     *
//     * @param configName
//     * @param viewID
//     * @return
//     */
//    public String getRequestKey(String configName, int viewID) {
//        LinkedHashMap<Integer, ViewAttribute> attrs = mModelLoader.read(mContext, configName);
//        if (attrs != null) {
//            ViewAttribute attr = attrs.get(viewID);
//            if (attr != null) {
//                return attr.requestKey;
//            }
//        }
//        return null;
//    }
    public void refreshViewById(int viewId) {
        InputItem item = itemsPutByViewId.get(viewId);
        item.refreshView();
    }

    public void refreshView() {
        Iterator<Map.Entry<Integer, InputItem>> i = itemsPutByViewId.entrySet().iterator();
        mAutoInputItems = new HashMap<>();
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

    /**
     * Same as {@link #ignoreRequired(String)}
     *
     * @param viewID The id of the view which is refer to the ViewAttribute
     */
//    public void ignoreRequired(int viewID) {
//        ViewAttribute attr = mAttrs.get(viewID);
//        if (attr != null && Util.isLegal(attr.requestKey)) {
//            ignoreRequired(attr.requestKey);
//        }
//    }
//
//    public boolean restoreRequired(int viewID) {
//        ViewAttribute attr = mAttrs.get(viewID);
//        return (attr != null && Util.isLegal(attr.requestKey) && restoreRequired(attr.requestKey));
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
//        Iterator<Entry<String, ViewAttribute>> eIterator = mValidateMap.entrySet().iterator();
//        while (eIterator.hasNext()) {
//            Entry<String, ViewAttribute> entry = eIterator.next();
//            View view = rootView.findViewById(entry.getValue().viewId);
//            if (view.getVisibility() != View.VISIBLE)
//                continue;
//            setViewEnabled(view, isEnable);
//        }
    }

    public void setViewEnable(int resourceId, boolean isEnable) {
        View view = rootView.findViewById(resourceId);
        setViewEnabled(view, isEnable);
    }

    public void setViewEnabled(View view, boolean enable) {
//        if (view instanceof ButtonItemBox) {
//            ((ButtonItemBox) view).setEnabled(enable);
//        } else if (view instanceof EditItemBox) {
//            ((EditItemBox) view).setEnabled(enable);
//        } else if (view instanceof SwitchItemBox) {
//            ((SwitchItemBox) view).setEnabled(enable);
//        } else if (view instanceof ItemBoxBase) {
//            ((ItemBoxBase) view).setEnabled(enable);
//        } else if (view instanceof LinearLayout) {
//            LinearLayout linearLayout = (LinearLayout) view;
//            if (linearLayout.getChildCount() > 0) {
//                for (int i = 0; i < linearLayout.getChildCount(); i++) {
//                    setViewEnabled(linearLayout.getChildAt(i), enable);
//                }
//            }
//        } else {
        view.setFocusable(enable);
        view.setEnabled(enable);
        view.setClickable(enable);
//        }

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

    public void showTip(int resId) {
        showTip(mContext.getResources().getString(resId));
    }

    public void showTip(String tip) {
        Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
    }

//    public BehaviorMultiOptional.MultiOptionalClickListener getOnMultiOptionalClickListener() {
//        return mMultiOptionalClickListener;
//    }
//
//    public void setOnMultiOptionalClickListener(BehaviorMultiOptional.MultiOptionalClickListener l) {
//        mMultiOptionalClickListener = l;
//    }
//
//    public BehaviorOptional.OnOptionClickListener getOnOptionClickListener() {
//        return mOnOptionClick;
//    }
//
//    public void setOnOptionClickListener(BehaviorOptional.OnOptionClickListener l) {
//        mOnOptionClick = l;
//    }
//
//    public BehaviorButton.OnButtonClickListener getOnButtonClickListener() {
//        return mOnButtonClickListener;
//    }
//
//    public void setOnButtonClickListener(BehaviorButton.OnButtonClickListener onButtonClickListener) {
//        this.mOnButtonClickListener = onButtonClickListener;
//    }
//
//    public void setOnViewFoundListener(OnViewFoundListener l) {
//        this.mOnViewFound = l;
//    }
//
//    public void setOnCheckBoxStateListener(BehaviorCheckBox.OnCheckStatusChanged l) {
//        mOnCheckedChangedListener = l;
//    }
//
//    public BehaviorCheckBox.OnCheckStatusChanged getOnCheckStatusChangedListener() {
//        return mOnCheckedChangedListener;
//    }
//
//    public interface OnViewFoundListener {
//        void onFound(View view, ViewAttribute config);
//    }

    private boolean isDebug = false;

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }


    public ViewObserver getViewObserver() {
        return viewObserver;
    }

    public void setViewObserver(ViewObserver viewObserver) {
        this.viewObserver = viewObserver;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack{
        void onStart(InputBinderEngine engine);
    }
}
