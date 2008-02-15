call mvn package
call mvn dependency:copy-dependencies
copy target\*.jar target\dependency\

java -jar target\dependency\eclipsebuilder-1.0-SNAPSHOT.jar