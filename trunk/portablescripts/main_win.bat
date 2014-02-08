@ECHO OFF
MKDIR %~dp0downloads
SET PATH=%PATH%;%~dp0downloads
SET DOWNLOADS=%~dp0downloads

rem wget
IF EXIST "%DOWNLOADS%\wget.exe" goto wgetexists
bitsadmin.exe /transfer "Download wget" "http://users.ugent.be/~bpuype/wget/wget.exe" "%TEMP%\wget.exe"
COPY /Y "%TEMP%\wget.exe" "%~dp0downloads\wget.exe"
:wgetexists

rem Notepad++
wget -nc -P "%DOWNLOADS%" http://download.tuxfamily.org/notepadplus/6.5.3/npp.6.5.3.Installer.exe

rem Groovy/Grails Tool Suite (GGTS 3.4.0.RELEASE) based on Eclipse 3.8 http://spring.io/tools/ggts/all
wget -nc -P "%DOWNLOADS%" http://download.springsource.com/release/STS/3.4.0/dist/e3.8/groovy-grails-tool-suite-3.4.0.RELEASE-e3.8.2-win32-x86_64.zip
rem spring tool suite
wget -nc -P "%DOWNLOADS%" http://download.springsource.com/release/STS/3.4.0/dist/e3.8/spring-tool-suite-3.4.0.RELEASE-e3.8.2-win32-x86_64.zip

rem JDK7
IF EXIST "%DOWNLOADS%\jdk-7u51-windows-x64.exe" goto jdk7exists
PUSHD "%DOWNLOADS%"
wget -nc -O jdk-7u51-windows-x64.exe --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F" "http://download.oracle.com/otn-pub/java/jdk/7u51-b13/jdk-7u51-windows-x64.exe"
POPD
:jdk7exists



rem eclipsedropinsbuilder
PUSHD "%DOWNLOADS%"
IF EXIST "%DOWNLOADS%\eclipsedropinsbuilder" goto eclipsedropinsbuilderexists
svn co https://jee5sample.googlecode.com/svn/trunk/eclipsedropinsbuilder/
goto eclipsedropinsbuilderdone
:eclipsedropinsbuilderexists
svn up eclipsedropinsbuilder
:eclipsedropinsbuilderdone
POPD

rem Maven
wget -nc -P "%DOWNLOADS%" http://www.us.apache.org/dist/maven/maven-3/3.1.1/binaries/apache-maven-3.1.1-bin.zip

rem Gradle
wget -nc -P "%DOWNLOADS%" http://services.gradle.org/distributions/gradle-1.11-rc-1-bin.zip


