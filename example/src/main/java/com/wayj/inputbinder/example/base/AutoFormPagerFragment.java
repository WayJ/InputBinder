package com.wayj.inputbinder.example.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.wayj.inputbinder.InputBinder;
import com.wayj.inputbinder.item.InputItem;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by way on 2017/9/21.
 */

public abstract class AutoFormPagerFragment extends Fragment {
    protected Action mAction = Action.Add;
    protected String prefix = null;
//    protected DataDictionaryCache dataDictionaryCache;
    protected InputBinder mInputBinder;
    protected AutoFormActivity.AutoFormCallBack mAutoFormCallBack;
    private boolean firstCreated;
    public String attachFileNames="";
    //    protected AutoFormRequestBody mAutoFormRequestBody;
//    protected Bundle mData;
//    public PhotoItem headImg;

//    public List<PopulationAttachfiles> getAttaches() {
//
//        return null;
//    }
//
//    public List<IssueAttachFile> getIssueAttaches(){
//        return null;
//    }
//    public NeighborAttachfile getMultipleFile(){
//        return null;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstCreated = true;
//        if (dataDictionaryCache == null) {
//            dataDictionaryCache = DataDictionaryCache.getInstance();
//            dataDictionaryCache.setContext((AppCompatActivity) getActivity());
//        }
        mAction = (Action) getActivity().getIntent().getSerializableExtra(IAutoListFunction.KEY_ACTION_TYPE);
//        mData=getArguments();
//        dataDictionaryCache.loadDataDictionary(PropertyTypes.gender, new DataDictionaryCache.OnLoaderDictionarListener() {
//            @Override
//            public void onLoaderDictionar(DataDictionary dataDictionary) {
//                genders =dataDictionary.getData();
//            }
//        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getFragmentLayoutResId(), null, false);
    }

    public View $(int resId) {
        return getView().findViewById(resId);
    }

    @Override
    public void onStart() {
        super.onStart();
        startInputBinder(getViewConfigFileResId(), getConfigItemName(), prefix);
    }

    protected abstract String getConfigItemName();

    protected abstract int getViewConfigFileResId();

    protected abstract int getFragmentLayoutResId();
    protected String getAttachFileNames(){
        return null;
    }

    /**
     * 设置表单和数据进行绑定
     *
     * @param configRawFileResId ViewConfig文件资源id
     * @param configItemName     该表单对应配置项xml中配置名字
     * @param prefix             最后组装的请求参数的前缀，可不填
     */
    public void startInputBinder(int configRawFileResId, String configItemName, String prefix) {
//        mInputBinder = InputBinder.setup(getActivity(), configRawFileResId, prefix);
//        mFactory = mInputBinder.getViewController();
        try {
            List<InputItem> inputItems = new ArrayList<>();
            collectExpandInputItem(inputItems);
            if (mAutoFormCallBack != null) {
                onRestoreFormState(mAutoFormCallBack.getSavedRequestMap());
            }
//            mInputBinder.addInputItems(inputItems)
//                    .read(getActivity(), configItemName);
        } catch (Exception e) {
            e.printStackTrace();
//            Tip.show(e.getMessage());
        }
//        if (mFactory != null) {
//            mFactory.setAllViewEnable(mAction != Action.View);
//        }

//        handleView(mFactory);
    }

    /*
    * 设置业务中不可修改数据
    * */
//    protected void handleView(ViewBehavioralController mFactory) {
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (firstCreated) {
            View view=getView();
            view.scrollTo(0, 0);
            if(view instanceof ScrollView){
                ScrollView scrollView= (ScrollView) view;
                scrollView.smoothScrollTo(0,0);
            }
            firstCreated = false;
        }
    }

    /**
     * 对部分特殊的表单字段项进行特殊处理，例如点击
     *
     * @param inputItemList
     */
    protected abstract void collectExpandInputItem(List<InputItem> inputItemList);

//    public void setDataDictionaryCache(DataDictionaryCache dataDictionaryCache) {
//        this.dataDictionaryCache = dataDictionaryCache;
//    }

    public void setAutoFormCallBack(AutoFormActivity.AutoFormCallBack autoFormCallBack) {
        this.mAutoFormCallBack = autoFormCallBack;
    }

//    public void setAutoFormRequestBody(AutoFormRequestBody autoFormRequestBody) {
//        mAutoFormRequestBody = autoFormRequestBody;
//    }

    /**
     * 恢复页面，从已有数据onRestoreFormState
     */
    public void onRestoreFormState(Map<String, String> restoreRequestMap) {
        mInputBinder.addSavedRequestMap(restoreRequestMap);
    }

    /**
     * 保存数据，从页面上已输入的项中获取数据保存起来，用于提交表达或者下次恢复页面
     */
    public void saveState() {
        if (mInputBinder != null) {
            try {
                onSaveFormState(mInputBinder.getRequestMap());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<File> getFileList() {
        return null;
    }

    protected void onSaveFormState(Map<String, String> saveMap) throws JSONException {
        if (mAutoFormCallBack != null && saveMap != null) {
            mAutoFormCallBack.onSaveRequestMap(saveMap);
        }
    }

    public boolean verificateParams() {
        return true;
//        return mFactory.validateRequestParams(mInputBinder.getRequestMap());
    }

    @Override
    public void onStop() {
        super.onStop();
        saveState();
    }

}
