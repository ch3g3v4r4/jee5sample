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

3. Generate project files for Eclipse (get nightly build of WTP 2.0)
a.
> cd sample
> mvn eclipse:eclipse
b.
Search and replace in sample-ear\.settings\:
<installed facet="jst.ear" version="1.3"/>
by 
<installed facet="jst.ear" version="5.0"/>
c.
Search and replace in sample-ejb\.settings\:
<faceted-project>
  <fixed facet="jst.java"/>
  <fixed facet="jst.ejb"/>
  <installed facet="jst.ejb" version="2.1"/>
  <installed facet="jst.java" version="5.0"/>
</faceted-project>
by
<faceted-project>
  <fixed facet="jst.ejb"/>
  <fixed facet="jst.java"/>
  <installed facet="jst.jpa" version="1.0"/>
  <installed facet="jst.java" version="5.0"/>
  <installed facet="jst.ejb" version="3.0"/>
</faceted-project>
d. Search and replace in sample-web\.settings\
<faceted-project>
  <fixed facet="jst.java"/>
  <fixed facet="jst.web"/>
  <installed facet="jst.web" version="2.4"/>
  <installed facet="jst.java" version="5.0"/>
</faceted-project>
by
<faceted-project>
  <fixed facet="jst.java"/>
  <fixed facet="jst.web"/>
  <installed facet="jst.jsf" version="1.2"/>
  <installed facet="jst.web" version="2.4"/>
  <installed facet="jst.java" version="5.0"/>
</faceted-project>
e. Update sample-web\.settings\org.eclipse.wst.common.component to add 2 dependency modules (JSF libs)
f. Add sample-web\.metadata from a JSF 1.2 project
