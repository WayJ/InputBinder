package com.tianque.inputbinder.inf;

public interface EditViewAbility {
    void addTextChangedListener(OnTextChangedListener onTextChangedListener);

    /**
     *
     * @param type  0:任意；1：数字
     */
    void setEditType(int type);

    interface OnTextChangedListener{
        void onTextChanged(String text);
    }


}
