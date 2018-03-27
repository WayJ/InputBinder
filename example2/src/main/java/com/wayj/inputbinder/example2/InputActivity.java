package com.wayj.inputbinder.example2;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.InputBinderEngine;
import com.tianque.inputbinder.item.ButtonInputItem;

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
        inputBinder.setRootView(this).setRelationEntity(Student.class);
        ButtonInputItem buttonInputItem = new ButtonInputItem(R.id.input_btn,"点我一下，代码赋值");
        buttonInputItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InputActivity.this,"点击了一下",Toast.LENGTH_SHORT).show();
            }
        });
        buttonInputItem.setDisplayText("点一下试试看");
        inputBinder.addInputItem(buttonInputItem);
        inputBinder.start();
    }
}
