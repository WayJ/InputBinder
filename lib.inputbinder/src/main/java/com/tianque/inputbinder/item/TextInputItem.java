package com.tianque.inputbinder.item;

import com.tianque.inputbinder.inf.RequestValueContract;
import com.tianque.inputbinder.item.base.BaseTextDisplayInputItem;
import com.tianque.inputbinder.rxjava.SimpleObserver;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class TextInputItem extends BaseTextDisplayInputItem implements RequestValueContract.RequestValueObserver<String> {

    public TextInputItem(int resourceId) {
        super(resourceId);
    }

    public TextInputItem(int resourceId, String displayText) {
        super(resourceId, displayText);
    }

    @Override
    public void onRequestValue(Observable<String> observable) {
        observable
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
//                        if (isStarted)
//                            refreshView();
                    }
                })
                .subscribe(new SimpleObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        getViewProxy().setContent(s);
                    }
                });
    }




}
