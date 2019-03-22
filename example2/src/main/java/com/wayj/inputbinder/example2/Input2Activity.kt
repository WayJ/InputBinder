package com.wayj.inputbinder.example2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.tianque.inputbinder.InputBinder
import com.tianque.inputbinder.convert.ItemTypeConvert
import com.tianque.inputbinder.function.ContainerFunc
import com.tianque.inputbinder.item.ButtonInputItem
import com.tianque.inputbinder.item.InputItem
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

        //初始化
        inputBinder = InputBinder.Build(this)
                .addTypeConvert(TeacherItemConvert())
                .addTypeConvert(object:ItemTypeConvert<Student,OptionalInputItem>(){
                    override fun setItemValue(item: OptionalInputItem?, value: Student?) {
//                        item.requestValue=value.name
                        //将putIn方法的入参object传递与此，对具体的控件进行赋值

                    }
                    //返回一个InputItem类型，可自己集成父类来扩展
                    override fun newInputItem(resId: Int, viewAttribute: ViewAttribute?): OptionalInputItem {
                        return OptionalInputItem(resId)
                    }
                })
                .bindBean(Student::class.java)
                .create()

        //添加or控制 一个特殊的输入项
        val buttonInputItem = ButtonInputItem(R.id.input_btn, "点一下试试看")
        buttonInputItem.setOnClickListener { Toast.makeText(this@Input2Activity, "点击了一下", Toast.LENGTH_SHORT).show() }

        inputBinder.addInputItem(buttonInputItem)
                .start()


        //模拟请求接口获得数据并显示
        doRequestAndShow()

        //取值并显示
        printTxt = findViewById(R.id.print)
        (findViewById<View>(R.id.button) as Button).setOnClickListener {
            printTxt.text = inputBinder.requestMap.toString().replace(",".toRegex(), ",\n")
        }

    }

    private fun doRequestAndShow() {
        //request
        //get data
        //转换成实体类

        val student = Student()
        student.address="sadas"
        student.roomNumber=21412
        student.teacher = Teacher(2,"zhang san")
        inputBinder.putIn(student)

//        inputBinder.putOut(object :ContainerFunc{
//            override fun onPutOut(map: MutableMap<String, String>?) {
//
//            }
//
//            override fun onVerifyFailed(inputItems: MutableList<InputItem<Any>>?) {
//
//            }
//        })

        inputBinder.updateView()

    }




}
