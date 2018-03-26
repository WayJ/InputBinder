package com.wayj.inputbinder.example.widget.recyclerview;

/**
 * 主动加载和刷新监听
 */
public interface OnListScrollListener {
    void onRefresh();

    void onLoadMore();
}
