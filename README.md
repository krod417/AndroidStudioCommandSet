# AndroidStudioCommandSet
   androidStudio插件集合
# 功能列表
## postfix快捷命令
```
    1.log快捷命令：
        a).原生LogAPI快捷命令：
            字符串: "测试".log---->Log.d(TAG, "测试");
            字符串实例：
                String info = new String();
                info.e.log-->Log.e(TAG, info);
            特殊对象：
                A a = new A();
                a.i.log-->Log.i(TAG, "a" + a);
        b).自定义LogAPI快捷命令：
            需要在Preferences-->OtherSettings->AndroidCodeGenerator->CLog Template 修改成自定义api调用函数模型
            源生模版为：Log.$method$($TAG$, $expr$);$END$
            下面是我自定义的模版：
            AppLog.e("");则模版填写：AppLog.$method$($expr$);$END$
            调用方式跟原生一样，命令为clog。
     2.toast快捷命令：
        a).原生ToastAPI快捷命令：
            字符串: "测试".toast---->Toast.makeText(this, "测试", Toast.LENGTH_SHORT);
            字符串实例：
                String info = new String();
                info.l.toast-->Toast.makeText(this, info, Toast.LENGTH_LONG);
            特殊对象：
                A a = new A();
                info.l.toast-->Toast.makeText(this, "a" + a, Toast.LENGTH_LONG);
        b).自定义LogAPI快捷命令：
            需要在Preferences-->OtherSettings->AndroidCodeGenerator->CToast Template 修改成自定义api调用函数模型
            源生模版为：Toast.makeText($context$, $expr$, $dur$).show();$END$
            下面是我自定义的模版：
            ToastUtil.show("");则模版填写：ToastUtil.show($expr$);$END$
            调用方式跟原生一样，命令为ctoast。
    3.isemp／isnemp快捷命令:判断字符串是否为空
        实例：String info = new String(); 
            info.isemp-->if (TextUtils.isEmpty(info)) {}
            info.isnemp-->if (!TextUtils.isEmpty(info)) {}
    4.sv快捷命令:
        实例：View v;
            v.sv-->v.setVisibility(View.VISIBLE);
            v.g.sv-->v.setVisibility(View.GONE);
    5.find快捷命令：前提是View的变量名跟id名一样
        实例：
            TextView tv;
            tv.find-->tv = (TextView)findViewById(R.id.tv);
            支持自定义find方法，会优先查找自定义find方法如果不存在才使用系统的findViewById
            需要在Preferences-->OtherSettings->AndroidCodeGenerator->FindId Template 修改成自定义api调用函数模型
            我定义的模版：
                findbyId:tv.find-->tv = findById(R.id.tv);
```
## search String/Extract resource快捷菜单
```
    支持查询string／color／dimen 
    通过弹窗添加string／color／dimen属性
```
## 自动检索布局中的控件生成findviewbyid代码和成员变量
# License

Copyright 2015 krod

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
