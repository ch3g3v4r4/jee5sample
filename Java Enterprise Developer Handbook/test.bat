set PRJ_HOME=%~dp0
cd /D "%PRJ_HOME%"
call mvn clean pre-site
copy /Y target\site\reference\pdf\handbook.pdf "%TEMP%"
"%TEMP%\handbook.pdf"