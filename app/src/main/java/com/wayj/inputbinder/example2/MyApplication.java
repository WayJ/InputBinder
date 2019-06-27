package com.wayj.inputbinder.example2;

import android.app.Application;
import android.util.Log;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.inf.InputBinderStyleAction;
import com.tianque.inputbinder.inf.InputConvert;
import com.tianque.inputbinder.item.DateInputItem;
import com.tianque.inputbinder.item.MultiOptionalInputItem;
import com.tianque.inputbinder.item.OptionalInputItem;
import com.tianque.inputbinder.style.DatePickerWidget;
import com.tianque.inputbinder.style.MultiOptionalDialog;
import com.tianque.inputbinder.style.OptionalDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by way on 2018/3/27.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        InputBinder.addDefaultItemConvert("Teacher",TeacherItemConvert.class);

        //设置默认弹窗
        InputBinderStyleAction inputBinderStyleAction=new InputBinderStyleAction();
        inputBinderStyleAction.setOptionalDialogAction(new OptionalInputItem.OptionalDialogAction(){

            @Override
            public void showDialog(final OptionalInputItem inputItem) {
                List<String> textList = new ArrayList<>();
                for (String str : inputItem.getOptionalData().getOptionalTexts()) {
                    textList.add(str+"  ");
                }
                OptionalDialog optionalDialog = new OptionalDialog(inputItem.getView().getContext());
                optionalDialog.showPopWindow(inputItem.getView(), textList, new OptionalDialog.onOptionalItemSelect() {

                    @Override
                    public void getPosition(int position) {
                        inputItem.setSelectedIndex(position);
                    }
                });
            }
        }).setDateDialogAction(new DateInputItem.DateDialogAction() {
            @Override
            public void showDialog(DateInputItem inputItem) {
                DatePickerWidget datePicker = new DatePickerWidget(inputItem.getView().getContext());
                datePicker.notAllowTodayAfter().setPickerCaller(inputItem.getView()).onlyShowDay();
                datePicker.showDatePicker();
            }
        }).setMultiOptionalDialogAction(new MultiOptionalInputItem.MultiOptionalDialogAction() {
            @Override
            public void showDialog(MultiOptionalInputItem inputItem) {

                MultiOptionalDialog muDialog = new MultiOptionalDialog(inputItem.getView().getContext(), inputItem, inputItem.getView(), inputItem.getInputItemProfile());
                muDialog.show();
            }
        });
        InputBinder.setInputBinderStyleAction(inputBinderStyleAction);
    }


}
