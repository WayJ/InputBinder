package com.wayj.inputbinder.example.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wayj.inputbinder.example.R;
import com.wayj.inputbinder.example.api.CollectionModuleApi;
import com.wayj.inputbinder.example.widget.WaitDialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.wayj.inputbinder.example.base.IAutoListFunction.KEY_ACTION_TYPE;
import static com.wayj.inputbinder.example.base.IAutoListFunction.KEY_MODULE_NAME;

/**
 * Created by way on 2017/9/15.
 */

public abstract class AutoFormActivity extends AppCompatActivity {
    public static final String ORDER_NO = "orderNo";
    public static final String IntentKey_Object = "IntentKey_Object";
    public static final String IntentKey_ObjectId = "IntentKey_ObjectId";
    public static final String IntentKey_SelectOrg = "IntentKey_SelectOrg";
    public static final String IntentKey_IS_Contradiction = "is_Contradiction";
    public Action mAction = Action.Add;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabAdapter viewPagerAdapter;
    private String[] mTabTitle;
//    public Organization  selectOrg = MemberCache.getInstance().getMember().getCurrentOrganization();
    protected List<AutoFormPagerFragment> fragmentList = new ArrayList<>();
//    protected DataDictionaryCache dataDictionaryCache;
    protected AutoFormRequestBody mAutoFormRequestBody = new AutoFormRequestBody();
    private CollectionModuleApi mAutoAssemblyApi;
    protected WaitDialogFragment waitDialogFragment;
    private SharedPreferences sharedPreferences;
    private boolean isContradiction = false,isEvent=false;
    private String actionHandleType = null;
    private String dealCode = null;

    public String getDealCode() {
        return dealCode;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoform);
//        getToolbarBack().setVisibility(View.GONE);
        if (getIntent().hasExtra(KEY_ACTION_TYPE)) {
            mAction = (Action) getIntent().getSerializableExtra(KEY_ACTION_TYPE);
        }
        isContradiction = getIntent().getBooleanExtra(IntentKey_IS_Contradiction, false);

        sharedPreferences = getSharedPreferences("app.collection", MODE_PRIVATE);
//        if(getIntent().hasExtra(IntentKey_SelectOrg)) {
//            selectOrg = (Organization) getIntent().getSerializableExtra(IntentKey_SelectOrg);
//        }
//        dataDictionaryCache = DataDictionaryCache.getInstance();
//        dataDictionaryCache.setContext(getActivity());
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        tabLayout.setVisibility(View.GONE);
        if (getIntent().hasExtra(KEY_MODULE_NAME)) {
            int moduleResId = getIntent().getIntExtra(KEY_MODULE_NAME, -1);
            if (moduleResId > -1) {
                setTitle(getString(moduleResId));
            }
        }

