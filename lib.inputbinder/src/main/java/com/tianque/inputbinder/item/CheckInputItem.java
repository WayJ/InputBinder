package com.tianque.inputbinder.item;

import android.view.View;
import android.widget.CheckBox;

import com.tianque.inputbinder.inf.ViewProxyInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by way on 17/5/18.
 */

public class CheckInputItem extends InputItem<Boolean> {

    private CheckBox.OnCheckedChangeListener onCheckedChangeListener;
    private boolean isChecked;
    private boolean isOtherDic;
    private String isOtherCheck;
    private List<View> mDependedOpenView;//依赖该inputItem来控制是否显示的view的集合,该控件为true时，这个集合中控件都显示
    private List<View> mDependedCloseView;//依赖该inputItem来控制是否显示的view的集合，该控件为true时，这个集合中控件都不显示

    /**
     * 该inputItem获得提交参数时候，选中不选中各对应的传递的值
     * 默认： {TRUE.toString(),FALSE.toString()}
     */
    private String[] checkValues;

    public CheckInputItem(int resourceId) {
        super(resourceId);
//        setInputType(BehaviorType.CheckBox);
    }


    public CheckInputItem(int resourceId, boolean isChecked) {
        this(resourceId);
        setChecked(isChecked);
    }

    public CheckInputItem(int resourceId, boolean isChecked, boolean isOtherDic, String isOtherCheck) {
        this(resourceId);
        setChecked(isChecked);
        this.isOtherDic = isOtherDic;
        this.isOtherCheck = isOtherCheck;
    }

    @Override
    public Boolean getDisplayText() {
        return null;
    }

    @Override
    public String getRequestValue() {
        isChecked = getViewProxy().getContent();
        if (checkValues != null) {
            return isChecked ? checkValues[0] : checkValues[1];
        } else
            return Boolean.valueOf(isChecked).toString();
    }

    @Override
    public void setRequestValue(String value) {
        if (isOtherDic) {
            setCheckOtherDicByRequestValue(value);
        } else {
            setCheckedByRequestValue(value);
        }
    }

    private void setCheckOtherDicByRequestValue(String value) {
        if (value.equals("")) {
            setChecked(false);
        } else if (isOtherCheck.equals(value)) {
            setChecked(true);
        }

    }

    public boolean isChecked() {
        return isChecked;
    }


    /**
     * @param value
     */
    public void setCheckedByRequestValue(String value) {
        if (value.equals("1") || value.equals("0")) {
            setCheckValues("1", "0");
            setChecked(value.equals("1"));
        } else if (value.equals("true") || value.equals("false")) {
            setChecked(Boolean.valueOf(value));
        }
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public CheckBox.OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public CheckInputItem setOnCheckedChangeListener(CheckBox.OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
        return this;
    }

    public CheckInputItem setCheckValues(String trueValue, String falseValue) {
        if (checkValues == null)
            checkValues = new String[2];
        this.checkValues[0] = trueValue;
        this.checkValues[1] = falseValue;
        return this;
    }

    @Override
    public String getRequestKey() {
        return super.getRequestKey();
    }


    public void addDependedView(View view) {
        if (mDependedOpenView == null) {
            mDependedOpenView = new ArrayList<>();
        }
        mDependedOpenView.add(view);
    }

    public List<View> getDependedView() {
        return mDependedOpenView;
    }

    public List<View> getDependedCloseView() {
        return mDependedCloseView;
    }

    public void addDependedCloseView(View view) {
        if (mDependedCloseView == null) {
            mDependedCloseView = new ArrayList<>();
        }
        mDependedCloseView.add(view);
    }


    @Override
    public ViewProxyInterface<Boolean> initDefaultViewProxy(View view) {
        return new ViewProxyInterface<Boolean>() {

            @Override
            public Boolean getContent() {
                return false;
            }

            @Override
            public void setContent(Boolean isChecked) {
                if (getView() instanceof CheckBox) {
                    ((CheckBox) getView()).setChecked(isChecked);
                }
            }

        };
    }
}
