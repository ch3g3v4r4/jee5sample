cd %~dp0support\android-maven-plugin
call mvn install
cd %~dp0native\hello
call mvn install
cd %~dp0
call mvn clean install