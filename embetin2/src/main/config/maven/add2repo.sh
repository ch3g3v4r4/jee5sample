cd `dirname $0`
SCRIPTDIR=`pwd`
cd -
mvn install:install-file "-Dfile=$SCRIPTDIR/artifacts/datanucleus-appengine-1.0.2.RC1.jar" -DgroupId=com.google.appengine.orm -DartifactId=datanucleus-appengine -Dversion=1.0.2.RC1 -Dpackaging=jar -DgeneratePom=true