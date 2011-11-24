set ANDROID_HOME=D:\programs\android-sdk-windows
set PATH=%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools;%PATH%
call mvn clean install com.jayway.maven.plugins.android.generation2:android-maven-plugin:deploy com.jayway.maven.plugins.android.generation2:android-maven-plugin:run
