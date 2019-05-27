package com.tianque.inputbinder.rxjava;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class SimpleObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
