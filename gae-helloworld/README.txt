Testing
1. Start using "mvn clean appengine:devserver" command
2. Access to http://localhost:8080
3. Use eclipse to connect to Java remote debugging port: 8000 of localhost

Deploy this app to Google App Engine:

1. Registering a new application at http://appengine.google.com/ and remember the app-id
2. Replace all references to 'helloworld' by <app-id> in appengine-web.xml and pom.xml files
3. Use command "mvn appengine:update" to upload the application to Google App Engine server
4. Test by browsing to http://<app-id>.appspot.com
5. See https://developers.google.com/appengine/docs/java/tools/maven for more GAE commands

Developing with Eclipse:

1. Use command "mvn eclipse:eclipse" to create Eclipse project files
3. Import project to Eclipse using 'File/Import/Import Existing projects to Workspace')
4. Add classpath variable M2_REPO to point to <your home>/.m2/repository
