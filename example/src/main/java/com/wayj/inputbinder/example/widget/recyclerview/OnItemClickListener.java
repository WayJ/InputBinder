package com.wayj.inputbinder.example.widget.recyclerview;

import android.view.View;

public interface OnItemClickListener {
    void onItemClick(View view, int position);

    boolean onItemLongClick(View view, int position);
}
