package com.wayj.inputbinder.example.widget.recyclerview;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.wayj.inputbinder.example.entity.GridPage;
import com.wayj.inputbinder.example.widget.PageLoadCallback;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class AutoLoadRecyclerView<T> extends RecyclerView {

    private String errorMsg = "";

    /*列表加载数据新线程*/
    public static HandlerThread mLoadHandlerThread = new HandlerThread("loadNextPage");

    public interface OnReceveDataListenner {
        void onReceveDataListenner(GridPage gridPage);
    }

    public OnReceveDataListenner listener;

    public void setOnReceveDataListenner(OnReceveDataListenner onReceveDataListenner) {
        this.listener = onReceveDataListenner;
    }

    public enum LoadState {
        start,
        releaseToLoad,
        inLoading,
        finish,
        end,
        noMore,
        error;
    }

    private RecyclerViewAdapter<? extends RecyclerViewHolder, T> mAdapter;
    private int mCurrentPage;
    private LinearLayoutManager linearLayoutManager;
    private boolean isNoMoreData;

    private Handler mLoadHandler;
    public boolean isExecuting = false;

    /*第一次是否主动加载界面*/
    private boolean firstLoadingData = true;


    private int lastVisibleItem = 0;

    private boolean isHaveBaseData = false;

    /*下拉刷新界面*/
    private float downY = 0;
    private float lastMoveY = 0;
    private boolean needRefresh = false;
    private PageLoadCallback mCallback;

    public int getmCurrentPage() {
        return mCurrentPage;
    }

    public interface SearchParamsResetListener {
        void resetSearchParam(boolean isResetSearchParams);
    }

    public SearchParamsResetListener searchParamsResetListener;

    public void addSearchParamsResetListener(SearchParamsResetListener searchParamsResetListener) {
        this.searchParamsResetListener = searchParamsResetListener;
    }

    //添加页数变化监听
    public void addPageLoadCallback(PageLoadCallback mCallback) {
        this.mCallback = mCallback;
    }

    public void setmCurrentPage(int mCurrentPage) {
        this.mCurrentPage = mCurrentPage;
    }

    private LinearLayout.LayoutParams mLayoutParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    public AutoLoadRecyclerView(Context context) {
        this(context, null);
    }

    public AutoLoadRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLoadRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {
        linearLayoutManager = new LinearLayoutManager(context);
        setLayoutManager(linearLayoutManager);
        setHasFixedSize(true);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.getAdapter() != null
                        && lastVisibleItem + 1 == recyclerView.getAdapter().getItemCount()
                        && !isExecuting) {
                    if (!isNoMoreData && lastVisibleItem >= 0) {
                        onLoadMore(LoadState.inLoading, 0);
                        smoothScrollToPosition(lastVisibleItem);
                        if (!loadNextPage()) {
                            onLoadMore(LoadState.end, 0);
                        }
                    } else {
                        onLoadMore(LoadState.noMore, 0);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            }
        });
    }


    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof RecyclerViewAdapter))
            return;
        this.mAdapter = (RecyclerViewAdapter) adapter;
        if (firstLoadingData)
            refreshAndClear();
        super.setAdapter(mAdapter);

    }


    public void refreshAndClear() {
        //刷新
        if (searchParamsResetListener != null) {
            searchParamsResetListener.resetSearchParam(true);
        }
        refreshAndClear(0, false);

    }

    //是否跳转到指定页
    private boolean isLoadPage = false;

    public void refreshAndClear(int page, boolean isLoadPage) {
        mCurrentPage = page;
        isNoMoreData = false;
        this.isLoadPage = isLoadPage;
        onRefresh(LoadState.inLoading, 0);
        mDataSource.clear();
        if (!loadNextPage()) {
            onRefresh(LoadState.end, 0);
            smoothScrollToPosition(0);
        }
    }

    private List<T> mDataSource = new ArrayList<>();

    public boolean loadNextPage() {
        if (!isNoMoreData && !isExecuting) {
            try {
                if (mLoadHandler == null) {
                    if (mLoadHandlerThread == null) {
                        mLoadHandlerThread = new HandlerThread("loadNextPage");
                    }
                    if (!mLoadHandlerThread.isAlive())
                        mLoadHandlerThread.start();
                    if (mLoadHandlerThread.isAlive())
                        mLoadHandler = new Handler(mLoadHandlerThread.getLooper());
                }
                /*后台发生请求*/
                mCurrentPage++;
                if (mAdapter == null) {
                    UIHandler.sendEmptyMessage(1);
                    return true;
                }
                UIHandler.sendEmptyMessage(0);
//                GridPage<T> result = mAdapter.getNextPage(mCurrentPage);
//                if (result!=null){
//                    isHaveBaseData = true;
//                    Message message = new Message();
//                    message.obj = result;
//                    message.what = 1;
//                    UIHandler.sendMessage(message);
//                    mDataSource.addAll(result.getRows());
//                    if(mCallback!=null){
//                        mCallback.onPageLoaded(result,mDataSource);
//                    }
//                }else{
                isHaveBaseData = false;
                mAdapter.getNextPage(mCurrentPage, new Observer<GridPage<T>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("onSubscribe", "onSubscribe: ");
                    }

                    @Override
                    public void onNext(GridPage value) {
                        if (value == null)
                            value = new GridPage<>();
                        if (listener != null) {
                            listener.onReceveDataListenner(value);
                        }

                        Message message = new Message();
                        message.obj = value;
                        message.what = 1;
                        UIHandler.sendMessage(message);
                        if (value == null) {
                            return;
                        }
                        mDataSource.addAll(value.getRows());
                        if (mCallback != null) {
                            mCallback.onPageLoaded(value, mDataSource);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Message message = new Message();
                        message.obj = e.getMessage() == null ? "未知错误" : e.getMessage();
                        message.what = 2;
                        UIHandler.sendMessage(message);
                    }

                    @Override
                    public void onComplete() {
                        Log.e("onComplete", "onComplete: ");
                    }
                });
//                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Message message = new Message();
                message.obj = e.getMessage() == null ? "未知错误" : e.getMessage();
                message.what = 2;
                UIHandler.sendMessage(message);
            }
        }
        return false;

    }

    /*更新UI*/
    private Handler UIHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0:
                    /*发送请求前*/
                    isExecuting = true;
                    if (mCurrentPage == 1 && mAdapter != null)
                        mAdapter.clear();
                    setEmptyView(mCurrentPage == 1 ? true : false);
                    break;
                case 1:
                    /*获取数据后前*/
                    isExecuting = false;
                    GridPage<T> result = (GridPage<T>) msg.obj;
                    if (result != null) {
                        setEmptyView(false);
                        if (result.getPage() >= result.getTotal() || result.getRows().size() <= 0)
                            isNoMoreData = true;
                        if (mCurrentPage == 1 && (result.getRows() == null || result.getRows().size() == 0))
                            setEmptyView(true);

                        if (result.getRows() != null && mAdapter != null) {
                            if (mCurrentPage == 1 || isLoadPage) {
                                onRefresh(LoadState.finish, 0);
                                mAdapter.setDataList(result.getRows(), isLoadPage);
                                isLoadPage = false;
                            } else {
                                onLoadMore(LoadState.finish, 0);
                                mAdapter.addAll(result.getRows());
                            }
                            //后台返回数据page一直=1时，导致一直会加载下一页，使用records总数字段来判断是否有下一页
                            if (!isNoMoreData && result.getRecords() > 0 && mAdapter.getItemCount() >= result.getRecords()) {
                                isNoMoreData = true;
                            }
                        }
                    } else {
                        if (mCurrentPage == 1) {
                            onRefresh(LoadState.finish, 0);
                        } else {
                            onLoadMore(LoadState.finish, 0);
                        }
                    }
                    break;
                case 2:
                    isExecuting = false;
                    String error = (String) msg.obj;
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(error);
                    if (TextUtils.isEmpty(error) || !error.contains("登录超时")) {
                        buffer.append(",");
                        buffer.append("点我重试");
                    }
                    errorMsg = buffer.toString();
                    if (mCurrentPage == 1) {
                        onRefresh(LoadState.error, 0);
                    } else {
                        onLoadMore(LoadState.error, 0);
                    }
                    mCurrentPage--;
                    break;
            }
        }
    };

    /*获取数据*/
