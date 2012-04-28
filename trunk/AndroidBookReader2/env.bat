if "%ANDROID_HOME%"=="" goto setDefaultAndroidHome
goto done
:setDefaultAndroidHome
set ANDROID_HOME=d:\programs\android_sdk
:done