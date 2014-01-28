@ECHO OFF
ECHO HELLO
MKDIR %~dp0downloads

bitsadmin.exe /transfer "Download wget" "http://users.ugent.be/~bpuype/wget/wget.exe" "%TEMP%\wget.exe"
COPY /Y "%TEMP%\wget.exe" "%~dp0downloads\wget.exe"
SET PATH=%PATH%;%~dp0downloads
SET DOWNLOADS=%~dp0downloads

rem Notepad++
wget -nc -P "%DOWNLOADS%" http://download.tuxfamily.org/notepadplus/6.5.3/npp.6.5.3.Installer.exe 

rem Groovy/Grails Tool Suite Downloads based on Eclipse 3.8 http://spring.io/tools/ggts/all
wget -nc -P "%DOWNLOADS%" http://download.springsource.com/release/STS/3.4.0/dist/e3.8/groovy-grails-tool-suite-3.4.0.RELEASE-e3.8.2-win32-x86_64.zip