        waitDialogFragment = new WaitDialogFragment();
        waitDialogFragment.show(this);
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                try {
                    if (mAction == Action.Add) {
                        Map<String, String> responseMap = setupIfEditAction();
                        if (responseMap == null) {
                            responseMap = new ArrayMap<String, String>();
                        }
                        onQuerySuccess(responseMap);
                        mAutoFormRequestBody.setRequestMap(responseMap);
                        e.onNext(true);
                        return;
                    } else {
                        Map<String, String> responseMap = setupIfEditActionWithObjectId();
                        if (responseMap == null || responseMap.size() == 0) {
                            responseMap = setupIfEditAction();
                        }
                        if (responseMap != null && responseMap.size() > 0) {
                            onQuerySuccess(responseMap);
                            mAutoFormRequestBody.setRequestMap(responseMap);
                            e.onNext(true);
                            return;
                        }
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();

                }
                e.onNext(false);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (waitDialogFragment != null && waitDialogFragment.isShow()) {
                            waitDialogFragment.dismiss();
                            waitDialogFragment = null;
                        }
                        if (aBoolean)
                            loadViewHandler.sendEmptyMessage(0);
                        else {
//                            Tip.show("数据加载失败");
                        }
                    }
                });
    }

    Handler loadViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initTabLayout(getTabTitles(), getAutoFormPagerFragments());
        }
    };

    protected void putToHashMap(Map<String, String> targetMap, JSONObject oldMap, String keyPre) {
        for (String key : oldMap.keySet()) {
            Object mapValue = oldMap.get(key);
            String thisKey = TextUtils.isEmpty(keyPre) ? key : keyPre + "." + key;
//            if (TQBuildConfig.DEVELOP)
//                TQLogUtils.e(thisKey + " = " + mapValue);
            if (mapValue instanceof JSONObject) {
                putToHashMap(targetMap, (JSONObject) mapValue, thisKey);
            } else if (mapValue instanceof Double) {
                targetMap.put(thisKey, ((Double) mapValue).intValue() + "");
            } else {
                String actualValue = mapValue.toString();
                if ("staffType".equals(key) || "visitTypes".equals(key)) {
                    try {
                        String ids = "";
                        JSONArray jsonArray = JSON.parseArray(mapValue.toString());
                        Iterator iterator = jsonArray.iterator();
                        while (iterator.hasNext()) {
                            JSONObject jsonObject = (JSONObject) iterator.next();
                            ids += jsonObject.getString("id") + ",";
                        }
                        actualValue = ids;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                targetMap.put(thisKey, actualValue);
            }
        }
    }


    private Map<String, String> setupIfEditActionWithObjectId() {
        int objectId = getIntent().getIntExtra(IntentKey_ObjectId, 0);
        if (objectId > 0 && getQueryRequestUrlResId() > 0) {
            try {
                return queryDetail(objectId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Map<String, String> setupIfEditAction() {
        String objectJsonStr = getIntent().getStringExtra(IntentKey_Object);
        if (!TextUtils.isEmpty(objectJsonStr)) {
            TreeMap<String, String> requestMap = new TreeMap<>();
            JSONObject jsonObject = JSONObject.parseObject(objectJsonStr);

            putToHashMap(requestMap, jsonObject, mAutoFormRequestBody.getPreFix());
            return requestMap;
        }
        return null;
    }

    public void initTabLayout(String[] titles, AutoFormPagerFragment[] fragments) {
        mTabTitle = titles;
        fragmentList.clear();
        for (int i = 0; i < fragments.length; i++) {
            fragmentList.add(fragments[i]);
            fragments[i].setDataDictionaryCache(dataDictionaryCache);
            fragments[i].setAutoFormCallBack(mAutoFormCallBack);
//            fragments[i].setAutoFormRequestBody(mAutoFormRequestBody);
        }

        viewPagerAdapter = new TabAdapter(getSupportFragmentManager(), fragmentList);
        //给ViewPager设置适配器
        viewPager.setAdapter(viewPagerAdapter);
        //将TabLayout和ViewPager关联起来。
        tabLayout.setupWithViewPager(viewPager);
        //设置可以滑动
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    public TabAdapter getViewPagerAdapter() {
        return viewPagerAdapter;
    }

    public abstract String[] getTabTitles();

    public abstract AutoFormPagerFragment[] getAutoFormPagerFragments();

    public abstract int getCreateRequestUrlResId();

    public abstract int getUpdateRequestUrlResId();

    public abstract int getQueryRequestUrlResId();

//    /**
//     * 在发送新增/更新请求前会执行该方法
//     *
//     * @param autoFormRequestBody
//     */
//    public void onAheadOfRequest(@Nullable AutoFormRequestBody autoFormRequestBody) {
//
//    }

    public void onQuerySuccess(Map<String, String> responseMap) {

    }

    public void onAheadOfQueryRequest(Map<String, String> requestMap) {
        if (getIntent().hasExtra(ORDER_NO)) {
            String orderNo = getIntent().getStringExtra("orderNo");
            requestMap.put("orderNo", orderNo);
        }
    }

    public Map<String, String> queryDetail(int objectId) throws IOException {
        Map<String, String> map = new HashMap<>();
        onAheadOfQueryRequest(map);
        String result = getApi().doRetrieve(getResources().getString(getQueryRequestUrlResId()), objectId, map).execute().body();
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            JSONArray jsonArray = JSON.parseArray(result);
            String itemJson = jsonArray.getString(0);
            jsonObject = JSONObject.parseObject(itemJson);
            e.printStackTrace();
        }
        //矛盾排查从受理中心返回的数据
        JSONObject unAssignJson=null;
        if(jsonObject.containsKey("completeData")){
            unAssignJson=jsonObject.getJSONObject("completeData");
        }
        handleItemJson(jsonObject);
        String attachFileString = null;

        if (jsonObject.containsKey("action")) {
            actionHandleType = jsonObject.getString("action");
        }
        if (jsonObject.containsKey("dealCode")) {
            dealCode = jsonObject.getString("dealCode");
        }
        supportInvalidateOptionsMenu();

        if (!TextUtils.isEmpty(jsonObject.getString("issueAttachFiles"))) {
            attachFileString = jsonObject.getString("issueAttachFiles");
        }
        if (!TextUtils.isEmpty(jsonObject.getString("body"))) {
            jsonObject = JSON.parseObject(jsonObject.getString("body"));
        }

        if (!TextUtils.isEmpty(jsonObject.getString("issueNew"))) {
            jsonObject = JSONObject.parseObject(jsonObject.getString("issueNew"));
        }

        Map<String, String> responseMap = new HashMap<>();
        putToHashMap(responseMap, jsonObject, mAutoFormRequestBody.getPreFix());
        if(unAssignJson!=null){
            putToHashMap(responseMap, unAssignJson, mAutoFormRequestBody.getPreFix());
        }
        responseMap.put("issueAttachFiles", attachFileString);
        return responseMap;
        //        RetrofitUtil.executeWithDialog(getActivity(), getApi().doRetrieve("", objectId, map), subscriber);
    }

    protected void onAheadOfQueryRequest(Map<String, String> map, int objectId) {

    }

    protected void handleItemJson(JSONObject json){

    }


    public void submit() {
        boolean isSubmit = true;
        try {
            for (AutoFormPagerFragment fragments : fragmentList) {
                if (!fragments.verificateParams()) {
                    isSubmit = false;
                    break;
                }
                fragments.saveState();
            }

            if (isSubmit)
                doCreateRequest(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
//                        Tip.show("操作成功");
                        finishWithSuccessAction();
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Tip.show("操作失败" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doCreateRequest(Observer<String> subscriber) {
        String action = null;
        if (mAction == Action.Add && getCreateRequestUrlResId() > 0) {
            action = getResources().getString(getCreateRequestUrlResId());
        } else if (mAction == Action.Edit && getUpdateRequestUrlResId() > 0) {
            action = getResources().getString(getUpdateRequestUrlResId());
        }
        Map<String, String> params = mAutoFormRequestBody.getRequestMap();
        if (action != null)
            onAheadOfSubmitRequest(params);
        if (params.containsKey("population.visitTypes")) {
            params.remove("population.visitTypes");
        }

        if (params.containsKey("issueAttachFiles")) {
            params.remove("issueAttachFiles");
        }
//        RetrofitUtil.executeWithDialog(getActivity(), getApi().
//                doCreate(action, params), subscriber);
    }

    protected void onAheadOfSubmitRequest(Map<String, String> requestMap) {
    }


    public class TabAdapter extends FragmentPagerAdapter {

        private List<AutoFormPagerFragment> fragments;

        public TabAdapter(FragmentManager fm, List<AutoFormPagerFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }


        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        //设置tablayout标题
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitle[position];
        }

    }


    public interface AutoFormCallBack {
        Map<String, String> getSavedRequestMap();

        void onSaveRequestMap(Map<String, String> requestMap);
    }

    AutoFormActivity.AutoFormCallBack mAutoFormCallBack = new AutoFormCallBack() {

        @Override
        public Map<String, String> getSavedRequestMap() {
            return mAutoFormRequestBody.getRequestMap();
        }

        @Override
        public void onSaveRequestMap(Map<String, String> requestMap) {
            mAutoFormRequestBody.getRequestMap().putAll(requestMap);
        }
    };


    protected CollectionModuleApi getApi() {
        if (this.mAutoAssemblyApi == null) {
//            this.mAutoAssemblyApi = RetrofitUtil.getRetrofit().create(CollectionModuleApi.class);
        }
        return mAutoAssemblyApi;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collection_cannel_submit, menu);
        if (mAction == Action.View) {
            menu.findItem(R.id.menu_event_handle).setVisible(isContradiction);
            menu.findItem(R.id.menu_collection_id_submit).setVisible(false);
        } else {
            menu.findItem(R.id.menu_event_handle).setVisible(false);
            menu.findItem(R.id.menu_collection_id_submit).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!TextUtils.isEmpty(actionHandleType)) {
            menu.findItem(R.id.menu_event_handle).setTitle(actionHandleType);
        } else {
            menu.findItem(R.id.menu_event_handle).setVisible(false);
        }

        menu.findItem(R.id.menu_issue_process).setVisible(mAction!=Action.Add&&isEvent);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_collection_id_submit) {
            submit();
            return true;
        } else if (itemId == R.id.menu_event_handle) {
            if ("61".equals(dealCode)) {
                accept();
            } else if (!"71".equals(dealCode)) {
                handle();
            }
            return true;
        }else if(itemId == R.id.menu_issue_process){
            showIssueProcessdDialog();
            return true;
        } else if (itemId == R.id.menu_collection_id_cancel) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected void showIssueProcessdDialog() {

    }

    protected void handle() {

    }

    protected void accept() {

    }

    protected void finishWithSuccessAction() {
        setResult(RESULT_OK);
        sharedPreferences.edit().putBoolean(AutoListActivity.class.getName(), true).commit();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != fragmentList && null != viewPager && fragmentList.size() > viewPager.getCurrentItem()) {
            fragmentList.get(viewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
