set ANDROID_HOME=D:\programs\android-sdk-windows
set PATH=%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools;%PATH%
call mvn clean install android:deploy android:run
