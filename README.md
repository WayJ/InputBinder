# InputBinder
一个能够快速开发form表单页面的工具，自带了基础样式，快速搭建复杂且数目较多的form表单页面。

在实例类的Field上添加@Input注解，即将该项输入项纳入控制。该库会处理后续的存取数据、设置操作等各种功能。

- 配置输入控件的类型、请求参数名、视图操作类型（button、checkbox）
- 配置方式：注解 @input
- 设置输入项的数据:
  - 将实体类的实例的数据设置到输入项上，并进行绑定操作，绑定后，输入项上的用户输入会自定赋值到绑定的对象
  - 将Map<String,String>的数据设置到输入项上
- 获得用户输入的数据，将用户编辑好的输入控件上的值取出，
  - 返回一个Map<String,String>：key为请求参数名，value为具体的参数值
  - 返回一个实体类的实例
- 有基本输入控件类型支持：文本输入、时间选择、打勾选中、多选一、多选多等基本类型
- 有自带基础样式支持，且可扩展：inputbinder-style。样式与核心代码分离，可修改扩展
- 支持自定义的输入控件：例如身份证输入、图片选择、音频录入等

---------

### Import

 [ ![Download](https://api.bintray.com/packages/wayj/maven/inputbinder/images/download.svg) ](https://bintray.com/wayj/maven/inputbinder/_latestVersion)  

```
    api 'com.wayj.inputbinder:inputbinder:+'
    api 'com.wayj.inputbinder:inputbinder-style:+'
    
    //私有maven    
    api 'com.tianque.android:inputbinder:+'
    api 'com.tianque.android:inputbinder-style:+'
```

* inputbinder：核心代码
* inputbinder-style：样式代码，可随意修改

#### 支持的form表单的input类型

* 文本输入框
* 按钮
* 日期选择
* 勾选框
* 单选框
* 多选框
* 支持自行扩展

---------

### 使用

仅说明实体类注解的配置方式，xml配置可见example。

![](https://github.com/WayJ/InputBinder/blob/master/docs/demo.gif)

#### 注解

将实体类的变量与输入表单上的控件关联起来，用@Input 注解标记上，对应的[Layout文件](https://github.com/WayJ/InputBinder/blob/master/example2/src/main/res/layout/input_xml_activity.xml)

```kotlin
class Student {
	/////基本
    @Input
    var name: String? = null
    @Input 
    var address: String? = null
    @Input
    var isBoy: Boolean = false
    ////////// 时间
    @Input(type = InputItemType.Date)
    private var birthday: String? = null
    ////////// 
    @Input(type = InputItemType.Optional, parm = "{'optionalKeys':['0.6','0.8','1.0']}", requestKey = "vision")
    var vision: String? = null//视力
    @Input(type = InputItemType.Optional, parm = "{'optionalKeys':['0.6','0.8','1.0'],'optionalValues':['6','8','10']}", requestKey = "student.vision2")
    var vision2: String? = null//视力
    @Input(type = InputItemType.MultiOptional, parm = "{'optionalKeys':['语文','数学','英语']}")
    var multi: String? = null
    ////////checkbox
    /**
     * 通过checkbox控件的值来控制某些控件显示不显示 dependent 是正依赖，即该控件和关联控件的显示是一致的，要么都显示，要么都不显示，dependent_inversion则是翻回来只显示一个
     * 这里仅对view进行控制，没有对最后的数据进行处理
     */
    @Input(parm = "{'dependent':'roomNumber','dependent_inversion':'address'}")
    var hasRoom: Boolean = false
    @Input
    var roomNumber: String? = null
    @Input
    var address: String? = null
    ////////自定义
    @Input(type = InputItemType.Extend, requestKey="teacher.id",parm = "{'dicName':'教师名字'}")
    var teacher:Teacher? = null

}
```

* type = InputItemType.XXX：表示这个输入控件的类型，可不设置，如果不设置，默认等于该变量的类型（String，Date，Boolean） 
  * InputItemType.Extend：自定义扩展类型，如果标记这个类型，将会讲一些处理过程交由ItemTypeConvert来做，需要开发者自己继承ItemTypeConvert类来实现功能
* requestKey：发送请求时的输入项请求参数名，默认与变量名相同，可配置
* viewName：关联View的依据，默认与变量名相同，可配置
* required: 是否必填，可通过validateRequestParams 方法来验证必填项是否全部填写
* parm：不同输入项类型有不同的参数配置，必须是JSON类型字符串
  * Optional：单选项（多选一）
    * optionalKeys：选项显示的文字
    * optionalValues：发送请求时的value，如果不设置的话默认等同于optionalKeys
  * CheckBox：勾选框
    * dependent：正依赖，即如果打勾则显示、否则就隐藏
    * dependent_inversion：反向依赖，即如果打勾则不显示，不打勾就隐藏
  * 自定义
    * item初始化时，可通过ViewAttribute.parm 获得配置的参数
    * item初始化后，可通过getConfig(String)来获得配置



#### InputBinder使用

具体例子见[这里](https://github.com/WayJ/InputBinder/blob/master/example2/src/main/java/com/wayj/inputbinder/example2/Input2Activity.kt)

```kotlin
inputBinder = InputBinder.Build(this)
     .addTypeConvert(TeacherItemConvert())
     .addTypeConvert(object:ItemTypeConvert<Student,OptionalInputItem>(){
        override fun setItemValue(item: OptionalInputItem?, value: Student?) {

                    }

        override fun newInputItem(resId: Int, viewAttribute: ViewAttribute?): OptionalInputItem {
               return OptionalInputItem(resId)
            }
       })
      .bindBean(Student::class.java)
      .create()

inputBinder.addInputItem(buttonInputItem)
           .start()
```

导入数据（查看详情、修改等时候用到）

```kotlin
//模拟数据
var student = Student()
student.address="sadas"
student.teacher = Teacher(2,"zhang san")
//导入
inputBinder.putIn(student)
```

导出数据

```java
inputBinder.getRequestMap()
```

刷新视图

```
inputBinder.updateView()
```

设置不可编辑不可点击

```
inputBinder.getEngine().setAllViewEnable(false)
```

自定义类型示例：类型转换器 将对应的复杂类型的输入项的数据转换成对应的InputItem

```kotlin
class TeacherItemConvert : ItemTypeConvert<Teacher, OptionalInputItem>() {
    override fun setItemValue(item: OptionalInputItem?, value: Teacher?) {
        item!!.requestValue = value!!.id.toString()
    }

    override fun newInputItem(resId:Int,viewAttribute: ViewAttribute?): OptionalInputItem {
        var teacherItem = OptionalInputItem(resId)
        teacherItem.optionalTexts=arrayOf("How", "Are", "You")
        teacherItem.optionalValues= arrayOf("1","2","3")
        return teacherItem
    }
}
```

自己创建InputItem添加入控制链中

```
inputBinder.addInputItem(buttonInputItem)
```

放入数据

```
val student = Student()
student.address="sadas"
student.roomNumber=21412
student.teacher = Teacher(2,"zhang san")
inputBinder.putIn(student)
```

取出数据，会先进行数据合格校验，验证必填项、手机号码、身份证号码是否合格等

```
inputBinder.putOut(object :ContainerFunc{
	//数据合格校验成功的回调
    override fun onPutOut(map: MutableMap<String, String>?) {

    }
	//数据合格校验失败的回调
    override fun onVerifyFailed(inputItems: MutableList<InputItem<Any>>?) {

    }
})
```

找到某个InputItem进行操作

```
InputBinder.findInputByViewId(int viewId)
InputBinder.findInputByViewName(String viewName)
```



#### POJO配置读取说明

会读取标记了@input的成员变量，且会读取父类的配置

可以读取成员变量的类型中配置的@Input，要使用InputItemType.Recursion（递归读取）

```java
public class Boy extends People{
    @Input
    public String name;
	@Input(type=InputItemType.Recursion)
	public Father father;
}

public class Father extends People{
    @Input
    public String name;
    @Input
    public String remark;
}

public class People{
    @Input
    public String birthday;
    @Input(type = InputItemType.Optional)
    public String sexType;
}
```

上面的代码通过  ``inputBinder.readProfile(new Boy())``  后

得到以下输入项配置，即 7个输入项配置- 
| Profile Name 名字，默认的viewId           | 输入项类型     |
| --------------- | ----------------- |
| name            | TextInputItem     |
| birthday        | TextInputItem     |
| sexType         | OptionalInputItem |
| father.name     | TextInputItem     |
| father.remark   | TextInputItem     |
| father.birthday | TextInputItem     |
| father.sexType  | OptionalInputItem |

然后会查询Layout文件中View，如果没有找到对应的输入项视图，会直接删去该 Profile。



#### 数据绑定说明

在执行绑定代码后，默认会进行单项数据绑定

```java
Boy boy=new Boy();

inputBinder.readProfile(Boy.class);
inputBinder.start();
inputBinder.bind(boy);

//如果用户在 boy的name输入项 填入了数据 ("李磊")，则 boy.name会自动修改
print(boy.getName());   // ->  李磊

//也可以这样,将全部的输入数据赋值到 object
Boy boyNew=new Boy();
inputBinder.putOut(boyNew);
print(boy.getName());  //  ->  李磊
```









#### TodoList

* 完善数据校验功能

  * （已支持）校验支持逻辑运算-与，譬如   手机号码，不为空（必填）且手机号码格式正确

  * ```
    //kotlin
    verify= Input.Verify_NotNull and Input.Verify_Mobile
    //java 
    verify= Input.Verify_NotNull & Input.Verify_Mobile
    ```

  * 

    * 但是不支持逻辑运算-或，譬如 身份证件，或为15位老身份证或为18位老身份证

