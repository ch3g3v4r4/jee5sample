call mvn package
call mvn dependency:copy-dependencies
mkdir run
copy target\*.jar target\dependency\

java -jar target\dependency\rfp-analysis-1.0-SNAPSHOT.jar