:<<"::CMDLITERAL"
@ECHO OFF
GOTO :CMDSCRIPT
::CMDLITERAL

if [ "$(uname)" == "Darwin" ]; then
  export MAVEN_OPTS=-XstartOnFirstThread
fi
/bin/sh `which mvn` -f "$(dirname $0)/pom.xml" "-Dsource=$(dirname $0)/src/main/groovy/hello.groovy" "-Dscriptdir=$(dirname $0)" groovy:execute
exit $?

:CMDSCRIPT
SET SCRIPT_DIR=%~dp0
REM Remove trailing slash from SCRIPT_DIR
SET SCRIPT_DIR=%SCRIPT_DIR:~0,-1%
CALL mvn -f "%~dp0pom.xml" "-Dsource=%~dp0src/main/groovy/hello.groovy" "-Dscriptdir=%SCRIPT_DIR%" groovy:execute
