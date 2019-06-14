package com.tianque.inputbinder.inf;

import com.tianque.inputbinder.function.InputObserve;

import io.reactivex.Observable;


public abstract class RequestDataContract {
    /**
     * 从实体类中读取field变量的数据给inputItem，进行中间的转换过程
     * @param <In> field变量的数据，一般是object
     * @param <Out> inputItem持有的数据，一般和具体的InputItem对应
     */
    public interface IObjectDataConvert<In,Out> {
        /**
         * 返回一个数据设置的被观察者，使用rxjava的原因是 观察者可能延时去设置数据，例如数据字典项可能要先查询到自己的数据（http）才去设置当前选中哪一个
         * @param value
         * @return
         */
        Observable<Out> requestConvertValueFromObject(In value);

        In requestObjectValue(Out out);
    }


    /**
     *
     * @param <Out> 即RequestValueObservableFactory的第二个泛型
     */
    public interface RequestDataObserver<Out> {
        void postData(Observable<Out> observable);
    }


    /**
     * 参考IObjectValueConvert 的功能，争对特殊的InputItem使用，即转换器可能不满足requestObjectValue的使用的时候，例如数据字典项
     * @param <Out>
     */
    public interface getObjectValueFromInput<Out>{
        Out requestData();
    }


    /**
     * 参考databinding的数据绑定，让输入项上修改值时候，自动修改绑定的实体类上的变量
     * @param <Out>
     */
    public interface RequestDataObservable<Out>{
        void observe(InputObserve<Out> observe);
    }
}