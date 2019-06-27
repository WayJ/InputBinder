package com.wayj.inputbinder.example2.model;

import com.wayj.inputbinder.example2.Student;
import com.wayj.inputbinder.example2.api.BaseApi;
import com.wayj.inputbinder.example2.api.StudentApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by way on 2018/3/28.
 */

public class StudentModel  extends BaseApi<StudentApi> {

    public Map<String,String> getSimpleStudent() {
        Map<String,String> data=new HashMap<>();
        data.put("name","hanmeimei");
        data.put("isBoy","1");
        data.put("birthday","1999-1-1");
        data.put("vision","0.6");
        data.put("student.vision2","8");
        data.put("multi","数学");
        return data;
    }

    public Student queryById(int id) throws IOException {
        return getApi().getStudent(id).execute().body();
    }

    public Student createStudentByMap(Map<String,String> map) throws IOException {
        map.put("os","Android 7.0");
        return getApi().create(map).execute().body();
    }

    public Student createStudentByObj(Student student) throws IOException {
        student.setMore("moreMessage");
        return getApi().create2(student).execute().body();
    }
}
