package com.tianque.inputbinder.item;

import android.view.View;

import com.tianque.inputbinder.item.base.BaseTextDisplayInputItem;

public class ButtonInputItem extends BaseTextDisplayInputItem {
    private View.OnClickListener onClickListener;

    public ButtonInputItem(int resourceId) {
        super(resourceId);
    }

    public ButtonInputItem(int resourceId, String displayText) {
        super(resourceId,displayText);
    }

    public ButtonInputItem setOnClickListener(View.OnClickListener onClickListener) {
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
