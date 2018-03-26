package com.tianque.inputbinder.style.itembox;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.tianque.inputbinder.style.R;


public class EditItemBox extends ItemBoxBase<String> implements View.OnClickListener {

    private OnClickListener mOnClickListener;
    private CallBackEdit mCallBackEdit;
    private CallBackFinish mCallBackFinish;
    private KeyListener mKeyListener;

    public EditItemBox(Context context) {
        super(context);
    }

    public EditItemBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setFocusable(boolean focusable) {
        getExpansionView().setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                       int dstart, int dend) {
                return source.length() < 1 ? dest.subSequence(dstart, dend) : "";
            }
        }});
        getExpansionView().setFocusable(focusable);
    }

    public void setHint(CharSequence hint) {
        getExpansionView().setHint(hint);
    }

    @Override
    public View initExpansionView(Context context, AttributeSet attrs) {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final EditText mExpansionView = (EditText) LayoutInflater.from(context).inflate(
                R.layout.editlines, null);
        mExpansionView.setPadding(0, 20, 20, 20);
        // mExpansionView=new EditText(mContext, attrs);
        // LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT,
        // LayoutParams.WRAP_CONTENT);
        // mExpansionView.setLayoutParams(layoutParams);
        // mExpansionView.setPadding(dip2px(3), dip2px(10), dip2px(12),
        // dip2px(10));
        // mEditText = (EditText) mExpansionView.findViewById(R.id.editText1);
        switch (editType) {
            case 0:
                mExpansionView.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 1:
                mExpansionView.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 2:
                mExpansionView.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case 3:
                mExpansionView.setInputType(InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case 4:
                /*只能输入数字-*/
                mExpansionView.setKeyListener(new DialerKeyListener() {
                    @Override
                    public int getInputType() {
                        return InputType.TYPE_CLASS_NUMBER;
                    }

                    @Override
                    protected char[] getAcceptedChars() {
                        String cc = "0987654321-";
                        return cc.toCharArray();
                    }
                });
                break;
        }
        try {
            int inputType = attrs.getAttributeIntValue(nameSpace, "inputType", -1);
            if (inputType >= 0) {
                mExpansionView.setInputType(inputType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//		mExpansionView.setHint("");
        try {
            int hintTextResId = attrs.getAttributeResourceValue(nameSpace, "hint", -1);
            if (hintTextResId > 0) {
                String hint = context.getString(hintTextResId);
                if (TextUtils.isEmpty(hint)) {
                    hint = attrs.getAttributeValue(nameSpace, "hint");
                }
                if (!TextUtils.isEmpty(hint)) {
                    mExpansionView.setHint(hint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // maxLength
        if (mEditLength > 0) {
            mExpansionView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                    mEditLength)});
        }

        // lines
        try {
            int lines = attrs.getAttributeIntValue(nameSpace, "lines", -1);
            if (lines > 0) {
                mExpansionView.setLines(lines);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // minLines
        try {
            int lines = attrs.getAttributeIntValue(nameSpace, "minLines", -1);
            if (lines > 0) {
                mExpansionView.setMinLines(lines);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mExpansionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
        mExpansionView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (mCallBackEdit != null) {
                    mCallBackEdit.getValue(getExpansionView().getText().toString().trim());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (mCallBackFinish != null) {
                    mCallBackFinish.afterTextChanged(arg0.toString());
                }
            }
        });
        mExpansionView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean focus) {
                int color = focus && mExpansionView.isEnabled() ? getResources().getColor(R.color.btn_style_white_color_pressed) : Color.LTGRAY;
                setOnSelectLineColor(color, focus);
                if (mRequire && !focus && getExpansionView().isEnabled()) {
                    if (getExpansionView().getText().toString().equals("")) {
                        getExpansionView().setError(mTitle + "不能为空");
                    }
                } else {
                    getExpansionView().setError(null);
                }
            }
        });
        mExpansionView.setSingleLine(false);
        mExpansionView.setLayoutParams(lp);
        mExpansionView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        return mExpansionView;
    }

    public void setmCallBackFinish(CallBackFinish mCallBackFinish) {
        this.mCallBackFinish = mCallBackFinish;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;

    }

    public String getText() {
        return getExpansionView().getText().toString().trim();
    }

    public void setText(String text) {
        getExpansionView().setText(text);
//        setBoxVisible();
    }

    @Override
    public void onClick(View v) {
        if (mOnClickListener != null)
            mOnClickListener.onClick(v);
    }

    @Override
    public String getContent() {
        if (getExpansionView() != null)
            return getExpansionView().getText().toString();
        return null;
    }

    @Override
    public void setContent(String content) {
        if (getExpansionView() != null)
            getExpansionView().setText(content);
//        setBoxVisible();
    }

    public EditText getEditText() {
        return getExpansionView();
    }

    public void setError(String error) {
        getExpansionView().setError(error);
    }

    public void setCallBackEdit(CallBackEdit mCallBackEdit) {
        this.mCallBackEdit = mCallBackEdit;
    }

    @Override
    public void setEnabled(boolean enabled) {
        // super.setEnabled(enabled);
        if (!enabled) {
            getExpansionView().setTextColor(Color.argb(160, 0, 0, 0));
            getExpansionView().setHint("");
            getExpansionView().clearFocus();
            getExpansionView().setEnabled(false);
            requestFocus();
        } else {
            getExpansionView().setTextColor(Color.BLACK);
            getExpansionView().setHint("");
            getExpansionView().setFocusable(true);
            getExpansionView().setEnabled(true);
            getExpansionView().setClickable(true);
//            mExpansionView.requestFocus();
        }
//        setBoxVisible();
    }

    public void setInputPasswordType(boolean isPwd) {
        if (isPwd)
            getExpansionView().setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        else
            getExpansionView().setInputType(InputType.TYPE_CLASS_TEXT);
    }

    @Override
    public EditText getExpansionView() {
        return (EditText) super.getExpansionView();
    }

    public void setSpecialInputType() {
        getExpansionView().setKeyListener(mKeyListener);
    }

    public void setKeyListener(KeyListener keyListener) {
        this.mKeyListener = keyListener;
    }

    public interface CallBackEdit {
        void getValue(String txt);
    }

    public interface CallBackFinish {
        void afterTextChanged(String txt);
    }
}
