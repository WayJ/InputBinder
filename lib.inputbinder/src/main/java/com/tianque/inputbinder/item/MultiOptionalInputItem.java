package com.tianque.inputbinder.item;

import android.text.TextUtils;
import android.view.View;
import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.inf.ViewProxyInterface;
import com.tianque.inputbinder.util.Logging;
import org.json.JSONArray;
import java.util.List;

public class MultiOptionalInputItem extends ButtonInputItem {
    public static final String ParmTag_keys="optionalKeys";
    public static final String ParmTag_values="optionalValues";

    public final String SEPARATOR = ",";
    private String[] optionalTexts;
    private String[] optionalValues;
    private boolean[] selectedIndex;

    public MultiOptionalInputItem(int resourceId) {
        super(resourceId);
    }

    @Override
    public void onStart() {
        super.onStart();
        String keysStr = getConfigParm(ParmTag_keys);
        String valuesStr = getConfigParm(ParmTag_values);
        if(!TextUtils.isEmpty(keysStr)){
            try{
                JSONArray jsonArray=new JSONArray(keysStr);
                optionalTexts = new String[jsonArray.length()];
                for(int i=0;i<jsonArray.length();i++){
                    optionalTexts[i]=jsonArray.getString(i);
                }
                if(!TextUtils.isEmpty(valuesStr)){
                    jsonArray=new JSONArray(valuesStr);
                    optionalValues = new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        optionalValues[i]=jsonArray.getString(i);
                    }
                    if(optionalTexts.length!=optionalValues.length)
                        throw new RuntimeException("keys values 的个数不同");
                }else{
                    optionalValues=optionalTexts;
                }
                selectedIndex=new boolean[optionalTexts.length];
            }catch (Exception e){
                Logging.e(e);
            }
        }

        if(multiOptionalDialogAction==null){
            if(InputBinder.getInputBinderStyleAction()!=null) {
                multiOptionalDialogAction = InputBinder.getInputBinderStyleAction().getMultiOptionalDialogAction();
            }else{
                throw new RuntimeException("InputBinder.getInputBinderStyleAction() is null");
            }
        }

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multiOptionalDialogAction!=null)
                    multiOptionalDialogAction.showDialog(MultiOptionalInputItem.this);
                else{
                    Logging.e(new Exception("optionalDialogAction is null"));
                }
            }
        });
    }

    @Override
    public String getDisplayText() {
        return getData(optionalTexts,selectedIndex);
    }

    @Override
    public String getRequestValue() {
        return getData(optionalValues,selectedIndex);
    }

    @Override
    public void setRequestValue(String value) {
        if (value == null) {
            return;
        }
        String[] ids = value.split(SEPARATOR);
        if (ids == null || ids.length == 0 || optionalValues == null || optionalValues.length == 0) {
            return;
        }

        //每次设置值需要清空原来的缓存数据
        selectedIndex=new boolean[optionalValues.length];

        for(int j=0;j<ids.length;j++){
            for(int i=0;i<optionalValues.length;i++){
                if(optionalValues[i].equals(ids[j])){
                    selectedIndex[i]=true;
                    break;
                }
            }
        }
        if(isStarted)
            refreshView();
    }

    @Override
    public ViewProxyInterface initDefaultViewProxy(View view) {
        return null;
    }

    public String[] getSelectTexts() {
        return optionalTexts;
    }

    public void setOptionalTexts(String[] optionalTexts) {
        this.optionalTexts = optionalTexts;
    }

    public String[] getOptionalValues() {
        return optionalValues;
    }

    public void setOptionalValues(String[] optionalValues) {
        this.optionalValues = optionalValues;
    }

    public void setOptionalTexts(List<String> texts) {
        setOptionalTexts(texts.toArray(new String[texts.size()]));
    }

    public void setOptionalValues(List<String> values) {
        setOptionalValues(values.toArray(new String[values.size()]));
    }

    public void setSelectedIndex(int index){
        selectedIndex[index]=true;
    }

    public void setSelectedIndexes(boolean[] indexs){
        if(optionalTexts.length!=indexs.length)
            throw new RuntimeException("error size");
        selectedIndex = indexs;
        if(isStarted)
            refreshView();
    }

    public void setUnSelectedIndex(int index){
        selectedIndex[index]=false;
    }

    public boolean[] getSelectedIndex() {
        return selectedIndex;
    }


    private String getData(String[] selectTexts,boolean[] selectedIndexs) {
        StringBuffer stringBuffer = new StringBuffer();
        if (selectTexts == null || selectTexts.length == 0
                || selectedIndexs == null || selectedIndexs.length == 0) {
            return null;
        } else {
            for (int i=0;i < selectedIndexs.length;i++) {
                if (selectedIndexs[i]) {
                    stringBuffer.append(selectTexts[i] + SEPARATOR);
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


    MultiOptionalDialogAction multiOptionalDialogAction;
    public interface MultiOptionalDialogAction{
        void showDialog(MultiOptionalInputItem inputItem);
    }
}
