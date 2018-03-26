package com.tianque.inputbinder.item;

import android.view.View;


/**
 * Created by way on 17/5/18.
 */

public class ButtonInputItem extends TextInputItem {
    private View.OnClickListener onClickListener;

    public ButtonInputItem(int resourceId) {
        super(resourceId);
    }

    public ButtonInputItem(int resourceId,String displayText) {
        this(resourceId);
        setDisplayText(displayText);
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public ButtonInputItem setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }
}
