call mvn package
call mvn dependency:copy-dependencies
copy target\*.jar target\dependency\

java -jar target\dependency\server-1.0-SNAPSHOT.jar