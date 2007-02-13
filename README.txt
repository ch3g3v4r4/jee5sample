1. Build:

* Required: Maven 2, JDK 5+
Commands:
> cd sample
> mvn -N install
> mvn package install

The sample.ear file will be created at sample/sample-ear/target/

2. Deploy:

* Required: sample.ear, JEE5 server (JBoss 5/Sun JEE SDK/Glassfish...)
Configure a data source with JNDI name: jdbc/SampleDS
Copy sample.ear to deploy directory 

Note: For JBoss 5.0.0beta1, jta-data-source in persistence.xml in EAR file must be updated to java:/jdbc/SampleDS

Note: a sample data source file for JBoss is below:

---------------mysql-ds.xml--------------
<datasources>
  <local-tx-datasource>
    <jndi-name>jdbc/SampleDS</jndi-name>
    <connection-url>jdbc:mysql://localhost:3306/test</connection-url>
    <driver-class>com.mysql.jdbc.Driver</driver-class>
    <user-name>thai</user-name>
    <password>thai</password>
    <exception-sorter-class-name>org.jboss.resource.adapter.jdbc.vendor.MySQLExceptionSorter</exception-sorter-class-name>
    <metadata>
       <type-mapping>mySQL</type-mapping>
    </metadata>
  </local-tx-datasource>
</datasources>
------------------------------

3. Generate project files for IDEs (Eclipse, IDEA)
> cd sample
> mvn eclipse:eclipse
> mvn idea:idea
