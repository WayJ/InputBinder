package com.wayj.inputbinder.example.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.wayj.inputbinder.example.R;
import com.wayj.inputbinder.example.api.CollectionModuleApi;
import com.wayj.inputbinder.example.entity.AutoListCard;
import com.wayj.inputbinder.example.entity.GridPage;
import com.wayj.inputbinder.example.widget.PageLoadCallback;
import com.wayj.inputbinder.example.widget.recyclerview.AutoLoadRecyclerView;
import com.wayj.inputbinder.example.widget.recyclerview.LRecyclerView;
import com.wayj.inputbinder.example.widget.recyclerview.OnItemClickListener;
import com.wayj.inputbinder.example.widget.recyclerview.RecyclerViewAdapter;
import com.wayj.inputbinder.example.widget.recyclerview.RecyclerViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;

import static com.wayj.inputbinder.example.base.IAutoListFunction.IntentKey_ModuleCard;
import static com.wayj.inputbinder.example.base.IAutoListFunction.KEY_ACTION_TYPE;
import static com.wayj.inputbinder.example.base.IAutoListFunction.KEY_MODULE_NAME;

/**
 * Created by way on 2017/9/15.
 */

public class AutoListActivity extends AppCompatActivity implements View.OnClickListener, OnItemClickListener, AutoLoadRecyclerView.SearchParamsResetListener, PageLoadCallback {
    //    public static final String IntentKey_ModuleCard = "IntentKey_ModuleCard";
//    protected Organization selectOrg;
    protected LinearLayout orgSelctLl;
    protected TextView selectOrgText;
    private LRecyclerView mRecyclerView;
    protected RecyclerViewAdapter<ViewHolder, JSONObject> mAdapter;
    protected CollectionModuleApi mAutoAssemblyApi;
    protected int typePosition;
    protected AutoListCard mAutoListCard;
    SharedPreferences sharedPreferences;
    protected LinearLayout selectll, typell;
    protected TextView type;
    public boolean isContradiction = false;
    //是否为邻里评议
    protected Action mAction = Action.Add;
    protected TextView mCountView;
    protected TextView mPageView;
    protected LinearLayout pageinfo_ll;
    private View.OnClickListener page_select_ClickListener;

    //    protected CollectionListRetrofitDialogExecute collectionListRetrofitDialogExecute;
    protected int mCurrentPage = 1;

