# dotolist  

## 使用方法与主要功能  

1. MainActivity
 - 点击加号进入NoteActivity(新建模式)  
 - 长按Note进入NoteActivity(修改模式)  
 - Note 按时间排序

2. NoteActivity
 - 默认priority为Normal 截止日期为当天
 - 点击选择日期可展示DatePicker 并即时更新TextView 此时按钮变为确认 点击后隐藏DataPicker
 - 标题随模式切换
 - 修改模式下 成功提交后会删除原先的note
 - 修改模式下 返回时会询问是否保存