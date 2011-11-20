cd support\android-maven-plugin
call mvn install
cd %~dp0
set ANDROID_HOME=d:\programs\android-sdk-windows\
call mvn android:emulator-start
%TEMP%\android-maven-plugin-emulator-start.vbs