package com.wayj.inputbinder.example.entity;

import java.io.Serializable;

/**
 * Created by way on 2017/9/14.
 */

public abstract class AutoListCard implements AutoListCardInf,Serializable{


    private int holderLayoutResId;
    public int getDuplicateIdCardResId(){
        return -1;
    }

    public abstract Integer getModuleNameRes() ;
}
