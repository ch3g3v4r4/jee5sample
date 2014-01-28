@ECHO OFF
ECHO HELLO
MKDIR %~dp0downloads

bitsadmin.exe /transfer "Download wget" "http://users.ugent.be/~bpuype/wget/wget.exe" "%TEMP%\wget.exe"
COPY /Y "%TEMP%\wget.exe" "%~dp0downloads\wget.exe"
SET PATH=%PATH%;%~dp0downloads
SET DOWNLOADS=%~dp0downloads

wget -nc -P "%DOWNLOADS%" http://download.tuxfamily.org/notepadplus/6.5.3/npp.6.5.3.Installer.exe 
