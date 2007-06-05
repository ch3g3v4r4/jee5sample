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

