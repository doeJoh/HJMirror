# HJMirror ([中文说明](README.md))

## What is that?

HJMirror is an Java and Droid project to help users mirror their Droid Device to PC Screen, it was written in java and depends on ADB Service, So before you use it, you need to install the Java Environment at first.

### This project combines two parts, Droid plugin and PC Server：

1. Droid plugin will read some device info to tell the PC Server how to display, and have a Java class which will been started by ADB to snapshoot this device and send to PC Server.

2. PC Server was written with Swings (It's not the best but the only way I can do, maybe someone can give me some help.) and based on ddmlibs, it can setup adb tools and check devices connection by USB on this PC, install the Droid plugin to Device, start to get snapshoots, and display them on the screen.


## How to build this project?

1. Pull project sources from branch like 2.0.0.

2. Please make sure you have already installed JDK, Android SDK and Android Studio.

3. Open this project by using Android Studio and don't update anything like gradle-plugin or gradle.

4. Running a gradle task named '/build/assemble' of droid module. It will build an APK file in "./droid/build/outputs/apk/", if everything is ok.

5. Copy this APK file to "./server/src/main/resources/", and make sure it named "HJMirror.apk".

6. Running a gradle task named '/other/release' of server module. It will build an executable Jar file in "./server/build/libs/".

7. Running this java file by using command "java -jar HJMirrorxxxxx.jar".

8. Operate this JavaApp by following the infomation on it.


## What's news on v2.0.0

1. Auto fetch and setup the adb tools from Google.

2. Multi-language support, on framework at least.

3. Much faster on plugin launching.

4. Code improvement, looks better than v1.0.0 at least...
