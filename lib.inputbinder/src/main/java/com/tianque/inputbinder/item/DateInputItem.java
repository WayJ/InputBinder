package com.tianque.inputbinder.item;

import android.view.View;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.util.TimeUtils;
import java.util.Date;

public class DateInputItem extends ButtonInputItem {
//    public static final String DefaultFormat_ALL="yyyy-MM-dd HH:mm:ss";
    public static final String DefaultFormat="yyyy-MM-dd";

    private String format;

    public DateInputItem(int resourceId) {
        super(resourceId);
    }

    public DateInputItem(int resourceId, Date date) {
        this(resourceId);
        setDisplayText(TimeUtils.getDateAsString(date,getFormat()));
    }

    public DateInputItem(int resourceId, Date date,String format){
        this(resourceId);
        setFormat(format);
        setDisplayText(TimeUtils.getDateAsString(date,format));
    }

    public DateInputItem(int resourceId,String date,String format){
        this(resourceId,new Date(date),format);
    }

    public DateInputItem(int resourceId,String date){
        this(resourceId);
        setDisplayText(date);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(dateDialogAction==null){
            if(InputBinder.getInputBinderStyleAction()!=null) {
                dateDialogAction = InputBinder.getInputBinderStyleAction().getDateDialogAction();
            }else{
                throw new RuntimeException("InputBinder.getInputBinderStyleAction() is null");
            }
        }

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateDialogAction!=null)
                    dateDialogAction.showDialog(DateInputItem.this);
                else{
                    Logging.e(new Exception("optionalDialogAction is null"));
                }
            }
        });
    }

    public String getFormat() {
        return format!=null?format:DefaultFormat;
    }

    public DateInputItem setFormat(String format) {
        this.format = format;
        return this;
    }

    @Override
    public String getContent() {
        String displayText=super.getContent();
//        if(TextUtils.isEmpty(displayText)){
//            return null;
//        }
//        Date date = checkTextIsDateStr(displayText);
//        if(date!=null){
//           super.setDisplayText(TimeUtils.getDateAsString(date,getFormat()));
//        }
        return super.getContent();
    }

    @Override
    public void setDisplayText(String displayText) {
        Date date = checkTextIsDateStr(displayText);
        if(date!=null){
            super.setDisplayText(TimeUtils.getDateAsString(date,getFormat()));
        }else
            super.setDisplayText(displayText);
    }

    protected Date checkTextIsDateStr(String displayText){
        try {
            Date date = TimeUtils.getDateFromString(displayText,DefaultFormat);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    DateDialogAction dateDialogAction;
    public interface DateDialogAction{
        void showDialog(DateInputItem inputItem);
    }
}
