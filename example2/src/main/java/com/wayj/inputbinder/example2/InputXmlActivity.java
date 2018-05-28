package com.wayj.inputbinder.example2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.function.PullMapFunc;
import com.tianque.inputbinder.item.ButtonInputItem;
import com.tianque.inputbinder.item.CheckInputItem;
import com.tianque.inputbinder.item.DateInputItem;
import com.tianque.inputbinder.item.InputItem;
import com.tianque.inputbinder.item.MultiOptionalInputItem;
import com.tianque.inputbinder.item.OptionalInputItem;
import com.tianque.inputbinder.item.TextInputItem;
import com.wayj.inputbinder.example2.model.StudentModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InputXmlActivity  extends AppCompatActivity implements View.OnClickListener {

    private InputBinder mInputBinder;
    private Student mStudent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_xml_activity);
        mInputBinder = new InputBinder(this);
        mInputBinder.setRootView(this).setRelationXmlNode(R.raw.student_info_view_config,
                "Student");
        mInputBinder.addInputItems(makeViewData());
        String action =getIntent().getStringExtra("action");
        if(!TextUtils.isEmpty(action)&&action.equals("edit")){
            //模拟请求接口获得数据并显示
            doRequestAndShow();
        }
        mInputBinder.start();
        if (!TextUtils.isEmpty(action)&&action.equals("view")){
            mInputBinder.getEngine().setAllViewEnable(false);
        }
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               TextView print = (TextView)findViewById(R.id.print);
               print.setText(mInputBinder.getRequestMap().toString().replaceAll(",",",\n"));
            }
        });

    }

    protected List<InputItem> makeViewData() {
        List<InputItem> itemList = new ArrayList<>();
        bindData(mStudent, itemList);
        int[] array = new int[]{1,1,2,4};
        return itemList;
    }

    public void bindData(Student mStudent, List<InputItem> itemList) {
        if (mStudent!=null){
            itemList.add(new TextInputItem(R.id.name,mStudent.getName()));
            itemList.add(new CheckInputItem(R.id.isBoy));
            itemList.add(new DateInputItem(R.id.birthday));
            itemList.add(new ButtonInputItem(R.id.input_btn).setOnClickListener(this));
            OptionalInputItem vision = new OptionalInputItem(R.id.vision);
            vision.addConfigParm("optionalKeys","['0.6','0.8','1.0']");
//            vision.setSelectedIndex(2);
            itemList.add(vision);
            OptionalInputItem vision2 = new OptionalInputItem(R.id.vision2);
            vision2.addConfigParm("optionalKeys","['0.6','0.8','1.0']");
            vision2.addConfigParm("optionalValues","['6','8','10']");
//            vision2.setSelectedIndex(1);
            itemList.add(vision2);
            MultiOptionalInputItem multi = new MultiOptionalInputItem(R.id.multi);
            multi.addConfigParm("optionalKeys","['语文','数学','英语']");
//            multi.setSelectedIndex(1);
            itemList.add(multi);
            itemList.add(new CheckInputItem(R.id.hasRoom,mStudent.getHasRoom()));
            itemList.add(new TextInputItem(R.id.roomNumber,mStudent.getRoomNumber()));
            itemList.add(new TextInputItem(R.id.address,mStudent.getAddress()));
        }
    }

    private void doRequestAndShow() {
        mInputBinder.doQuery(new PullMapFunc() {
            @Override
            public Map doQuery() {
                return new StudentModel().getSimpleStudent();
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

}
