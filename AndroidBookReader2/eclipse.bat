call mvn eclipse:eclipse
echo REMOVE JRE FROM CLASSPATH.
ren .classpath .classpath.bak
findstr /v JRE_CONTAINER .classpath.bak > .classpath
del .classpath.bak