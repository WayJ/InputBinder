package com.wayj.inputbinder.example.widget.recyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wayj.inputbinder.example.widget.PageLoadCallback;


/**
 * Created at 陈 on 2016/10/26.
 *
 * @author chenwanfeng
 * @email 237142681@qq.com
 */

public class LRecyclerView extends RelativeLayout {
    /*下拉刷新*/
    private TextView mRefreshTextView;
    private View mRefreshView;
    private SimpleRoundLoadingView mRefreshRound;
    private int refreshHeight = 0;
    /*上拉加载*/
    private TextView mFootTextView;
    private View mFootView;
    private SimpleRoundLoadingView mFootRound;
    private int footHeight = 0;
    /*空页面*/
    private View mEmptyView;
    private TextView mEmptyTextView;

    private OnListScrollListener mScrollListener;

    AutoLoadRecyclerView.OnReceveDataListenner onReceveDataListenner;
    private AutoLoadRecyclerView mAutoLoadRecyclerView;
    private RecyclerViewAdapter mAdapter;

    public void setReceveDataListener(AutoLoadRecyclerView.OnReceveDataListenner listener) {
        this.onReceveDataListenner = listener;
        mAutoLoadRecyclerView.setOnReceveDataListenner(onReceveDataListenner);
    }


    public LRecyclerView(Context context) {
        this(context, null);
    }

    public LRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 在搜索完之后刷新时，用来清空搜索参数
     */
    public void addSearchParamsResetListener(AutoLoadRecyclerView.SearchParamsResetListener searchParamsResetListener) {
        mAutoLoadRecyclerView.addSearchParamsResetListener(searchParamsResetListener);
    }

    /**
     * 添加显示页数的回调接口
     */
    public void addPageLoadCallback(PageLoadCallback callback) {
        mAutoLoadRecyclerView.addPageLoadCallback(callback);
    }

    public void refreshAndClear(int page, boolean isLoadPage) {
        mAutoLoadRecyclerView.refreshAndClear(page, isLoadPage);
    }

    public void refreshAndClear() {
        refreshAndClear(0, false);
    }

    public int getCurrentPage() {
        return mAutoLoadRecyclerView.getmCurrentPage();
    }

