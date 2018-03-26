package com.wayj.inputbinder.example.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by way on 2017/9/22.
 */

public class AutoFormRequestBody {
    private Map<String,String> requestMap;
    private List<File> fileList;
    protected String preFix;

    public AutoFormRequestBody() {
        requestMap=new HashMap<>();
        fileList=new ArrayList<>();
    }

    public Map<String, String> getRequestMap() {
        return requestMap;
    }

    public void setRequestMap(Map<String, String> requestMap) {
        this.requestMap = requestMap;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public String getPreFix() {
        return preFix;
    }

    public void setPreFix(String preFix) {
        this.preFix = preFix;
    }
}
