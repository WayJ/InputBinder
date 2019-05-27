package com.tianque.inputbinder.item;

import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tianque.inputbinder.inf.RequestValueContract;
import com.tianque.inputbinder.rxjava.SimpleObserver;
import com.tianque.inputbinder.viewer.ViewContentProxy;
import com.tianque.inputbinder.util.ContextUtils;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.util.ResourceUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Action;

public class CheckInputItem extends InputItem<Boolean> implements RequestValueContract.RequestValueObserver{
    public static final String ParmTag_dependent = "dependent";
    public static final String ParmTag_dependent_inversion = "dependent_inversion";

    private CheckBox.OnCheckedChangeListener onCheckedChangeListener;

    private List<Integer> mDependedView;//依赖该inputItem来控制是否显示的view的集合,该控件为true时，这个集合中控件都显示
    private List<Integer> mDependedInversionView;//依赖该inputItem来控制是否显示的view的集合，该控件为true时，这个集合中控件都不显示

    /**
     * 该inputItem获得提交参数时候，选中不选中各对应的传递的值
     * 默认： {FALSE,TRUE}即 checkValues[0]= false,checkValues[1]=true
     */


//    private String[] checkValues;
    private CheckData checkDataDelegate=new CheckData();

    public CheckInputItem(int resourceId) {
        super(resourceId);
    }

//    public CheckInputItem(int resourceId, boolean isChecked) {
//        this(resourceId);
//        setChecked(isChecked);
//    }

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
                checkDataDelegate.isChecked=checked;
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
                    view.setVisibility(checkDataDelegate.isChecked ? View.VISIBLE : View.GONE);
            }
        }
        if (getInputItemHand() != null && mDependedInversionView != null && mDependedInversionView.size() > 0) {
            for (Integer id : mDependedInversionView) {
                View view = getInputItemHand().findViewById(id);
                if (view != null)
                    view.setVisibility(!checkDataDelegate.isChecked ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public Boolean getContent() {
        return isChecked();
    }

    @Override
    public String getRequestValue() {
        boolean check = getViewProxy().getContent();
        if (checkDataDelegate != null) {
            return String.valueOf(checkDataDelegate.getRequestValue(check));
        } else
            return Boolean.valueOf(check).toString();
    }


    public boolean isChecked() {
        if(checkDataDelegate==null)
            return false;
        else
            return checkDataDelegate.isChecked;
    }

    public void setChecked(boolean checked) {
        checkDataDelegate.isChecked = checked;
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

    public CheckData getCheckData() {
        return checkDataDelegate;
    }

    public void setCheckData(CheckData checkData) {
        this.checkDataDelegate = checkData;
    }

    //    public CheckInputItem setCheckValues(String trueValue, String falseValue) {
//        if (checkValues == null)
//            checkValues = new String[2];
//        this.checkValues[0] = trueValue;
//        this.checkValues[1] = falseValue;
//        return this;
//    }

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
    public ViewContentProxy<Boolean> initDefaultViewProxy(View view) {
        return new ViewContentProxy<Boolean>() {

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

    @Override
    public void onRequestValue(Observable observable) {
        checkDataDelegate.onRequestValue(observable.doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                setChecked(checkDataDelegate.isChecked);
            }
        }));
    }


    public static class CheckData<RV> implements RequestValueContract.RequestValueObserver<RV> {
        private Pair<RV,RV> data; // 这里，first为false，second为true
        private boolean isChecked;

        public void setCheckData(RV trueValue,RV falseValue){
            data = new Pair<>(falseValue,trueValue);
        }

        public void setTrueValue(RV trueValue){
            data=new Pair<>(data.first,trueValue);
        }

        public void setFalseValue(RV falseValue){
            data=new Pair<>(falseValue,data.second);
        }

        @Override
        public void onRequestValue(Observable<RV> observable) {
            observable.subscribe(new SimpleObserver<RV>() {
                @Override
                public void onNext(RV rv) {
                    if(rv.equals(data.first))
                        isChecked = false;
                    else if(rv.equals(data.second))
                        isChecked = true;
                }
            });
        }

        public RV getRequestValue(boolean isChecked) {
            if(isChecked)
                return data.second;
            else
                return data.first;
        }
    }
}
