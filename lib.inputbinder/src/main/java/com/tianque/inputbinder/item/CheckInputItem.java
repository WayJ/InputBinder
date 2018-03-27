package com.tianque.inputbinder.item;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import com.tianque.inputbinder.inf.ViewProxyInterface;

import java.util.ArrayList;
import java.util.List;

public class CheckInputItem extends InputItem<Boolean> {

    private CheckBox.OnCheckedChangeListener onCheckedChangeListener;
    private boolean isChecked;
    private List<View> mDependedOpenView;//依赖该inputItem来控制是否显示的view的集合,该控件为true时，这个集合中控件都显示
    private List<View> mDependedCloseView;//依赖该inputItem来控制是否显示的view的集合，该控件为true时，这个集合中控件都不显示

    /**
     * 该inputItem获得提交参数时候，选中不选中各对应的传递的值
     * 默认： {FALSE,TRUE}即 checkValues[0]= false,checkValues[1]=true
     */
    private static final String SEPARATOR = ",";
    private static final String defaultCheckValues1="0"+SEPARATOR+"1";
    private static final String defaultCheckValues2="false"+SEPARATOR+"true";
    private static final String defaultCheckValues3="FALSE"+SEPARATOR+"TRUE";
    private String[] checkValues;

    public CheckInputItem(int resourceId) {
        super(resourceId);
    }

    public CheckInputItem(int resourceId, boolean isChecked) {
        this(resourceId);
        setChecked(isChecked);
    }

    @Override
    public void onStart() {
        super.onStart();
        //        if (!TextUtils.isEmpty(attr.dependent)) {
//            String viewName = attr.dependent;
//            InputItem inputItem;
//            if (viewName.startsWith("-")) {
//                inputItem = mAutoInputItems.get(viewName.substring(1));
//            } else {
//                inputItem = mAutoInputItems.get(viewName);
//            }
//            if (inputItem == null) {
//                throw new RuntimeException("R.id." + attr.key + " 控件所依赖的 R.id." + attr.dependent + "未找到，必须先定义被依赖控件，请检查layout文件和viewconfig文件");
//            } else if (!(inputItem instanceof CheckInputItem)) {
//                throw new RuntimeException("R.id." + attr.key + " 控件所依赖的 R.id." + attr.dependent + " 只能是CheckInputItem");
//            }
//            if (viewName.startsWith("-")) {
//                ((CheckInputItem) inputItem).addDependedCloseView(view);
//                view.setVisibility(((CheckInputItem) inputItem).isChecked() ? View.GONE : View.VISIBLE);
//            } else {
//                ((CheckInputItem) inputItem).addDependedView(view);
//                view.setVisibility(((CheckInputItem) inputItem).isChecked() ? View.VISIBLE : View.GONE);
//            }
//        }
    }

    @Override
    public Boolean getContent() {
        return isChecked();
    }

    @Override
    public String getRequestValue() {
        isChecked = getViewProxy().getContent();
        if (checkValues != null) {
            return isChecked ? checkValues[1] : checkValues[0];
        } else
            return Boolean.valueOf(isChecked).toString();
    }

    @Override
    public void setRequestValue(String value) {
        if(TextUtils.isEmpty(value))
            return;
        if(checkValues==null){
            initDefaultCheckValuesWithValue(value);
        }
        if(checkValues!=null){
            if(value.equals(checkValues[0]))
                setChecked(false);
            else if(value.equals(checkValues[1]))
                setChecked(true);

        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    private void initDefaultCheckValuesWithValue(String value) {
        if(defaultCheckValues1.contains(value)){
            checkValues=defaultCheckValues1.split(SEPARATOR);
        }else if(defaultCheckValues2.contains(value)){
            checkValues=defaultCheckValues2.split(SEPARATOR);
        }else if(defaultCheckValues3.contains(value)){
            checkValues=defaultCheckValues3.split(SEPARATOR);
        }
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        if(isStarted)
            refreshView();
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
