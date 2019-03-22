package com.wayj.inputbinder.example2

import com.tianque.inputbinder.convert.ItemTypeConvert
import com.tianque.inputbinder.item.OptionalInputItem
import com.tianque.inputbinder.model.ViewAttribute

class TeacherItemConvert : ItemTypeConvert<Teacher, OptionalInputItem>() {
    override fun setItemValue(item: OptionalInputItem?, teacher: Teacher?) {
        item!!.requestValue = teacher!!.id.toString()
    }

    override fun newInputItem(resId: Int, viewAttribute: ViewAttribute?): OptionalInputItem {
        var teacherItem = OptionalInputItem(resId)
        teacherItem.optionalTexts = arrayOf("How", "Are", "You")
        teacherItem.optionalValues = arrayOf("1", "2", "3")

        //dd
        //dd = getDD
        //
        teacherItem.optionalTexts = arrayOf("男", "女")
        teacherItem.optionalValues = arrayOf("1", "2")
        return teacherItem
    }
}