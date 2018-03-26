package com.tianque.inputbinder.item;

import android.view.View;
import android.widget.Button;


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
//
//    public View.OnClickListener getOnClickListener() {
//        return onClickListener;
//    }

    public ButtonInputItem setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getView()!=null&&onClickListener!=null){
//            if(getView() instanceof ButtonClickSetting){
//                ((ButtonClickSetting)getView()).setOnClickListener(onClickListener);
//            }else if(getView() instanceof Button){
                getView().setOnClickListener(onClickListener);
//            }
        }
    }

//    public interface ButtonClickSetting{
//        void setOnClickListener(View.OnClickListener onClickListener);
//    }
}
