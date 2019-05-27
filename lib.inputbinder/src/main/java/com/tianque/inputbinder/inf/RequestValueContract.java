package com.tianque.inputbinder.inf;

import io.reactivex.Observable;
import io.reactivex.Observer;


public abstract class RequestValueContract{
    public interface RequestValueObservable<In,Out> {
        Observable<Out> requestValue(In value);
    }


    /**
     *
     * @param <Out> 即RequestValueObservableFactory的第二个泛型
     */
    public interface RequestValueObserver<Out> {
        void onRequestValue(Observable<Out> observable);
    }
}