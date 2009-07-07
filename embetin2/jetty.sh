#!/bin/sh
MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=256m -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
mvn -Dappengine-local-runtime.scope=runtime -Dappengine-api-stubs.scope=runtime -Djava.util.logging.config.file=war/WEB-INF/logging.properties jetty:run