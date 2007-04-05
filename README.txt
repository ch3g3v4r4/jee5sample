1. Build:

* Required: Maven 2, JDK 5+
Commands:
> cd sample
> mvn -N -U install
> mvn -U package install

The sample.ear file will be created at sample/sample-ear/target/

2. Deploy:

* Required: sample.ear, JEE5 server (JBoss 5/Sun JEE SDK/Glassfish...)
Configure a data source with JNDI name: jdbc/SampleDS
Copy sample.ear to deploy directory (or using command mvn install from sample/ directory - remember to update sample/profiles.xml)

Note: For JBoss 5.0.0beta1, jta-data-source in persistence.xml in EAR file must be updated to java:/jdbc/SampleDS (a bug of JBoss?)

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
d. Search and replace in sample-war\.settings\
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
  <installed facet="jst.web" version="2.5"/>
  <installed facet="jst.java" version="5.0"/>
</faceted-project>

e. Update sample-war\.settings\org.eclipse.wst.common.component to add 2 dependency modules (JSF libs). Below is a sample

<project-modules id="moduleCoreId" project-version="1.5.0">
  <wb-module deploy-name="sample-war">
    <wb-resource deploy-path="/" source-path="/src/main/webapp"/>
    <wb-resource deploy-path="/WEB-INF/classes" source-path="/src/main/java"/>
    <wb-resource deploy-path="/WEB-INF/classes" source-path="/src/main/resources"/>
    <dependent-module deploy-path="/WEB-INF/lib" handle="module:/classpath/lib/C:/Documents and Settings/Thai Ha/.m2/repository/javax/faces/jsf-api/1.2_03/jsf-api-1.2_03.jar">
	<dependency-type>uses</dependency-type>
    </dependent-module>
    <dependent-module deploy-path="/WEB-INF/lib" handle="module:/classpath/lib/C:/Documents and Settings/Thai Ha/.m2/repository/javax/faces/jsf-impl/1.2_03/jsf-impl-1.2_03.jar">
	<dependency-type>uses</dependency-type>
    </dependent-module>
    <dependent-module deploy-path="/WEB-INF/lib" handle="module:/classpath/lib/C:/Documents and Settings/Thai Ha/.m2/repository/jstl/jstl/1.2/jstl-1.2.jar">
	<dependency-type>uses</dependency-type>
    </dependent-module>
    <property name="context-root" value="sample-war"/>
    <property name="java-output-path" value="target/classes"/>
  </wb-module>
</project-modules>

If in J2EE perspective, the Deployment Descriptor doesn't show up, remove sample-war project then import it into workspace again. And also try to configure the Web App Libraries entries (just click OK)

f. Configure Maven command (mvn install) as External Program Builder for Auto Builds

4. Use NetBeans IDE:
Install Mevenide2-Netbeans plugin (http://mevenide.codehaus.org/m2-site/index.html) to NetBeans IDE and open the project directly from NetBeans.
