package com.tianque.inputbinder.item;


import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tianque.inputbinder.inf.ViewBehaviorInterface;

import java.lang.reflect.Method;

/**
 * Created by way on 17/5/18.
 */

public class TextInputItem extends InputItem<String> {
    private String Tag="";

    private String displayText;
    private ViewPoxy viewPoxy;
    public TextInputItem(int resourceId) {
        super(resourceId);
    }

    public TextInputItem(int resourceId,String displayText) {
        this(resourceId);
        setDisplayText(displayText);
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @Override
    public String getDisplayText() {
        return displayText;
    }

    @Override
    public String getRequestValue() {
        //try get new requestValue from view
        if(!getViewPoxy().getContent().equals(displayText)){
            displayText=getViewPoxy().getContent();
        }
        return displayText;
    }

    @Override
    public void setRequestValue(String value) {
        if(value!=null){
            this.displayText = value;
        }
    }

    @Override
    public ViewBehaviorInterface<String> getViewPoxy() {
        if(viewPoxy==null){
            viewPoxy = new ViewPoxy(getView());
        }
        return viewPoxy;
    }

    private class ViewPoxy implements ViewBehaviorInterface<String> {
        ViewBehaviorInterface<String> viewBehavior;
        public ViewPoxy(View view){
            if(view instanceof ViewBehaviorInterface){
                viewBehavior = (ViewBehaviorInterface)view;
            }
        }

        @Override
        public void setContent(String content) {
            if(viewBehavior!=null)
                viewBehavior.setContent(content);
            else{
                if(TextUtils.isEmpty(content)||getView()==null)
                    return;
                if (getView() instanceof TextView) {
                    ((TextView) getView()).setText(content);
                } else {
                    try {
                        Method m = getView().getClass().getDeclaredMethod("setText",
                                CharSequence.class);
                        m.invoke(getView(), content);
                    } catch (Exception e) {
                        Log.e(Tag,e.getMessage(),e);
                        throw new RuntimeException(
                                "can not call setContent method");
                    }
                }
            }
        }

        public String getContent(){
            if(viewBehavior!=null)
                return viewBehavior.getContent();
            else{
                if (getView() instanceof TextView) {
                    return ((TextView) getView()).getText().toString();
                }else
                    throw new RuntimeException("未找到 getContent 方法");
            }
        }

    }
}