    private void init(Context context) {
        mEmptyView = LayoutInflater.from(context).inflate(R.layout.emptyview, null);
        mEmptyTextView = (TextView) mEmptyView.findViewById(R.id.empty_view_text);
        mEmptyTextView.setText("为人民服务！");
        mEmptyView.setClickable(false);
        mEmptyView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmptyView.setClickable(false);
                mEmptyTextView.setText("为人民服务！");
                mAutoLoadRecyclerView.refreshAndClear();
            }
        });

        mRefreshView = LayoutInflater.from(context).inflate(R.layout.refresh_headview, null);
        mRefreshTextView = (TextView) mRefreshView.findViewById(R.id.refresh_title);
        mRefreshRound = (SimpleRoundLoadingView) mRefreshView.findViewById(R.id.refresh_progress);
        mRefreshView.setId(R.id.l_recycler_refresh_view);
        mRefreshTextView.setText("正在刷新...");
        mRefreshView.setPadding(20, 20, 20, 20);
        mRefreshView.measure(0, 0);
        refreshHeight = mRefreshView.getMeasuredHeight();

        mFootView = LayoutInflater.from(context).inflate(R.layout.refresh_headview, null);
        mFootTextView = (TextView) mFootView.findViewById(R.id.refresh_title);
        mFootRound = (SimpleRoundLoadingView) mFootView.findViewById(R.id.refresh_progress);
        mFootView.setId(R.id.l_recycler_foot_view);
        mFootTextView.setText("正在加载...");
        mFootView.setClickable(false);
        mFootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFootTextView.setText("正在加载...");
                mFootView.setClickable(false);
                mAutoLoadRecyclerView.loadNextPage();
            }
        });
        mFootView.setPadding(20, 20, 20, 20);
        mFootView.measure(0, 0);
        footHeight = mRefreshView.getMeasuredHeight();
        mFootView.setPadding(0, 0, 0, -footHeight);

        mRefreshView.setPadding(0, -refreshHeight, 0, 0);
        mAutoLoadRecyclerView = new AutoLoadRecyclerView(context) {
            @Override
            public boolean onLoadMore(LoadState state, float moveY) {
                if (isExecuting)
                    return false;
                switch (state) {
                    case start:
//                        mRefreshView.setPadding(0, (int) moveY, 0, 0);
                        break;
                    case releaseToLoad:
//                        if (moveY <= refreshHeight * 0.1) {
//                            mRefreshView.setPadding(0, (int) moveY, 0, 0);
//                        } else {
//                            mRefreshView.setPadding(0, (int) (refreshHeight * 0.1), 0, 0);
//                        }
//                        if (moveY >= refreshHeight) {
//                            mRefreshTextView.setText("释放加载");
//                            return true;
//                        } else {
//                            mRefreshTextView.setText("上拉加载");
//                        }
                        break;
                    case inLoading:
                        if (mScrollListener != null)
                            mScrollListener.onLoadMore();
                        mFootTextView.setText("正在加载...");
                        mFootView.setPadding(0, 0, 0, 0);
                        mFootRound.startAnimator();
                        break;
                    case finish:
                        mFootTextView.setText("加载完成   ");
//                        mFootView.setPadding(0, 0, 0, -footHeight);
                        smoothScrollTo(mFootView, mFootView.getPaddingBottom(), -footHeight);
                        mFootRound.cancelAnimator();
                        break;
                    case end:
                        mFootTextView.setText("加载完成   ");
//                        mFootView.setPadding(0, 0, 0, -footHeight);
                        smoothScrollTo(mFootView, mFootView.getPaddingBottom(), -footHeight);
                        mFootRound.cancelAnimator();
                        break;
                    case noMore:
                        mFootTextView.setText("没有更多数据了   ");
                        mFootView.setPadding(0, 0, 0, 0);
                        smoothScrollTo(mFootView, mFootView.getPaddingBottom(), -footHeight);
                        break;
                    case error:
                        mFootTextView.setText(getErrorMsg());
                        mFootView.setClickable(true);
                        mFootRound.cancelAnimator();
                        break;
                }
                return false;
            }

            @Override
            public boolean onRefresh(LoadState state, float moveY) {
                if (isExecuting)
                    return false;
                switch (state) {
                    case start:
                        mRefreshView.setPadding(0, (int) moveY, 0, 0);
                        mRefreshRound.setAngle(Math.abs((int) ((refreshHeight - moveY) * 360 * moveY / refreshHeight)));
                        break;
                    case releaseToLoad:
                        moveY = moveY - refreshHeight;
                        if (moveY <= refreshHeight * 0.2) {
                            mRefreshView.setPadding(0, (int) moveY, 0, 0);
                            mRefreshRound.setAngle((int) Math.abs(moveY * 360 * moveY / refreshHeight));
                        } else {
                            mRefreshView.setPadding(0, (int) (refreshHeight * 0.2), 0, 0);
                        }
                        if (moveY >= refreshHeight) {
                            mRefreshTextView.setText("释放刷新...");
                            if (mScrollListener != null)
                                mScrollListener.onRefresh();
                            return true;
                        } else {
                            mRefreshTextView.setText("下拉刷新...");
                        }

                        break;
                    case inLoading:
                        mFootView.setPadding(0, 0, 0, -footHeight);
                        mRefreshTextView.setText("正在刷新...");
                        mEmptyTextView.setText("为人民服务！");
                        smoothScrollTo(mRefreshView, mRefreshView.getPaddingTop(), 0);
                        mRefreshRound.startAnimator();
                        break;
                    case finish:
                        mRefreshTextView.setText("刷新成功   ");
                        smoothScrollTo(mRefreshView, mRefreshView.getPaddingTop(), -refreshHeight);
                        mRefreshRound.cancelAnimator();
                        break;
                    case end:
                        mRefreshTextView.setText("下拉刷新...");
                        smoothScrollTo(mRefreshView, mRefreshView.getPaddingTop(), -refreshHeight);
                        mRefreshRound.cancelAnimator();
                        break;
                    case error:
                        mRefreshTextView.setText("下拉刷新...");
                        smoothScrollTo(mRefreshView, mRefreshView.getPaddingTop(), -refreshHeight);
                        mEmptyTextView.setText(getErrorMsg());
                        mEmptyView.setClickable(true);
                        mRefreshRound.cancelAnimator();
                        break;
                }
                return false;
            }

            @Override
            boolean setEmptyView(boolean show) {
                if (mEmptyView != null)
                    mEmptyView.setVisibility(show ? VISIBLE : GONE);
                return false;
            }
        };
        mAutoLoadRecyclerView.setId(R.id.l_recycler_view);

        LayoutParams lp1 =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        addView(mRefreshView, lp1);

        LayoutParams lp2 =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        addView(mFootView, lp2);

        LayoutParams lp3 =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp3.addRule(RelativeLayout.BELOW, R.id.l_recycler_refresh_view);
        lp3.addRule(RelativeLayout.ABOVE, R.id.l_recycler_foot_view);
        addView(mAutoLoadRecyclerView, lp3);

        LayoutParams lp4 =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp4.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addView(mEmptyView, lp4);
    }

    private void smoothScrollTo(final View view, int startHeight, int endHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(startHeight, endHeight);
        Log.d("test", "start: " + startHeight + "  end：" + endHeight);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d("test", "value: " + (int) animation.getAnimatedValue());
                view.setPadding(0, (int) animation.getAnimatedValue(), 0, 0);
            }
        });
        animator.start();
    }

    public void setAdapter(RecyclerViewAdapter adapter) {
        this.mAdapter = adapter;
        mAutoLoadRecyclerView.setAdapter(mAdapter);
    }

    public RecyclerViewAdapter getAdapter() {
        return this.mAdapter;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void setmEmptyView(View view) {
        this.mEmptyView = view;
    }

    public void setmScrollListener(OnListScrollListener mScrollListener) {
        this.mScrollListener = mScrollListener;
    }

    public boolean isRefreshing() {
        return mAutoLoadRecyclerView.IsExecuting();
    }

    public void onRefresh() {
        /*正在加载数据时不执行操作*/
        if (mAutoLoadRecyclerView.IsExecuting())
            return;
        mRefreshView.setVisibility(VISIBLE);
        mRefreshTextView.setText("正在刷新...");
        mAutoLoadRecyclerView.refreshAndClear();
    }

    public AutoLoadRecyclerView getAutoLoadRecyclerView() {
        return mAutoLoadRecyclerView;
    }

}
