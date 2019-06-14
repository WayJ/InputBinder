package com.tianque.inputbinder.item.base;

import android.view.View;

public abstract class BaseButtonInputItem extends BaseTextDisplayInputItem {
    private View.OnClickListener onClickListener;

    public BaseButtonInputItem(int resourceId) {
        super(resourceId);
    }

    public BaseButtonInputItem(int resourceId, String displayText) {
        super(resourceId,displayText);
    }

    public BaseButtonInputItem setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null && onClickListener != null) {
            getView().setOnClickListener(onClickListener);
        }
    }

}
