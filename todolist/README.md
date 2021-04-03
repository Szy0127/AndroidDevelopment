# ToDoList  

## 使用方法与主要功能  

1. MainActivity
 - 点击加号进入NoteActivity(新建模式)  
 - 长按Note进入NoteActivity(修改模式)  
 - Note 按时间排序
 - Menu设置颜色进入ColorActivity
 - Menu设置背景进入BackgroundActivity

2. NoteActivity
 - 默认priority为Normal 截止日期为当天
 - 点击选择日期可展示DatePicker 并即时更新TextView 此时按钮变为确认 点击后隐藏DataPicker
 - 点击选择日期的同时隐藏键盘
 - 标题随模式切换
 - 修改模式下 成功提交后会删除原先的note
 - 修改模式下 返回时会询问是否保存(若不满足以下条件则直接返回且不保存修改)
    - content不为空
    - 至少修改了一个属性

3. ColorActivity
  - ColorPickerView选择颜色
  - 提交后修改三个Priority对应的颜色并存储至SharedPreference中
  
4. BackgroundActivity
  - 点击图片直接退出并更换背景 同时存储至SharedPreference中