package com.wayj.inputbinder.example2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.tianque.inputbinder.InputBinder
import com.tianque.inputbinder.InputBinderEngine
import com.tianque.inputbinder.convert.ItemTypeConvert
import com.tianque.inputbinder.item.ButtonInputItem
import com.tianque.inputbinder.item.OptionalInputItem
import com.tianque.inputbinder.model.ViewAttribute


/**
 * Created by way on 2018/3/7.
 */

class Input2Activity : AppCompatActivity() {
    lateinit var printTxt: TextView
    lateinit var inputBinder: InputBinder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)


        val buttonInputItem = ButtonInputItem(R.id.input_btn, "点我一下，代码赋值")
        buttonInputItem.setOnClickListener { Toast.makeText(this@Input2Activity, "点击了一下", Toast.LENGTH_SHORT).show() }
        buttonInputItem.setDisplayText("点一下试试看")

        printTxt = findViewById(R.id.print)
        (findViewById<View>(R.id.button) as Button).setOnClickListener {
            printTxt.text = inputBinder.requestMap.toString().replace(",".toRegex(), ",\n")
        }
        inputBinder = InputBinder.Build(this)
                .addTypeConvert(TeacherItemConvert())
                .addTypeConvert(object:ItemTypeConvert<Student,OptionalInputItem>(){
                    override fun setItemValue(item: OptionalInputItem?, value: Student?) {
//                        item.requestValue=value.name
                    }

                    override fun newInputItem(resId: Int, viewAttribute: ViewAttribute?): OptionalInputItem {
                        return OptionalInputItem(resId)
                    }
                })
                .bindBean(Student::class.java)
                .create()
        inputBinder.engine.callBack = InputBinderEngine.CallBack { }
        inputBinder.addInputItem(buttonInputItem)
                .start()
//        inputBinder.engine.setAllViewEnable(false)
        //模拟请求接口获得数据并显示
        doRequestAndShow()

    }

    private fun doRequestAndShow() {
        //request
        //get data
        //转换成实体类

        var student = Student()
        student.address="sadas"
        student.teacher = Teacher(2,"zhang san")
        inputBinder.doPull(student)

        inputBinder.updateView()

        inputBinder.requestMap
    }




}
