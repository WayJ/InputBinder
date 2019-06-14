package com.tianque.inputbinder.item;

import com.tianque.inputbinder.function.InputObserve;
import com.tianque.inputbinder.inf.EditViewAbility;
import com.tianque.inputbinder.inf.RequestDataContract;
import com.tianque.inputbinder.item.base.BaseTextDisplayInputItem;
import com.tianque.inputbinder.rxjava.SimpleObserver;

import io.reactivex.Observable;
import io.reactivex.functions.Action;

public class TextInputItem extends BaseTextDisplayInputItem implements RequestDataContract.RequestDataObserver<String> ,RequestDataContract.RequestDataObservable<String>{
    private int editType;//0，任意;1: 数字、电话号码等

    public TextInputItem(int resourceId) {
        super(resourceId);
    }

    public TextInputItem(int resourceId, String displayText) {
        super(resourceId, displayText);
    }

    @Override
    public void postData(Observable<String> observable) {
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


    @Override
    public void onStart() {
        super.onStart();
        if(getView()!=null&& getView() instanceof EditViewAbility){
            EditViewAbility editViewAbility=(EditViewAbility)getView();
            if(editType!=0)
                editViewAbility.setEditType(editType);
            editViewAbility.addTextChangedListener(new EditViewAbility.OnTextChangedListener() {
                @Override
                public void onTextChanged(String text) {
                    dataObserve.onNext(text);
                }
            });
        }
    }

    InputObserve<String> dataObserve;
    @Override
    public void observe(InputObserve<String> observe) {
        this.dataObserve =observe;
    }


    public int getEditType() {
        return editType;
    }

    public void setEditType(int editType) {
        this.editType = editType;
    }
}
