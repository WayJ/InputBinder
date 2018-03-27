package com.wayj.inputbinder.example2;

import com.tianque.inputbinder.inf.Input;
import com.tianque.inputbinder.item.InputItemType;
import com.tianque.inputbinder.item.OptionalInputItem;

import java.util.Date;

/**
 * Created by way on 2018/3/5.
 */
public class Student {
    int studentId;
    @Input
    String name;
    @Input
    boolean isBoy;
    @Input(type = InputItemType.Date)
    String birthday;
    @Input
    Date comeDay;
    @Input
    String more;
    @Input(type = InputItemType.Optional,parm = "{'optionalKeys':['0.6','0.8','1.0']}",requestKey = "vision")
    String vision;//视力
    @Input(type = InputItemType.Optional,parm = "{'optionalKeys':['0.6','0.8','1.0'],'optionalValues':['6','8','10']}",requestKey = "student.vision2")
    String vision2;//视力

    @Input(type = InputItemType.MultiOptional,parm = "{'optionalKeys':['语文','数学','英语']}")
    String multi;
}
