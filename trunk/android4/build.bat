cd %~dp0android-maven-plugin
call mvn install
cd %~dp0platform-native
call mvn install
cd %~dp0helloflashlight
call mvn clean install