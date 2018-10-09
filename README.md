# HJMirror

## What is that?

HJMirror is an Java and Droid project to help users mirror their Droid Device to PC Screen, it was written in java and depends on ADB Service, So before you use it, you need to install the Java Environment and ADB Service first. You can get the Java Environment on Oracle website and ADB Service by installing the Android SDK.

### This project combines two parts, Droid plugin and PC Server：

1. Droid plugin will read some device info to tell the PC Server how to display, and have a Java class which will been started by ADB to snapshoot this device and send to PC Server.

2. PC Server was written with Swings (It's not the best but the only way I can do, maybe someone can give me some help.) and based on ddmlibs, it can check devices connection by USB on this PC, install the Droid plugin to Device, start to get snapshoots, and display them on the screen.


## How to build this project?

1. Pull project sources from branch like 1.0.0.

2. Please make sure you have already installed JDK, Android SDK and Android Studio.

3. Open this project by using Android Studio and don't update anything like gradle-plugin or gradle.

4. Running a gradle task named '/build/assemble' of droid module. It will build an APK file in "./droid/build/outputs/apk/", if everything is ok.

5. Copy this APK file to "./server/src/main/conf/", and make sure it named "HJMirror.apk".

6. Running a gradle task named '/other/release' of server module. It will build an Running Jar file in "./server/build/libs/", and it will copy the apk file in config to this folder either.

7. Running this java file by using command "java -jar HJMirrorxxxxx.jar".

8. Operate this JavaApp by follow the infomation.


## Improvement

1. This project has already fast but not enough, it can reach 21fps on my Macbook and Nexus6p, but the CPU using-rate is pretty high, so it still need improvement on performance.

2. It's still pretty difficult on using, you have to install the JVM, ADB Service and Droid Device Driver at first, it's not good. So I think maybe I can build my own ADB service based on Android source code, and recode this PC Server by using some other techs like Golang, SDL or Qt. But it still need Windows user to install Driver at first, and I have no idea on it, sad.

3. I wrote this code in one afternoon, so the quality of this code is terrible, it will need some time and some help to improve it.

