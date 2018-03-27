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
    @Input(type = InputItemType.Optional,parm = "{'optionalKeys':['key1','key2','key3']}",requestKey = "student.vision")
    String vision;//视力
    @Input(type = InputItemType.Optional,parm = "{'optionalKeys':['key1','key2','key3'],'optionalValues':['value1','value2','value3']}",requestKey = "student.vision2")
    String vision2;//视力
}
