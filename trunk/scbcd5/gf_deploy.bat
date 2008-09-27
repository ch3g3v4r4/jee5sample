@echo off
REM You must use Maven 2.0.8 or later to allow ERRORLEVEL is returned correctly

if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

set MAVEN_TERMINATE_CMD=on
cmd /C mvn.bat install
IF ERRORLEVEL 1 GOTO end

call F:/programs/glassfish/bin/asadmin.bat deploy --user=admin --passwordfile=gf_password scbcd5-ear/target/scbcd5-ear-1.0-SNAPSHOT.ear

:end

if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal