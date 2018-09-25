# HJMirror


## 如何构建

1. 请先安装Java环境、Android SDK与Android Studio，并确保网络畅通！

2. 导入项目，并运行droid/assemble任务，构建APK文件

3. 将生成的APK文件拷贝入server/src/main/conf目录中，并确保名称为：HJMirror.apk

4. 执行server/release任务，进行server打包，会在build/libs下生成目标jar文件

5. 使用java -jar 目标jar文件 来执行程序，然后按程序指导操作


## 如何使用

1. 请先确保您的机器上有ADB程序并已经安装了要投屏的手机的ADB驱动

2. 将您的手机开启ADB调试，并开启第三方APK安装

3. 将您的手机通过USB连接到PC上

4. 启动目标jar文件，然后在文件输入框中指定ADB程序所在路径

5. 点击开始连接，进行设备查询，如果上述过程正确，则应当会看到您的手机设备

6. 双加设备进行连接

7. 接下去程序会自动安装插件APK到您的手机，并启动获取设备信息

8. 然后会关闭插件APK，开启截图进程并传递图像至PC端的服务程序

9. 如果第一次无法获取图像，可能是设备较慢导致，请重试一次

## 改善意见

本程序还有较多问题，涵盖性能、代码、使用不便等等等等，目前依然没有什么实用价值。

About Language, this code write in Chinese, and need translate which this aurthor really got no idea.

