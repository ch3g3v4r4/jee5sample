call mvn install:install-file "-Dfile=%~dp0artifacts\atomikos-util.jar" -DgroupId=com.atomikos -DartifactId=atomikos-util -Dversion=3.3.0 -Dpackaging=jar -DgeneratePom=true

call mvn install:install-file "-Dfile=%~dp0artifacts\transactions.jar" -DgroupId=com.atomikos -DartifactId=transactions -Dversion=3.3.0 -Dpackaging=jar -DgeneratePom=true

call mvn install:install-file "-Dfile=%~dp0artifacts\transactions-api.jar" -DgroupId=com.atomikos -DartifactId=transactions-api -Dversion=3.3.0 -Dpackaging=jar -DgeneratePom=true

call mvn install:install-file "-Dfile=%~dp0artifacts\transactions-hibernate3.jar" -DgroupId=com.atomikos -DartifactId=transactions-hibernate3 -Dversion=3.3.0 -Dpackaging=jar -DgeneratePom=true

call mvn install:install-file "-Dfile=%~dp0artifacts\transactions-jdbc.jar" -DgroupId=com.atomikos -DartifactId=transactions-jdbc -Dversion=3.3.0 -Dpackaging=jar -DgeneratePom=true

call mvn install:install-file "-Dfile=%~dp0artifacts\transactions-jta.jar" -DgroupId=com.atomikos -DartifactId=transactions-jta -Dversion=3.3.0 -Dpackaging=jar -DgeneratePom=true

