package com.wayj.inputbinder.example;

import com.wayj.inputbinder.inf.Input;
import com.wayj.inputbinder.item.InputItemType;

import java.util.Date;

/**
 * Created by way on 2018/3/5.
 */

public class Student {
    int studentId;
    @Input
    String name;
    boolean isBoy;
    String birthday;
    Date comeDay;
    String more;
}
