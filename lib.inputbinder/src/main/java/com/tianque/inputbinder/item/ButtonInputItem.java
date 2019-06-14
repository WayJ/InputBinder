package com.tianque.inputbinder.item;

import com.tianque.inputbinder.inf.RequestDataContract;
import com.tianque.inputbinder.item.base.BaseButtonInputItem;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ButtonInputItem extends BaseButtonInputItem implements RequestDataContract.RequestDataObserver<String> {


    public ButtonInputItem(int resourceId) {
        super(resourceId);
    }

    public ButtonInputItem(int resourceId, String displayText) {
        super(resourceId,displayText);
    }


    @Override
    public void postData(Observable<String> observable) {
        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                setDisplayText(s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
