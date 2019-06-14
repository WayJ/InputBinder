package com.tianque.inputbinder.item;

import android.util.Pair;
import android.view.View;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.inf.RequestDataContract;
import com.tianque.inputbinder.item.base.BaseButtonInputItem;
import com.tianque.inputbinder.rxjava.SimpleObserver;
import com.tianque.inputbinder.viewer.ViewContentProxy;
import com.tianque.inputbinder.util.Logging;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Action;

public class MultiOptionalInputItem extends BaseButtonInputItem implements RequestDataContract.RequestDataObserver {

    private MultiOptionalData multiOptionalData;

    public MultiOptionalData getMultiOptionalData() {
        return multiOptionalData;
    }

    public void setMultiOptionalData(MultiOptionalData multiOptionalData) {
        this.multiOptionalData = multiOptionalData;
    }

    public MultiOptionalInputItem(int resourceId) {
        super(resourceId);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (multiOptionalDialogAction == null) {
            if (InputBinder.getInputBinderStyleAction() != null) {
                multiOptionalDialogAction = InputBinder.getInputBinderStyleAction().getMultiOptionalDialogAction();
            } else {
                throw new RuntimeException("InputBinder.getInputBinderStyleAction() is null");
            }
        }

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiOptionalDialogAction != null)
                    multiOptionalDialogAction.showDialog(MultiOptionalInputItem.this);
                else {
                    Logging.e(new Exception("optionalDialogAction is null"));
                }
            }
        });
    }

    @Override
    public String getContent() {
        if (multiOptionalData == null || multiOptionalData.selectedIndexs == null || multiOptionalData.selectedIndexs.length == 0) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            String[] selectedValues = multiOptionalData.getOptionalTexts();
            boolean[] selectedIndex = multiOptionalData.selectedIndexs;

            boolean first=true;
            for (int i = 0; i < selectedValues.length; i++) {
                if (selectedIndex[i]) {
                    if(!first)
                        sb.append(SEPARATOR);
                    else
                        first=false;
                    sb.append(selectedValues[i]);
                }
            }
            return sb.toString().trim();
        }
    }

    @Override
    public String getRequestValue() {
        return getData(multiOptionalData);
    }

    @Override
    public ViewContentProxy initDefaultViewProxy(View view) {
        return null;
    }

//    public void setSelectedIndex(int index){
//        selectedIndex[index]=true;
//    }

    public void setSelectedIndexes(boolean[] indexs) {
        if (multiOptionalData.dataList.size() != indexs.length)
            throw new RuntimeException("error size");
        multiOptionalData.selectedIndexs = indexs;
        if (isStarted)
            refreshView();
    }

//    public void setUnSelectedIndex(int index){
//        selectedIndex[index]=false;
//    }
//
//    public boolean[] getSelectedIndex() {
//        return selectedIndex;
//    }


    private String getData(MultiOptionalData multiOptionalData) {
        StringBuilder sb = new StringBuilder();
        if (multiOptionalData == null || multiOptionalData.dataList.size() == 0) {
            return null;
        } else {
            List selectedValues = multiOptionalData.getSelectedValue();
            boolean first=true;
            for (int i = 0; i < selectedValues.size(); i++) {
                if(!first)
                    sb.append(SEPARATOR);
                else
                    first=false;
                sb.append(selectedValues.get(i));
            }
            return sb.toString().trim();
        }
    }


    private MultiOptionalDialogAction multiOptionalDialogAction;

    @Override
    public void postData(Observable observable) {
        multiOptionalData.postData(observable.doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                if (isStarted)
                    refreshView();
            }
        }));
    }

    public interface MultiOptionalDialogAction {
        void showDialog(MultiOptionalInputItem inputItem);
    }

    public static class MultiOptionalData<RV> implements RequestDataContract.RequestDataObserver<List<RV>> {
        private List<Pair<String, RV>> dataList = new ArrayList<>();
        //        private String[] optionalTexts;
        //设置默认值
        private boolean[] selectedIndexs;

        public boolean[] getSelectedIndexs() {
            return selectedIndexs;
        }

        public void setSelectedIndexs(boolean[] selectedIndexs) {
            this.selectedIndexs = selectedIndexs;
        }

        public String[] getOptionalTexts() {
            String[] optionalTexts = new String[dataList.size()];
            int i = 0;
            for (Pair<String, RV> pair : dataList) {
                optionalTexts[i] = pair.first;
                i++;
            }
            return optionalTexts;
        }

        public void add(String display, RV data) {
            dataList.add(new Pair<String, RV>(display, data));
        }

        public void add(int index, String display, RV data) {
            dataList.add(new Pair<String, RV>(display, data));
        }

        @Override
        public void postData(Observable<List<RV>> observable) {
            observable.subscribe(new SimpleObserver<List<RV>>() {
                @Override
                public void onNext(List<RV> values) {
                    if (values == null || values.size() == 0) {
                        return;
                    }
//                    String[] ids = value.split(SEPARATOR);
//                    if (ids.length == 0 || multiOptionalData == null || multiOptionalData.dataList.size() == 0) {
//                        return;
//                    }

                    //每次设置值需要清空原来的缓存数据
                    selectedIndexs = new boolean[dataList.size()];
                    for (int i = 0; i < dataList.size(); i++) {
                        Pair<String, RV> pair = dataList.get(i);
                        for (int j = 0; j < values.size(); j++) {
                            if (pair.equals(values.get(j))) {
                                selectedIndexs[i] = true;
                                break;
                            }
                        }


                    }
                }
            });
        }

        public List<RV> getSelectedValue() {
            if (dataList == null || dataList.size() == 0) {
                return null;
            } else {
                List<RV> rvs = new ArrayList<>();

                for (int i = 0; i < selectedIndexs.length; i++) {
                    if (selectedIndexs[i]) {
                        rvs.add(dataList.get(i).second);
                    }
                }
                return rvs;
            }
        }

    }
}
