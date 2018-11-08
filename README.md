# HJMirror ([ENGLISH](README_EN.md))

## 项目介绍?

HJMirror是一个用于将手机投屏至PC的Java项目，所以在使用前请先确认您已安装Java8环境。

### 这个项目代码主要有两部分构成

1. Droid模块包含了一个执行在安卓设备上的插件，无需手动安装，可以由Server模块自动加载安装到目标设备上

2. Server模块由Swing开发（不太好，但只熟悉这个，感兴趣的可以帮忙改下，考虑用Mono或Qt），可以自动下载配置ADB，并帮助用户安装并启动插件到目标设备之上。


## 怎么构建项目？

1. 拉取项目分支比如2.0.0的代码到本地

2. 确认您已经安装了JDK、AndroidSDK与Android Studio.

3. 使用 Android Studio打开项目，但注意不要提升Gradle 的版本。

4. 执行Droid模块下的'/build/assemble'Gradle任务 (不知道的自行百度Studio怎么执行Gradle任务)，如果一切正常的话，该任务会在"/droid/build/outputs/apk/"中创建APK安装文件。

5. 将创建的APK文件拷贝覆盖至 "/server/src/main/resources/" 目录中，并确保名称为"HJMirror.apk"，该APK将会在Server打包时被一起打入。

6. 执行Server模块下的'/other/release'Gradle任务， 该任务会在 "/server/build/libs/"中创建名为 HJMirrorxxxxx.jar 的可执行jar包。

7. 使用命令 "java -jar HJMirrorxxxxx.jar" 执行该 jar 包。

8. 最后，根据 APP 中的信息操作即可。


## v2.0.0更新内容

1. 自动从Google Repo拉取ADB工具到 Jar 包所在目录，无需用户再安装。

2. 多语言支持，至少框架上是完成了。

3. 优化了了加载插件到目标设备的速度，现在启动投屏快多了。

4. 大改了下代码，至少比之前好看了点。。。

## 下一步计划

正在使用Go语言+QT重写服务端代码
