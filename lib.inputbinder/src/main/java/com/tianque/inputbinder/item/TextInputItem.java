package com.tianque.inputbinder.item;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tianque.inputbinder.inf.ViewBehaviorInterface;
import com.tianque.inputbinder.util.Logging;

import java.lang.reflect.Method;

/**
 * Created by way on 17/5/18.
 */

public class TextInputItem extends InputItem<String> {

    private String displayText;
    private ViewProxy viewProxy;
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
        String content = getViewPoxy().getContent();
        if(!TextUtils.isEmpty(content)&&!content.equals(displayText)){
            displayText=content;
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
        if(viewProxy==null){
            viewProxy = new ViewProxy(getView());
        }
        return viewProxy;
    }

    private class ViewProxy implements ViewBehaviorInterface<String> {
        ViewBehaviorInterface<String> viewBehavior;
        public ViewProxy(View view){
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
                        Logging.e(e);
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
                try {
                    Class cls = getView().getClass();
                    Method m = cls.getDeclaredMethod("getText");
                    if(m!=null)
                        return m.invoke(getView()).toString();
                } catch (Exception e) {
                    Logging.e("未找到 getContent 方法");
                    Logging.e(e);
                }
                return null;
            }
        }

    }
}
