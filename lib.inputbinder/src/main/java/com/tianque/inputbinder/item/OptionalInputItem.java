package com.tianque.inputbinder.item;

import android.util.Pair;
import android.view.View;

import com.tianque.inputbinder.InputBinder;
import com.tianque.inputbinder.function.InputObserve;
import com.tianque.inputbinder.inf.RequestDataContract;
import com.tianque.inputbinder.item.base.BaseButtonInputItem;
import com.tianque.inputbinder.rxjava.SimpleObserver;
import com.tianque.inputbinder.util.Logging;
import com.tianque.inputbinder.viewer.InputViewer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Action;


public class OptionalInputItem extends BaseButtonInputItem implements RequestDataContract.RequestDataObserver, RequestDataContract.getObjectValueFromInput,RequestDataContract.RequestDataObservable {

    private OptionalData optionalData;
    private OptionalDialogAction optionalDialogAction;

    public OptionalData getOptionalData() {
        return optionalData;
    }

    public void setOptionalData(OptionalData optionalData) {
        this.optionalData = optionalData;
    }

    @Override
    public void postData(Observable observable) {
        optionalData.postData(observable.doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                if (isStarted&&optionalData.selectedIndex>=0)
                    setSelectedIndex(optionalData.selectedIndex);
            }
        }));
    }

    @Override
    public Object requestData() {
        return optionalData.requestData();
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

        if(optionalData!=null){
            optionalData.setViewProxy(getViewProxy());
        }
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
        if(isStarted)
            dataObserve.onNext(optionalData.requestData());
    }

    InputObserve dataObserve;
    @Override
    public void observe(InputObserve observe) {
        dataObserve = observe;
    }

    public interface OptionalDialogAction {
        void showDialog(OptionalInputItem inputItem);
    }

    public static class OptionalData<RV> implements RequestDataContract.RequestDataObserver<RV>, RequestDataContract.getObjectValueFromInput<RV> {
        private List<Pair<String, RV>> dataList = new ArrayList<>();
        //        private String[] optionalTexts;
        //设置默认值
        private int selectedIndex = -1;
        private WeakReference<InputViewer<String>> viewProxy;

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


        public void delayInitDataFinish(){
            if(delayValueRead!=null)
                postData(delayValueRead);
        }

        Observable<RV> delayValueRead;
        @Override
        public void postData(Observable<RV> observable) {
            if(dataList==null||dataList.size()==0){
                delayValueRead = observable;
                return;
            }
            observable.subscribe(new SimpleObserver<RV>() {
                @Override
                public void onNext(RV value) {
                    for (int i = 0; i < dataList.size(); i++) {
                        if (dataList.get(i).second.equals(value)) {
                            selectedIndex = i;
                            if(viewProxy!=null&&viewProxy.get()!=null)
                                viewProxy.get().setContent(dataList.get(i).first);
                            break;
                        }
                    }
                }
            });
        }

        @Override
        public RV requestData() {
            return getSelectedValue();
        }

        public RV getSelectedValue() {
            if (dataList == null || dataList.size() == 0 || selectedIndex == -1) {
                return null;
            } else
                return dataList.get(selectedIndex).second;
        }

        public void setViewProxy(InputViewer<String> viewProxy) {
            this.viewProxy = new WeakReference<>(viewProxy);
        }
    }

}
