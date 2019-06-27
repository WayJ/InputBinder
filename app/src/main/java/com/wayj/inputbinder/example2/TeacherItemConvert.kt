package com.wayj.inputbinder.example2

import com.tianque.inputbinder.convert.ItemTypeConvert
import com.tianque.inputbinder.convert.impl.OptionalInputItemConvert
import com.tianque.inputbinder.item.OptionalInputItem

class TeacherItemConvert : OptionalInputItemConvert<Teacher>() {
    override fun initOptionalData(optionalInputItem: OptionalInputItem?): OptionalInputItem.OptionalData<Teacher> {
        val data = OptionalInputItem.OptionalData<Teacher>();
        data.add("张老师",Teacher(1,"张三"))
        data.add("李老师", Teacher(2,"李四"))
        data.add("王五",Teacher(3,"王五"))
        return data
    }

}