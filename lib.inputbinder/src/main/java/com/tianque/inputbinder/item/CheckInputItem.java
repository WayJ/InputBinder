package com.tianque.inputbinder.item;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tianque.inputbinder.inf.ViewProxyInterface;
import com.tianque.inputbinder.util.ContextUtils;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.util.ResourceUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CheckInputItem extends InputItem<Boolean> {
    public static final String ParmTag_dependent = "dependent";
    public static final String ParmTag_dependent_inversion = "dependent_inversion";

    private CheckBox.OnCheckedChangeListener onCheckedChangeListener;
    private boolean isChecked;
    private List<Integer> mDependedView;//依赖该inputItem来控制是否显示的view的集合,该控件为true时，这个集合中控件都显示
    private List<Integer> mDependedInversionView;//依赖该inputItem来控制是否显示的view的集合，该控件为true时，这个集合中控件都不显示

    /**
     * 该inputItem获得提交参数时候，选中不选中各对应的传递的值
     * 默认： {FALSE,TRUE}即 checkValues[0]= false,checkValues[1]=true
     */
    private static final String defaultCheckValues1 = "0" + SEPARATOR + "1";
    private static final String defaultCheckValues2 = "false" + SEPARATOR + "true";
    private static final String defaultCheckValues3 = "FALSE" + SEPARATOR + "TRUE";
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
        initDependentView();
        addOnCheckedChangeListener();

    }

    private void addOnCheckedChangeListener() {
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                isChecked=checked;
                if(isStarted)
                    refreshDependedView();
            }
        };
        if(getView() instanceof CheckBox){
            ((CheckBox)getView()).setOnCheckedChangeListener(onCheckedChangeListener);
        }else{
            try {
                Method m = getView().getClass().getDeclaredMethod("setOnCheckedChangeListener",
                        CompoundButton.OnCheckedChangeListener.class);
                m.invoke(getView(), onCheckedChangeListener);
            } catch (Exception e) {
                Logging.e(e);
                throw new RuntimeException(
                        "The custom view must implement the setOnCheckedChangeListener(OnCheckedChangeListener l) method");
            }
        }
    }

    private void initDependentView() {
        if (!TextUtils.isEmpty(getConfigParm(ParmTag_dependent))) {
            String dependent = getConfigParm(ParmTag_dependent);
            String[] dependents;
            if (dependent.contains(SEPARATOR)) {
                dependents = dependent.split(SEPARATOR);
            } else {
                dependents = new String[1];
                dependents[0] = dependent;
            }
            if (dependent.length() > 0) {
                if (mDependedView == null)
                    mDependedView = new ArrayList<>();
                for (String viewName : dependents) {
                    int id = ResourceUtils.findIdByName(ContextUtils.getApplicationContext(), viewName);
                    if (id > 0)
                        mDependedView.add(id);
                }
            }
        }
        if (!TextUtils.isEmpty(getConfigParm(ParmTag_dependent_inversion))) {
            String dependent = getConfigParm(ParmTag_dependent_inversion);
            String[] dependents;
            if (dependent.contains(SEPARATOR)) {
                dependents = dependent.split(SEPARATOR);
            } else {
                dependents = new String[1];
                dependents[0] = dependent;
            }
            if (dependent.length() > 0) {
                if (mDependedInversionView == null)
                    mDependedInversionView = new ArrayList<>();
                for (String viewName : dependents) {
                    int id = ResourceUtils.findIdByName(ContextUtils.getApplicationContext(), viewName);
                    if (id > 0)
                        mDependedInversionView.add(id);
                }
            }
        }
        if (isStarted)
            refreshView();
    }

    @Override
    public void refreshView() {
        super.refreshView();
        refreshDependedView();
    }

    private void refreshDependedView() {
        if (getInputItemHand() != null && mDependedView != null && mDependedView.size() > 0) {
            for (Integer id : mDependedView) {
                View view = getInputItemHand().findViewById(id);
                if (view != null)
                    view.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        }
        if (getInputItemHand() != null && mDependedInversionView != null && mDependedInversionView.size() > 0) {
            for (Integer id : mDependedInversionView) {
                View view = getInputItemHand().findViewById(id);
                if (view != null)
                    view.setVisibility(!isChecked ? View.VISIBLE : View.GONE);
            }
        }
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
        if (TextUtils.isEmpty(value))
            return;
        if (checkValues == null) {
            initDefaultCheckValuesWithValue(value);
        }
        if (checkValues != null) {
            if (value.equals(checkValues[0]))
                setChecked(false);
            else if (value.equals(checkValues[1]))
                setChecked(true);

        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    private void initDefaultCheckValuesWithValue(String value) {
        if (defaultCheckValues1.contains(value)) {
            checkValues = defaultCheckValues1.split(SEPARATOR);
        } else if (defaultCheckValues2.contains(value)) {
            checkValues = defaultCheckValues2.split(SEPARATOR);
        } else if (defaultCheckValues3.contains(value)) {
            checkValues = defaultCheckValues3.split(SEPARATOR);
        }
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        if (isStarted)
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


    public void addDependedView(int viewResId) {
        if (mDependedView == null) {
            mDependedView = new ArrayList<>();
        }
        mDependedView.add(viewResId);
    }

    public List<Integer> getDependedView() {
        return mDependedView;
    }

    public List<Integer> getDependedInversionView() {
        return mDependedInversionView;
    }

    public void addDependedInversionView(int viewResId) {
        if (mDependedInversionView == null) {
            mDependedInversionView = new ArrayList<>();
        }
        mDependedInversionView.add(viewResId);
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
