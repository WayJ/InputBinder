package com.wayj.inputbinder.example2;

import android.app.Application;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.inf.InputBinderStyleAction;
import com.tianque.inputbinder.item.OptionalInputItem;
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
        InputBinderStyleAction inputBinderStyleAction=new InputBinderStyleAction();
        inputBinderStyleAction.setOptionalDialogAction(new OptionalDialogAction());
        InputBinder.setInputBinderStyleAction(inputBinderStyleAction);
    }


    public class OptionalDialogAction implements OptionalInputItem.OptionalDialogAction{

        @Override
        public void showDialog(final OptionalInputItem inputItem) {
            List<String> textList = new ArrayList<>();
            for (String str : inputItem.getSelectTexts()) {
                textList.add(str+"  ");
            }
            OptionalDialog optionalDialog = new OptionalDialog(inputItem.getView().getContext());
            optionalDialog.showPopWindow(inputItem.getView(), textList, new OptionalDialog.onOptionalItemSelect() {

                @Override
                public void getPosition(int position) {
                    inputItem.setSelectedIndex(position);
                    inputItem.refreshView();
                }
            });
        }
    }
}
