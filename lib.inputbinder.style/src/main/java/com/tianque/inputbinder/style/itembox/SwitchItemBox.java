package com.tianque.inputbinder.style.itembox;

import android.content.Context;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.tianque.inputbinder.style.R;

import java.util.ArrayList;
import java.util.List;

public class SwitchItemBox extends ItemBoxBase<Boolean> {

    private static int TAG_SWITCH_STATE = R.id.tag_switch_itembox_state;

    //	private List<OnClickListener> mOnClick;
    private boolean switchable = true;
    private List<CompoundButton.OnCheckedChangeListener> onCheckedChangeListener;

    public SwitchItemBox(Context context) {
        super(context);
    }

    public SwitchItemBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View initExpansionView(Context context, AttributeSet attrs) {

        LayoutParams lp_main = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = null;
        if (attrs == null)
            linearLayout = new LinearLayout(mContext);
        else
            linearLayout = new LinearLayout(mContext, attrs);

        linearLayout.setLayoutParams(lp_main);
        linearLayout.setPadding(0, 10, 0, 10);
        linearLayout.setGravity(Gravity.RIGHT);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        CheckSwitchButton mButton = new CheckSwitchButton(mContext, attrs);
        // com.tianque.mobile.library.view.widget.sortlistview.CheckSwitchButton
        // Button mButton = null;
        // if (attrs == null)
        // mButton = new Button(mContext);
        // else
        // mButton = new Button(mContext, attrs);

        //		 mButton.setBackgroundResource(R.drawable.switch_off_normal);
        // mButton.setTag(TAG_SWITCH_STATE, false);

        mButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        //		mButton.setFocusableInTouchMode(false);
        //		mButton.setLongClickable(false);
        //		mButton.setClickable(false);
        mButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (onCheckedChangeListener != null)
                    for (CompoundButton.OnCheckedChangeListener iterable_element : onCheckedChangeListener) {
                        iterable_element.onCheckedChanged(getButton(), arg1);
                    }
            }
        });
        //		linearLayout.setFocusableInTouchMode(false);
        //		linearLayout.setLongClickable(false);

        // mButton.setText("");
        // mButton.setTag(TAG_SWITCH_STATE, false);
        //		mButton.setMaxHeight(40);
        lp.setMargins(px2dip(mContext, 15), 0, px2dip(mContext, 20), 0);
        mButton.setLayoutParams(lp);
        //		linearLayout.setId(getId());
        //		linearLayout.setOnClickListener(this);
        linearLayout.addView(mButton);
        mButton.setVisibility(VISIBLE);
        linearLayout.setVisibility(VISIBLE);
        return linearLayout;
    }

    public CheckSwitchButton getButton() {
        return (CheckSwitchButton) ((LinearLayout) getExpansionView()).getChildAt(0);

    }

    @Override
    public View getExpansionView() {
        return super.getExpansionView();
    }

    public String getText() {
        return getButton().getText().toString();
    }

    public void setText(CharSequence text) {
        getButton().setText(text);
    }

    public void setText(String text) {
        getButton().setText(text);
    }

    public void setText(Spanned text) {
        getButton().setText(text);
    }

    //	@Override
    //	public void setOnClickListener(OnClickListener clickListener) {
    //		if (mOnClick == null)
    //			mOnClick = new ArrayList<OnClickListener>();
    //		mOnClick.add(clickListener);
    //	}
    //
    //	public List<OnClickListener> getOnClickListener() {
    //		return mOnClick;
    //	}

    //	@Override
    //	public void onClick(View v) {
    //		if (switchable) {
    //			if (v instanceof LinearLayout) {
    //				boolean tag = getButton().isChecked();
    //				getButton().setChecked(!tag);
    //				if (onCheckedChangeListener != null)
    //					for (OnCheckedChangeListener iterable_element : onCheckedChangeListener) {
    //						iterable_element.onCheckedChanged(getButton(), !tag);
    //					}
    //			}
    //		}
    //		//		if (mOnClick != null)
    //		//			for (OnClickListener listener : mOnClick) {
    //		//				listener.onClick(SwitchItemBox.this);
    //		//			}
    //	}

    @Override
    public Boolean getContent() {
        if (getButton() != null)
            return Boolean.valueOf(getButton().isChecked());
        return null;
    }

    @Override
    public void setContent(Boolean content) {
        getButton().setChecked(content);
    }

    public boolean getSwitchState() {

        return getButton().isChecked();

    }

    public void setSwitchState(boolean on) {

        getButton().setChecked(on);

    }

    public void setSwitchAble(boolean able) {
        switchable = able;
        getButton().setEnabled(able);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        if (onCheckedChangeListener == null)
            onCheckedChangeListener = new ArrayList<CompoundButton.OnCheckedChangeListener>();
        onCheckedChangeListener.add(checkedChangeListener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getButton().setEnabled(enabled);
        setSwitchAble(enabled);
    }


}
