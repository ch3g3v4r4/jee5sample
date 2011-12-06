set ANDROID_HOME=d:\programs\android-sdk-windows\
cd %~dp0helloflashlight
call mvn com.jayway.maven.plugins.android.generation2:android-maven-plugin:emulator-start
%TEMP%\android-maven-plugin-emulator-start.vbs