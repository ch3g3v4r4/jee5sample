1. Download and install Google App Engine SDK for Java http://code.google.com/appengine/downloads.html
2. Install 'Google Plugin for Eclipse' to your Eclipse IDE (update site: http://dl.google.com/eclipse/plugin/3.4)
3. Registering the application at http://appengine.google.com/ and remember the app-id 
4. Replace all references to 'embetin2' in the application by your app-id (step 3 above)
5. Use command 
mvn package eclipse:eclipse
to create Eclipse project files
6.1 Import project to Eclipse using 'File/Import/Import Existing projects to Workspace')
6.2 Add classpath variable M2_REPO to point to <your home>/.m2/repository
7. Use command
appengine-java-sdk-1.2.0\bin\appcfg update <your-app>/target/<app-id>
to upload the application to Google App Engine server
8. Test by browsing to http://<app-id>.appspot.com