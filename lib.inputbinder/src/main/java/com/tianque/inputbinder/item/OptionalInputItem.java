package com.tianque.inputbinder.item;

import android.text.TextUtils;
import android.view.View;

import com.tianque.inputbinder.inf.ViewProxyInterface;


/**
 * Created by way on 17/5/18.
 */
public class OptionalInputItem extends InputItem {
    protected String displayText = "";

    private String[] selectTexts;
    private String[] selectValues;
    //设置默认值
    private int selectedIndex = -1;


    public OptionalInputItem(int resourceId) {
        super(resourceId);
    }


    @Override
    public String getDisplayText() {
        return displayText;
    }

    @Override
    public String getRequestValue() {
        if (selectValues == null || selectValues.length == 0 || selectedIndex == -1) {
            return null;
        } else
            return selectValues[selectedIndex];
    }

    @Override
    public void setRequestValue(String value) {
        if (!TextUtils.isEmpty(value) && selectValues != null) {
            for (int i = 0; i < selectValues.length; i++) {
                if (selectValues[i].equals(value)) {
                    setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    @Override
    public ViewProxyInterface initDefaultViewProxy(View view) {
        return null;
    }


    public String[] getSelectTexts() {
        return selectTexts;
    }

    public void setSelectTexts(String[] selectTexts) {
        this.selectTexts = selectTexts;
    }

    public String[] getSelectValues() {
        return selectValues;
    }

    public void setSelectValues(String[] selectValues) {
        this.selectValues = selectValues;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        if (selectedIndex < 0)
            return;
        this.selectedIndex = selectedIndex;
        if (selectTexts == null || selectTexts.length == 0) {
            return;
        } else {
            this.displayText = selectTexts[selectedIndex];
        }
    }

}
