package com.tianque.inputbinder.item;

import android.view.View;

import com.tianque.inputbinder.inf.ViewProxyInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by way on 17/5/18.
 */

public class MultiOptionalInputItem extends InputItem {
    public final String SEPARATOR = ",";
    private List<String> selectTexts=new ArrayList<>();
    private List<String> selectValues=new ArrayList<>();
    private List<String> selectedIndex=new ArrayList<>();

    public MultiOptionalInputItem(int resourceId) {
        super(resourceId);
    }

    @Override
    public String getDisplayText() {
        return getData(selectedIndex, selectTexts);
    }

    @Override
    public String getRequestValue() {
        return getData(selectedIndex, selectValues);
    }

    @Override
    public void setRequestValue(String value) {
        if (value == null) {
            return;
        }
        String[] ids = value.split(SEPARATOR);
        if (ids == null || ids.length == 0 || selectValues == null || selectValues.size() == 0) {
            return;
        }

        if (selectedIndex == null) {
            selectedIndex = new ArrayList<>();
        }else{
            //每次设置值需要清空原来的缓存数据
            selectedIndex.clear();
        }
//        int j=0;
//        for (int i = 0; i < selectValues.size(); i++) {
//            //遍历selectValues和ids
//            if(j<ids.length) {
//                if (selectValues.get(i).equals(ids[j])) {
//                    selectedIndex.add(i + "");
//                    j++;
//                }
//            }
//        }

        for(int j=0;j<ids.length;j++){
            for(int i=0;i<selectValues.size();i++){
                if(selectValues.get(i).equals(ids[j])){
                    selectedIndex.add(i+"");
                    break;
                }
            }
        }

    }

    @Override
    public ViewProxyInterface initDefaultViewProxy(View view) {
        return null;
    }

    public List<String> getSelectTexts() {
        return selectTexts;
    }

    public void setSelectTexts(List<String> selectTexts) {
        this.selectTexts = selectTexts;
    }

    public List<String> getSelectValues() {
        return selectValues;
    }

    public void setSelectValues(List<String> selectValues) {
        this.selectValues = selectValues;
    }

    public List<String> getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(List<String> selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public void upDateSelectedIndex(List<String> upSelectedIndex) {
        if (upSelectedIndex == null || upSelectedIndex.size() == 0) {
            return;
        }

        if (selectedIndex != null && selectedIndex.size() > 0) {
            selectedIndex.clear();
        }
        selectedIndex=upSelectedIndex;
    }


    private String getData(List<String> postions, List<String> s) {
        StringBuffer stringBuffer = new StringBuffer();
        if (s == null || s.size() == 0
                || postions == null || postions.size() == 0) {
            return null;
        } else {
            for (String position : postions) {
                if (!"-1".equals(position)) {
                    stringBuffer.append(s.get(Integer.valueOf(position)) + SEPARATOR);
                }
            }
            String value = stringBuffer.toString();
            if (value == null) {
                return null;
            }
            if (value.length() > 0) {
                return value.substring(0, value.length() - 1);
            } else {
                return "";
            }
        }
    }

}
