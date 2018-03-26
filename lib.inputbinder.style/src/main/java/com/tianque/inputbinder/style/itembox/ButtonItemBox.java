package com.tianque.inputbinder.style.itembox;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.tianque.inputbinder.style.R;

import java.util.ArrayList;
import java.util.List;

public class ButtonItemBox extends ItemBoxBase<String> implements View.OnClickListener {

    private List<OnClickListener> mOnClick;
    private CallBackChangeText mCallBackEdit;

    public ButtonItemBox(Context context) {
        super(context);
    }

    public ButtonItemBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View initExpansionView(Context context, AttributeSet attrs) throws Exception {
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        Button mButton = (Button) LayoutInflater.from(context).inflate(R.layout.buttonlines, null);
//        Button mButton = new Button(context);

        if (mButton.getVisibility() != View.VISIBLE)
            mButton.setVisibility(View.VISIBLE);
        mButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);

        mButton.setOnClickListener(this);
        try {
            if (!mHaveMoreLine)
                mButton.setInputType(InputType.TYPE_NULL);
        } catch (Exception ignored) {
        }

        mButton.clearFocus();
        int backgroundResId = R.drawable.btn_style_white;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mButton.setBackground((getResources().getDrawable(backgroundResId, null)));
        } else {
            mButton.setBackgroundDrawable(getResources().getDrawable(backgroundResId));
        }
        mButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        mButton.setFocusableInTouchMode(false);
        mButton.setLongClickable(false);
        mButton.setSingleLine(false);

        if (!isVisibleRightArrow) {
            mButton.setHint(mBoxHint != null ? mBoxHint : "");
        } else {
            mButton.setHint("");
            Drawable btnArrow = getResources().getDrawable(R.drawable.tree_ec);
            mButton.setCompoundDrawablesWithIntrinsicBounds(null, null, btnArrow, null);
        }

        if (isVisibleDrownArrow) {
            Drawable btnArrow = getResources().getDrawable(R.drawable.tree_ex);
            mButton.setCompoundDrawablesWithIntrinsicBounds(null, null, btnArrow, null);
        }
        mButton.setText("");
        mButton.setLayoutParams(lp);
        mButton.setPadding(0, 0, dip2px(mContext, 6), 0);
        mButton.setId(getId());
        mButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        return mButton;
    }

    public Button getButton() {
        return (Button) getExpansionView();
    }

    public String getText() {
        return getButton().getText().toString().trim();
    }

    public void setText(Spanned text) {
        getButton().setText(text);
        if (mCallBackEdit != null) {
            mCallBackEdit.getValue(text.toString());
        }
//        setBoxVisible();
    }

    public void setText(CharSequence text) {
        getButton().setText(text);
        if (mCallBackEdit != null) {
            mCallBackEdit.getValue(text.toString());
        }
    }

    public void setText(String text) {
        setText(text, true);
    }

    public void setText(String text, boolean isCallBack) {
        getButton().setText(text);
        if (mCallBackEdit != null && isCallBack) {
            mCallBackEdit.getValue(text);
        }
    }

    public List<OnClickListener> getOnClickListener() {
        return mOnClick;
    }

    @Override
    public void setOnClickListener(OnClickListener clickListener) {
        if (mOnClick == null)
            mOnClick = new ArrayList<OnClickListener>();
        mOnClick.add(clickListener);
    }

    @Override
    public void onClick(View v) {
        if (mOnClick == null)
            return;
        for (OnClickListener listener : mOnClick) {
            try {
            listener.onClick(ButtonItemBox.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getContent() {
        if (getButton() != null)
            return getButton().getText().toString();
        return null;
    }

    @Override
    public void setContent(String text) {
        if (text != null) {
            getButton().setText(text);
        }
        if (mCallBackEdit != null) {
            mCallBackEdit.getValue(text);
        }
//        setBoxVisible();
    }


    public void setInfoIcon(String info) {
        setInfoIcon(getButton(), info);
    }

    public void setInfoIcon(Button view, String info) {
        if (view == null)
            return;
        if (!TextUtils.isEmpty(info)) {
            Drawable btngo = getResources().getDrawable(R.drawable.right_arrow);
            view.setText(info);
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, btngo, null);
        } else {
            view.setText("");
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            getButton().setTextColor(Color.argb(160, 0, 0, 0));
            getButton().setBackgroundColor(Color.TRANSPARENT);
            getButton().setHint("");
        } else {
            if (mBoxHint != null) {
                getButton().setHint(mBoxHint);
            } else {
                getButton().setTextColor(Color.BLACK);
                getButton().setHint("");
            }
            getButton().setBackgroundDrawable(
                    (getResources().getDrawable(R.drawable.btn_style_white)));
        }
//        setBoxVisible();
    }

    public void setCallBackChangeText(CallBackChangeText callBackEdit) {
        mCallBackEdit = callBackEdit;
    }

    public void setHintText(String txt) {
        getButton().setHint(txt);
    }

    public interface CallBackChangeText {
        void getValue(String txt);
    }

    public void setVisibleRightArrow(boolean visibleRightArrow) {
        this.isVisibleRightArrow = visibleRightArrow;
        if (!isVisibleRightArrow) {
            getButton().setHint(mBoxHint != null ? mBoxHint : "");
        } else {
            getButton().setHint("");
            Drawable btnArrow = getResources().getDrawable(R.drawable.tree_ec);
            getButton().setCompoundDrawablesWithIntrinsicBounds(null, null, btnArrow, null);
        }
    }

    public void setVisibleDownArrow(boolean visibleDownArrow) {
        this.isVisibleDrownArrow = visibleDownArrow;
        if (!isVisibleDrownArrow) {
            getButton().setHint(mBoxHint != null ? mBoxHint : "");
        } else {
            Drawable btnArrow = getResources().getDrawable(R.drawable.tree_ex);
            getButton().setCompoundDrawablesWithIntrinsicBounds(null, null, btnArrow, null);
        }
    }
}
