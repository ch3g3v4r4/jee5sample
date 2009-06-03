README

1. Setup development environment

1.1 Install Eclipse IDE and plugins
  - Install Eclipse from www.eclipse.com
  - Install WTP plugins from Update Site http://download.eclipse.org/webtools/updates/

1.2 Generate Eclipse project:
  > cd <project directory>
  > mvn eclipse:eclipse

1.3 Import project to Eclipse

1.4 In Eclipse, create M2_REPO classpath variable to point to Maven local repository

Note: See screencast at http://winkjava.110mb.com/new_project/newmavenproject.htm

2. Build

  > mvn package