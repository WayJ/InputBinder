package com.tianque.inputbinder.item;

import android.text.TextUtils;
import android.view.View;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.inf.ViewProxyInterface;
import com.tianque.inputbinder.util.Logging;

import org.json.JSONArray;


/**
 * Created by way on 17/5/18.
 */
public class OptionalInputItem extends ButtonInputItem {
    public static final String ParmTag_keys="optionalKeys";
    public static final String ParmTag_values="optionalValues";
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
    public void onStart() {
        super.onStart();
        String optionalKeys = getConfigParm(ParmTag_keys);
        String optionalValues = getConfigParm(ParmTag_values);
        if(!TextUtils.isEmpty(optionalKeys)){
            try{
                JSONArray jsonArray=new JSONArray(optionalKeys);
                selectTexts = new String[jsonArray.length()];
                for(int i=0;i<jsonArray.length();i++){
                    selectTexts[i]=jsonArray.getString(i);
                }
                if(!TextUtils.isEmpty(optionalValues)){
                    jsonArray=new JSONArray(optionalValues);
                    selectValues = new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++){
                        selectValues[i]=jsonArray.getString(i);
                    }
                    if(selectTexts.length!=selectValues.length)
                        throw new RuntimeException("keys values 的个数不同");
                }else{
                    selectValues=selectTexts;
                }
            }catch (Exception e){
                Logging.e(e);
            }
        }

        if(optionalDialogAction==null){
            if(InputBinder.getInputBinderStyleAction()!=null) {
                optionalDialogAction = InputBinder.getInputBinderStyleAction().getOptionalDialogAction();
            }else{
                throw new RuntimeException("InputBinder.getInputBinderStyleAction() is null");
            }
        }

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(optionalDialogAction!=null)
                    optionalDialogAction.showDialog(OptionalInputItem.this);
                else{
                    Logging.e(new Exception("optionalDialogAction is null"));
                }
            }
        });
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

    public void setSimpleOptional(String[] selectTexts) {
        this.selectTexts = selectTexts;
        this.selectValues = selectTexts;
    }

    OptionalDialogAction optionalDialogAction;
    public interface OptionalDialogAction{
         void showDialog(OptionalInputItem inputItem);
    }

}
