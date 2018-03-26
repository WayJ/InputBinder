package com.wayj.inputbinder.example.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.wayj.inputbinder.example.entity.GridPage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import io.reactivex.Observer;

public abstract class RecyclerViewAdapter<VH extends RecyclerViewHolder, T> extends RecyclerView.Adapter<VH> {

    private final int RefreshType = 1;

    protected ArrayList<T> mDataList = new ArrayList<>();
//    private Context mContext;

    private OnItemClickListener mOnItemClickListener;
    private RecyclerViewHolder mHolder;
    public RecyclerViewHolder getmHolder() {
        return mHolder;
    }

    public RecyclerViewAdapter() {
//        this.mContext = context;
    }



    /*复写这个方法时需要获取RecyclerViewHolder后setDefaultViewClickListener，设置点击事件*/
//    @Override
//    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
//        VH viewHolder = new VH(LayoutInflater.from(mContext).inflate(layoutId, parent, false));
//        setDefaultItemClickListener(viewHolder);
//        return viewHolder;
//    }

//    @Override
//    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
//
//    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public T getItem(int position) {
        if (position < mDataList.size())
            return mDataList.get(position);
        return null;
    }

    public T getItemByPosition(int position) {
        if (position < getItemCount())
            return mDataList.get(position);
        else
            return null;
    }

    public void setDataList(Collection<T> list,boolean isLoadPage) {
        this.mDataList.clear();
        this.mDataList.addAll(list);
        if(isLoadPage){
            notifyDataSetChanged();
        }else {
            notifyItemRangeInserted(mDataList.size(), list.size());
        }
    }

    public void setDataList(Collection<T> list){
        setDataList(list,false);
    }


    public void addAll(Collection<T> list) {
        int lastIndex = this.mDataList.size();
        if (this.mDataList.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }

    public void remove(int position) {
        if (this.mDataList.size() > 0) {
            mDataList.remove(position);
            notifyItemRemoved(position);
        }

    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /*设置Item的点击事件*/
    public void setDefaultItemClickListener(final RecyclerViewHolder holder) {
        mHolder=holder;
        if (holder != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(view, holder.getLayoutPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemClickListener != null)
                        return mOnItemClickListener.onItemLongClick(view, holder.getLayoutPosition());
                    return false;
                }
            });
        }
    }


    public abstract void getNextPage(int page, Observer<GridPage<T>> subscriber);

    public  GridPage<T> getNextPage(int page){
        return null;
    }


}
