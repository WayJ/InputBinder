package com.wayj.inputbinder.example.widget.recyclerview.stickyheader;

import android.view.ViewGroup;

import com.wayj.inputbinder.example.widget.recyclerview.RecyclerViewHolder;


/**
 * Created at 陈 on 2016/11/29.
 *
 * @author chenwanfeng
 * @email 237142681@qq.com
 */

/**
 * The adapter to assist the {@link StickyHeaderDecoration} in creating and binding the header views.
 *
 * @param <T> the header view holder
 */

public interface StickyHeaderAdapter<T extends RecyclerViewHolder> {
    /**
     * Returns the header id for the item at the given position.
     *
     * @param position the item position
     * @return the header id
     */
    long getHeaderId(int position);

    /**
     * Creates a new header ViewHolder.
     *
     * @param parent the header's view parent
     * @return a view holder for the created view
     */
    T onCreateHeaderViewHolder(ViewGroup parent);

    /**
     * Updates the header view to reflect the header data for the given position
     * @param holder the header view holder
     * @param position the header's item position
     */
    void onBindHeaderViewHolder(T holder, int position);
}
