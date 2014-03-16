:; /bin/sh "$(dirname $0)/hello"; exit $?

@echo off

set SCRIPT_DIR=%~dp0
rem Remove trailing slash from SCRIPT_DIR
set SCRIPT_DIR=%SCRIPT_DIR:~0,-1%

call mvn -f "%~dp0pom.xml" "-Dsource=%~dp0src/main/groovy/hello.groovy" "-Dscriptdir=%SCRIPT_DIR%" groovy:execute
