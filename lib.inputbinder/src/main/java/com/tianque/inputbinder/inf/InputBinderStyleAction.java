package com.tianque.inputbinder.inf;

import com.tianque.inputbinder.item.DateInputItem;
import com.tianque.inputbinder.item.MultiOptionalInputItem;
import com.tianque.inputbinder.item.OptionalInputItem;

/**
 * Created by way on 2018/3/27.
 */

public class InputBinderStyleAction {

    OptionalInputItem.OptionalDialogAction optionalDialogAction;
    DateInputItem.DateDialogAction dateDialogAction;
    MultiOptionalInputItem.MultiOptionalDialogAction multiOptionalDialogAction;

    public OptionalInputItem.OptionalDialogAction getOptionalDialogAction() {
        return optionalDialogAction;
    }

    public InputBinderStyleAction setOptionalDialogAction(OptionalInputItem.OptionalDialogAction optionalDialogAction) {
        this.optionalDialogAction = optionalDialogAction;
        return this;
    }

    public DateInputItem.DateDialogAction getDateDialogAction() {
        return dateDialogAction;
    }

    public InputBinderStyleAction setDateDialogAction(DateInputItem.DateDialogAction dateDialogAction) {
        this.dateDialogAction = dateDialogAction;
        return this;
    }

    public MultiOptionalInputItem.MultiOptionalDialogAction getMultiOptionalDialogAction() {
        return multiOptionalDialogAction;
    }

    public InputBinderStyleAction setMultiOptionalDialogAction(MultiOptionalInputItem.MultiOptionalDialogAction multiOptionalDialogAction) {
        this.multiOptionalDialogAction = multiOptionalDialogAction;
        return this;
    }
}