//    Runnable getPageRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            if (mAdapter == null) {
//                UIHandler.sendEmptyMessage(1);
//                return;
//            }
//            mCurrentPage++;
//            UIHandler.sendEmptyMessage(0);
//            GridPage<T> result = mAdapter.getNextPage(mCurrentPage);
//            if (result == null){
//                result = new GridPage<>();
//                isHaveBaseData = false;
//            }else{
//                isHaveBaseData = true;
//            }
//            Message message = new Message();
//            message.obj = result;
//            message.what = 1;
//            UIHandler.sendMessage(message);
//        }
//    };

    // 取消加载任务
    public boolean cancelTask() {
        isNoMoreData = false;
//        mLoadHandler.removeCallbacks(getPageRunnable);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.e("test", "frist item: " + linearLayoutManager.findFirstCompletelyVisibleItemPosition());
        if (!isExecuting && linearLayoutManager.findFirstCompletelyVisibleItemPosition() <= 0) {
            /*下拉刷新*/
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downY = ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (downY == 0) {
                        downY = ev.getRawY();
                        break;
                    }
                    if (lastMoveY == 0) {
                        lastMoveY = ev.getRawY();
                    } else if (lastMoveY == ev.getRawY()) {
                        break;
                    } else {
                        lastMoveY = ev.getRawY();
                    }
                    float moveY = ev.getRawY();
                    float disY = moveY - downY;
                    Log.d("test", "disY: " + disY);
                    if (!isExecuting && disY > 0) {
                        if (onRefresh(LoadState.releaseToLoad, disY)) {
                            needRefresh = true;
                        } else {
                            needRefresh = false;
                        }
                        return true;//防止改变paddingTop的同时,listview也上下滑动
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    downY = 0;//一次刷新完成之后,清空downy
                    if (needRefresh) {
                        needRefresh = false;
                        refreshAndClear();
                        return true;
                    } else {
                        onRefresh(LoadState.end, 0);
                        smoothScrollToPosition(0);
                    }
                    break;
            }
        } else {
            if (ev.getAction() == MotionEvent.ACTION_UP)
                onRefresh(LoadState.end, 0);
        }
        return super.onTouchEvent(ev);//处理listview的上下滑动
    }

    /*是否正在刷新*/
    public boolean IsExecuting() {
        return isExecuting;
    }

    /*是否设置适配器后自动加载界面*/
    public void setLoadingFirst(boolean isLoading) {
        this.firstLoadingData = isLoading;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    abstract boolean onLoadMore(LoadState state, float moveY);

    abstract boolean onRefresh(LoadState state, float moveY);

    abstract boolean setEmptyView(boolean show);
}
