package com.tianque.inputbinder.item;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tianque.inputbinder.inf.ViewProxyInterface;
import com.tianque.inputbinder.util.Logging;

import java.lang.reflect.Method;

public class TextInputItem extends InputItem<String> {

    private String displayText;

    public TextInputItem(int resourceId) {
        super(resourceId);
    }

    public TextInputItem(int resourceId, String displayText) {
        this(resourceId);
        setDisplayText(displayText);
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
        if(isStarted)
            refreshView();
    }

    @Override
    public String getContent() {
        return displayText;
    }

    @Override
    public String getRequestValue() {
        //try get new requestValue from view
        String content = getViewProxy().getContent();
        if(TextUtils.isEmpty(content)){
            displayText="";
        }else if (!content.equals(displayText)) {
            displayText = content;
        }
        return displayText;
    }

    @Override
    public void setRequestValue(String value) {
        this.displayText = value;
        if(isStarted)
            refreshView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!TextUtils.isEmpty(displayText)){
            refreshView();
        }
    }

    @Override
    public ViewProxyInterface initDefaultViewProxy(View view) {
        return new ViewProxyInterface<String>() {

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


}
