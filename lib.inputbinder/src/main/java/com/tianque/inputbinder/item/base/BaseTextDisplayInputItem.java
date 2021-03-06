package com.tianque.inputbinder.item.base;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.viewer.InputViewer;

import java.lang.reflect.Method;

public abstract class BaseTextDisplayInputItem extends InputItem<String> {

    public BaseTextDisplayInputItem(int resourceId) {
        super(resourceId);
    }

    public BaseTextDisplayInputItem(int resourceId, String displayText) {
        this(resourceId);
        setDisplayText(displayText);
    }

    public void setDisplayText(String displayText) {
//        this.displayText = displayText;
        getViewProxy().setContent(displayText);
        if(isStarted)
            refreshView();
    }

    @Override
    public String getContent() {
        return getViewProxy().getContent();
    }

    @Override
    public String getRequestValue() {
        //try get new requestValue from view
        String content = getViewProxy().getContent();
        if(content==null){
            content="";
        }
        return content;
    }


    @Override
    public void onStart() {
        super.onStart();
//        if(!TextUtils.isEmpty(displayText)){
//            refreshView();
//        }
    }

    @Override
    public InputViewer initDefaultViewProxy(View view) {
        return new InputViewer<String>() {

            @Override
            public void setContent(String content) {
                if (TextUtils.isEmpty(content) || getView() == null)
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


            public String getContent() {
                if (getView() instanceof TextView) {
                    return ((TextView) getView()).getText().toString();
                } else {
                    Logging.e("未找到 getContent or getText 方法");
                    return null;
                }
            }

        };
    }


    public static class DataHold{
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


}
