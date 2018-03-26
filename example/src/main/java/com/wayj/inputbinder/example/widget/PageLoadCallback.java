package com.wayj.inputbinder.example.widget;

import com.wayj.inputbinder.example.entity.GridPage;

import java.util.List;

public interface PageLoadCallback<T> {
    void onPreLoad();

    void onPageLoaded(GridPage<T> page, List<T> dataSources);
}
