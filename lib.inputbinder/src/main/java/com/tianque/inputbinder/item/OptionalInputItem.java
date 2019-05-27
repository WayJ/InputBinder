package com.tianque.inputbinder.item;

import android.util.Pair;
import android.view.View;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.inf.RequestValueContract;
import com.tianque.inputbinder.rxjava.SimpleObserver;
import com.tianque.inputbinder.viewer.ViewContentProxy;
import com.tianque.inputbinder.util.Logging;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Action;


/**
 * Created by way on 17/5/18.
 */
public class OptionalInputItem extends ButtonInputItem implements RequestValueContract.RequestValueObserver {

    private OptionalData optionalData;
    private OptionalDialogAction optionalDialogAction;

    public OptionalData getOptionalData() {
        return optionalData;
    }

    public void setOptionalData(OptionalData optionalData) {
        this.optionalData = optionalData;
    }

    @Override
    public void onRequestValue(Observable observable) {
        optionalData.onRequestValue(observable.doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                if (isStarted&&optionalData.selectedIndex>=0)
                    setSelectedIndex(optionalData.selectedIndex);
            }
        }));
    }


    public OptionalInputItem(int resourceId) {
        super(resourceId);
    }

    @Override
    public String getRequestValue() {
        if (optionalData == null || optionalData.selectedIndex < 0) {
            return null;
        } else
            return String.valueOf(optionalData.getSelectedValue());
    }

    @Override
    public void onStart() {
        super.onStart();

        if (optionalDialogAction == null) {
            if (InputBinder.getInputBinderStyleAction() != null) {
                optionalDialogAction = InputBinder.getInputBinderStyleAction().getOptionalDialogAction();
            } else {
                throw new RuntimeException("InputBinder.getInputBinderStyleAction() is null");
            }
        }

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (optionalDialogAction != null)
                    optionalDialogAction.showDialog(OptionalInputItem.this);
                else {
                    Logging.e(new Exception("optionalDialogAction is null"));
                }
            }
        });
    }

    @Override
    public ViewContentProxy initDefaultViewProxy(View view) {
        return null;
    }


    public int getSelectedIndex() {
        return optionalData.selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        if (selectedIndex < 0)
            return;
        optionalData.selectedIndex = selectedIndex;
        if (optionalData.dataList == null || optionalData.dataList.size() == 0) {
            throw new RuntimeException("dataList 为空，selectedIndex = " + selectedIndex);
        } else {
            setDisplayText(optionalData.getOptionalTexts()[selectedIndex]);
        }
    }

    public interface OptionalDialogAction {
        void showDialog(OptionalInputItem inputItem);
    }

    public static class OptionalData<RV> implements RequestValueContract.RequestValueObserver<RV> {
        private List<Pair<String, RV>> dataList = new ArrayList<>();
        //        private String[] optionalTexts;
        //设置默认值
        private int selectedIndex = -1;

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
        public void onRequestValue(Observable<RV> observable) {
            observable.subscribe(new SimpleObserver<RV>() {
                @Override
                public void onNext(RV value) {
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).second.equals(value)) {
                            selectedIndex = i;
                            break;
                        }
                    }
                }
            });
        }

        public RV getSelectedValue() {
            if (dataList == null || dataList.size() == 0 || selectedIndex == -1) {
                return null;
            } else
                return dataList.get(selectedIndex).second;
        }

    }

}
