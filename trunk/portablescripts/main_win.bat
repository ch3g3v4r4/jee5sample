@ECHO OFF
ECHO HELLO
MKDIR %~dp0downloads

bitsadmin.exe /transfer "Download wget" "http://users.ugent.be/~bpuype/wget/wget.exe" %~dp0downloads\wget.exe
SET PATH=%PATH%;%~dp0downloads

wget -P "%~dp0downloads" http://download.tuxfamily.org/notepadplus/6.5.3/npp.6.5.3.Installer.exe 
