package com.wayj.inputbinder.example2;

import com.tianque.inputbinder.inf.Input;
import com.tianque.inputbinder.item.InputItemType;

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
}
