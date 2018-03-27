package com.tianque.inputbinder.item;

import android.view.View;

public class ButtonInputItem extends TextInputItem {
    private View.OnClickListener onClickListener;

    public ButtonInputItem(int resourceId) {
        super(resourceId);
    }

    public ButtonInputItem(int resourceId, String displayText) {
        this(resourceId);
        setDisplayText(displayText);
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
