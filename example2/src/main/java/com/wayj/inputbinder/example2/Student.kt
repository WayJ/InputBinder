package com.wayj.inputbinder.example2

import com.tianque.inputbinder.inf.Input
import com.tianque.inputbinder.item.InputItemType
import com.tianque.inputbinder.item.OptionalInputItem

import java.util.Date

/**
 * Created by way on 2018/3/5.
 */
class Student {
    var studentId: Int = 0
    @Input(viewId = R.id.edit_name)
    var name: String? = null
    @Input
    var isBoy: Boolean = false
    @Input(type = InputItemType.Date)
    private var birthday: String? = null
    @Input
    var comeDay: Date? = null
    @Input
    var more: String? = null
    @Input(type = InputItemType.Optional, parm = "{'optionalKeys':['0.6','0.8','1.0']}", requestKey = "vision")
    var vision: String? = null//视力
    @Input(type = InputItemType.Optional, parm = "{'optionalKeys':['0.6','0.8','1.0'],'optionalValues':['6','8','10']}", requestKey = "student.vision2")
    var vision2: String? = null//视力

    @Input(type = InputItemType.MultiOptional, parm = "{'optionalKeys':['语文','数学','英语']}")
    var multi: String? = null


    /**
     * 通过checkbox控件的值来控制某些控件显示不显示 dependent 是正依赖，即该控件和关联控件的显示是一致的，要么都显示，要么都不显示，dependent_inversion则是翻回来只显示一个
     * 这里仅对view进行控制，没有对最后的数据进行处理
     */
    @Input(parm = "{'dependent':'roomNumber','dependent_inversion':'address'}")
    var hasRoom: Boolean = false

    @Input
    var roomNumber: Int? = null

    @Input
    var address: String? = null

    //@Input 对于复杂对象，这里暂时不错处理
    var master: Teacher? = null

    @Input(requestKey="master.id")
    var masterId: Int =0

    @Input(type = InputItemType.Extend, requestKey="teacher.id",parm = "{'dicName':'教师名字'}")
    var teacher:Teacher? = null



}
