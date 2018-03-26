package com.wayj.inputbinder.example2;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.InputBinderEngine;
import com.tianque.inputbinder.inf.ViewObserver;
import com.tianque.inputbinder.item.InputItem;

/**
 * Created by way on 2018/3/7.
 */

public class InputActivity extends Activity {
    TextView printTxt;
    InputBinder inputBinder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        printTxt= findViewById(R.id.print);
        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printTxt.setText(inputBinder.getRequestMap().toString());
            }
        });
        inputBinder=new InputBinder(this);
        inputBinder.getEngine().setCallBack(new InputBinderEngine.CallBack() {
            @Override
            public void onStart(InputBinderEngine engine) {

            }
        });
//        inputBinder.setViewObserver(new ViewObserver() {
//
//            @Override
//            public void onButtonClick(View view, InputItem inputItem) {
//
//            }
//
//            @Override
//            public void onCheckedChanged(View view, InputItem inputItem) {
//
//            }
//        });
        inputBinder.getEngine().setDebug(true);
        inputBinder.setRootView(this);
        inputBinder.setRelationEntity(Student.class);
        inputBinder.start();
    }
}