    public LRecyclerView getRecyclerView() {
        return mRecyclerView;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_collection);
//        setBackTitle("");
//        getToolbarBack().setVisibility(View.VISIBLE);
        sharedPreferences = getSharedPreferences("app.collection", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(AutoListActivity.class.getName(), false).commit();

        mAutoListCard = initAutoListCard();
        if (mAutoListCard == null) {
            mAutoListCard = getAutoListCard();
        }
        if (mAutoListCard == null) {
            throw new RuntimeException("mAutoListCard 不能为空");
        }
        if (mAutoListCard.getModuleNameRes() != null) {
            setTitle(mAutoListCard.getModuleNameRes());
        }
//
//        if (MemberCache.getInstance().userNotEmpty())
//            selectOrg = MemberCache.getInstance().getMember().getCurrentOrganization();
//        else {
//            Tip.show("当前用户信息为空");
//        }

        mCountView = (TextView) findViewById(R.id.count);
        mCountView.setText(getCountDescription() + ":0/0");
        pageinfo_ll = (LinearLayout) findViewById(R.id.pageinfo_ll);
        mPageView = (TextView) findViewById(R.id.tv_gopage);
        pageinfo_ll.setVisibility(View.GONE);

        selectll = (LinearLayout) findViewById(R.id.selectll);
        orgSelctLl = (LinearLayout) findViewById(R.id.org_select_ll);
        selectOrgText = (TextView) findViewById(R.id.org_select);
        typell = (LinearLayout) findViewById(R.id.type_ll);
        type = (TextView) findViewById(R.id.type);
        orgSelctLl.setOnClickListener(this);
        typell.setOnClickListener(this);
        type.setOnClickListener(this);
        setOrgVisible();
        selectOrgText.setOnClickListener(this);
//        selectOrgText.setText(selectOrg != null ? selectOrg.getOrgName() : "中国");
        mRecyclerView = (LRecyclerView) findViewById(R.id.recycler_view);
        mAutoAssemblyApi = RetrofitUtil.getRetrofit().create(CollectionModuleApi.class);

        mAdapter = new AutoListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addPageLoadCallback(this);
        mRecyclerView.addSearchParamsResetListener(this);
        mAdapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                requestDetail(false,mAdapter.getItem(position));
                gotoDetailActivity(false, mAdapter.getItem(position));
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                return false;
            }
        });
    }

    /**
     * 可设置选择网格隐藏
     */
    protected void setOrgVisible() {

    }

    Handler handler = new Handler();
    protected boolean isRepeart = true;

    @Override
    protected void onResume() {
        super.onResume();
        //防止Activity未重新生成导致刷新失败

        if (isRepeart && mRecyclerView != null && handler != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean isRefresh = sharedPreferences.getBoolean(AutoListActivity.class.getName(), false);
                    if (isRefresh && mRecyclerView != null) {
                        refresh();
                        sharedPreferences.edit().putBoolean(AutoListActivity.class.getName(), false).commit();
                    }
                }
            }, 200);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_collection_id_search) {
            search();
        } else if (itemId == R.id.menu_collection_id_add) {
            boolean orgLimit = getOrgLimit();
            if (orgLimit) {
                add();
            } else {
                Tip.show(getSelectOrgTip());
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    protected int getSelectOrgTip() {
        return R.string.error_population_org;
    }

//    /**
//     * 可设置不选择网格进入新增
//     *
//     * @return
//     */
//    protected boolean getOrgLimit() {
//        return selectOrg != null && selectOrg.getOrgLevel() != null && ("片组片格".equals(selectOrg.getOrgLevel().getDisplayName()) || 207 == selectOrg.getOrgLevel().getId());
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collection_add_search_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
            /*新增*/
        menu.findItem(R.id.menu_collection_id_add).setVisible(hasAddPermission());
//            /*搜索*/
        menu.findItem(R.id.menu_collection_id_search).setVisible(!isContradiction);
        return true;
    }

    protected void gotoDetailActivity(boolean isEdit, JSONObject item) {
        mAction = isEdit ? Action.Edit : Action.View;
        Intent intent = new Intent(this, mAutoListCard.getFormActivityClass());
        intent.putExtra(KEY_MODULE_NAME, mAutoListCard.getModuleNameRes());
        intent.putExtra(AutoFormActivity.IntentKey_IS_Contradiction, isContradiction);
        String requestIdKey = getRequestIdKey();
        if (item != null) {
            intent.putExtra(AutoFormActivity.IntentKey_Object, item.toJSONString());
            if (item.containsKey(requestIdKey)) {
                int id = item.getInteger(requestIdKey);
                if (id > 0) {
                    intent.putExtra(AutoFormActivity.IntentKey_ObjectId, id);
                }
            }
        }
        putData(intent);
        intent.putExtra(AutoFormActivity.IntentKey_SelectOrg, selectOrg);
        intent.putExtra(KEY_ACTION_TYPE, isEdit ? Action.Edit : Action.View);
        startActivityForResult(intent, REQUEST_CODE);
    }

    protected void putData(Intent intent) {

    }

    protected String getRequestIdKey() {
        return "id";
    }


    protected void add() {
        mAction = Action.Add;
        Intent intent = new Intent(this, mAutoListCard.getFormActivityClass());
        intent.putExtra(KEY_MODULE_NAME, mAutoListCard.getModuleNameRes());
        intent.putExtra(AutoFormActivity.IntentKey_SelectOrg, selectOrg);
        intent.putExtra(KEY_ACTION_TYPE, Action.Add);
        startActivityForResult(intent, REQUEST_CODE);
    }

    protected void search() {

    }

//    protected void initModules(String moduleName) {
//        mModuleArrays = new ArrayList<>();
//        titles = new ArrayList<>();
//    }
//
//    protected List<String> getTitles() {
//        return titles;
//    }

//    protected String getModuleItem(int position) {
//        if(isModuleArraysNotEmty()){
//            return mModuleArrays.get(position);
//        }
//        return null;
//    }

//    protected Integer getModuleArrayRes(){
//        return null;
//    }

//    protected boolean isModuleArraysNotEmty(){
//        return mModuleArrays!=null&&mModuleArrays.size()>0;
//    }
//
//    public List<String> getModuleArrays() {
//        return mModuleArrays;
//    }

    public void refresh() {
        mRecyclerView.refreshAndClear();
    }

    @Override
    public void onClick(View v) {

    }


    protected void collectListRequestKey(Map<String, String> searchMap) {

    }

    public String getPre() {
        return "";
    }

    protected String prefix = null;

    protected void getListPage(int page, Observer<GridPage<JSONObject>> subscriber) {
        Map<String, String> searchMap = new HashMap<>();
        mCurrentPage = page;
        if (mAutoListCard.getModuleNameRes() == R.string.information_module_floating_title) {
            searchMap.put("searchVo.logOut", "0");
            searchMap.put("searchVo.isDeath", "0");
        }
        searchMap.put("page", page + "");
        searchMap.put("rows", "20");
        searchMap.put(getPre() + "orgId", selectOrg.getId().toString());
        putSearchParams(searchMap);

//        searchMap.put("userId", MemberCache.getInstance().getMember().getUser().getId().toString());
        String sidexParam = prefix != null ? prefix + "sidx" : "sidx";
        String sordParam = prefix != null ? prefix + "sord" : "sord";
        searchMap.put(sidexParam, "id");
        searchMap.put(sordParam, "desc");
        collectListRequestKey(searchMap);
        if (mAutoListCard != null && mAutoListCard.getListDataUrlResId() > 0) {
            String requestUrl = requestListUrl != null ? requestListUrl : getResources().getString(mAutoListCard.getListDataUrlResId());
            if (getChangeUrl() > 0) {
                requestUrl = getResources().getString(getChangeUrl());
            }
            RetrofitUtil.executeWithDialog(this, mAutoAssemblyApi.getListData(requestUrl, searchMap), subscriber, false);

        }
    }

    private String requestListUrl = null;

    protected void setListUrl(int urlRes) {
        if (urlRes != 0) {
            requestListUrl = getString(urlRes);
        } else {
            requestListUrl = null;
        }
    }

    protected int getChangeUrl() {
        return 0;
    }


    protected void putSearchParams(Map<String, String> searchMap) {
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        return false;
    }

    @Override
    public void resetSearchParam(boolean isResetSearchParams) {

    }


    class AutoListAdapter extends RecyclerViewAdapter<ViewHolder, JSONObject>
            implements View.OnClickListener {

        @Override
        public void getNextPage(int page, Observer<GridPage<JSONObject>> subscriber) {
            getListPage(page, subscriber);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = View.inflate(parent.getContext(), R.layout.layout_event_list_item, null);
//            ViewHolder holder = new ViewHolder(view);
//            setDefaultItemClickListener(holder);
//            holder.setOnClickListener(this);
//            return holder;
            ViewHolder holder = getViewHolder(parent);
            setDefaultItemClickListener(holder);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            bindData(holder, position);
        }

        @Override
        public void onClick(View v) {
            AutoListActivity.this.onClick(v);
        }
    }


    protected void bindData(ViewHolder holder, int position) {

    }

    protected int getModuleNameRes() {
        return mAutoListCard.getModuleNameRes();
    }

    /*是否有搜索权限*/
    protected boolean hasSearchPermission() {
        return false;
    }

    /*是否有新增权限*/
    protected boolean hasAddPermission() {
        return true;
    }

    /*判断是否有修改权限*/
    protected boolean hasEditPermission() {
        return false;
    }

    /*判断是否有查看权限*/
    protected boolean hasViewPermission() {
        return false;
    }


    protected ViewHolder getViewHolder(ViewGroup parent) {
        return null;
    }

    public class ViewHolder extends RecyclerViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setOnClickListener(View... views) {
            for (View view : views) {
                view.setOnClickListener(AutoListActivity.this);
            }
        }
    }

    protected AutoListCard initAutoListCard() {
        return (AutoListCard) getIntent().getSerializableExtra(IntentKey_ModuleCard);
    }

    public AutoListCard getAutoListCard() {
        return mAutoListCard;
    }

    public CollectionModuleApi getAutoAssemblyApi() {
        return mAutoAssemblyApi;
    }

    public void setAutoListCard(AutoListCard autoListCard) {
        this.mAutoListCard = autoListCard;
    }

    protected String getCountDescription() {
        return "";
    }

    @Override
    public void onPreLoad() {

    }

    @Override
    public void onPageLoaded(GridPage page, List dataSources) {
        int fetchedCount = 0;
        if (dataSources != null) {
            fetchedCount = dataSources.size();
        }
        int totalCount = (int) (page != null ? page.getRecords() : 0);
        int totalC = (int) (page != null ? page.getTotal() : 0);
        //		int totalCshow = totalC;
        if (getCountDescription() == null) {
            mCountView.setVisibility(View.INVISIBLE);
            mPageView.setVisibility(View.INVISIBLE);
        } else {
            mCountView.setVisibility(View.VISIBLE);
            mPageView.setVisibility(View.VISIBLE);
            int showPage = mAction != Action.Add ? mCurrentPage : getRecyclerView().getCurrentPage();
            mCountView.setText(getCountDescription() + ":" + fetchedCount + "/" + totalCount);
            mPageView.setText((showPage > totalC ? totalC : showPage) + "/" + totalC + "页");
        }
        if (totalC > 1) {
            if (page_select_ClickListener == null)
                page_select_ClickListener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int totalNum = (Integer) mPageView.getTag();
                        String[] nums = new String[totalNum];
                        for (int i = 0; i < totalNum; i++) {
                            nums[i] = i + 1 + "";
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(AutoListActivity.this);
                        builder.setTitle("请选择页数");
                        builder.setItems(nums, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                   int position) {
                                try {
                                    int totalNum = (Integer) mPageView.getTag();
                                    mCurrentPage = position + 1;
                                    Integer selectNum = mCurrentPage;
                                    if (selectNum > totalNum || selectNum <= 0) {
                                        Tip.show("请输入正确页数", false);
                                        return;
                                    } else {
                                        getRecyclerView().refreshAndClear(selectNum - 1, true);
                                        return;
                                    }
                                } catch (NumberFormatException e) {
                                    Tip.show("请输入正确页数", false);
                                }
                                return;
                            }
                        });
                        builder.create().show();
                    }
                };
            mPageView.setOnClickListener(page_select_ClickListener);
            mPageView.setTag(totalC);
        } else
            mPageView.setOnClickListener(null);
    }

}
