package org.freejava.tools.handlers.newproject;

public class POMXMLTemplate
{
  protected static String nl;
  public static synchronized POMXMLTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    POMXMLTemplate result = new POMXMLTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL;
  protected final String TEXT_2 = NL + NL + "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + NL + "    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">" + NL + "" + NL + "    <modelVersion>4.0.0</modelVersion>" + NL + "    <groupId>";
  protected final String TEXT_3 = "</groupId>" + NL + "    <artifactId>";
  protected final String TEXT_4 = "</artifactId>" + NL + "    <packaging>";
  protected final String TEXT_5 = "</packaging>" + NL + "    <version>1.0-SNAPSHOT</version>" + NL + "    <name>";
  protected final String TEXT_6 = "</name>" + NL + "" + NL + "    <properties>";
  protected final String TEXT_7 = NL + "        <spring.version>2.5.6</spring.version>";
  protected final String TEXT_8 = NL + "        <hibernate.version>3.3.1.GA</hibernate.version>";
  protected final String TEXT_9 = NL + "    </properties>" + NL + "" + NL + "    <dependencies>" + NL + "        <dependency>" + NL + "          <groupId>junit</groupId>" + NL + "          <artifactId>junit</artifactId>" + NL + "          <version>3.8.1</version>" + NL + "          <scope>test</scope>" + NL + "        </dependency>" + NL;
  protected final String TEXT_10 = NL + "        <!-- Servlet & JSP APIs -->" + NL + "        <dependency>" + NL + "            <groupId>javax.servlet</groupId>" + NL + "            <artifactId>servlet-api</artifactId>" + NL + "            <version>2.4</version>" + NL + "            <scope>provided</scope>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>javax.servlet</groupId>" + NL + "            <artifactId>jsp-api</artifactId>" + NL + "            <version>2.0</version>" + NL + "            <scope>provided</scope>" + NL + "        </dependency>" + NL;
  protected final String TEXT_11 = NL + NL + "        <!-- Spring framework 2.5.x -->" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-aop</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-aspects</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-beans</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-context</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-context-support</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-core</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-jdbc</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-orm</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-test</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "            <scope>test</scope>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-web</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework</groupId>" + NL + "            <artifactId>spring-webmvc</artifactId>" + NL + "            <version>${spring.version}</version>" + NL + "        </dependency>" + NL + "        <dependency>" + NL + "            <groupId>org.springframework.security</groupId>" + NL + "            <artifactId>spring-security-core-tiger</artifactId>" + NL + "            <version>2.0.2</version>" + NL + "            <exclusions>" + NL + "                <exclusion>" + NL + "                    <groupId>org.springframework</groupId>" + NL + "                    <artifactId>spring-support</artifactId>" + NL + "                </exclusion>" + NL + "            </exclusions>" + NL + "        </dependency>";
  protected final String TEXT_12 = NL + NL + "    </dependencies>" + NL + "" + NL + "    <build>" + NL + "        <finalName>";
  protected final String TEXT_13 = "</finalName>" + NL + "" + NL + "        <plugins>" + NL + "            <plugin>" + NL + "                <artifactId>maven-compiler-plugin</artifactId>" + NL + "                <configuration>" + NL + "                    <source>1.5</source>" + NL + "                    <target>1.5</target>" + NL + "                </configuration>" + NL + "            </plugin>" + NL;
  protected final String TEXT_14 = NL + "            <plugin>" + NL + "                <groupId>org.codehaus.mojo</groupId>" + NL + "                <artifactId>tomcat-maven-plugin</artifactId>" + NL + "                <version>1.0-alpha-1</version>" + NL + "            </plugin>" + NL + "            <plugin>" + NL + "                <groupId>org.mortbay.jetty</groupId>" + NL + "                <artifactId>maven-jetty-plugin</artifactId>" + NL + "                <version>6.0.2</version>" + NL + "            </plugin>";
  protected final String TEXT_15 = NL + NL + "            <plugin>" + NL + "                <groupId>org.apache.maven.plugins</groupId>" + NL + "                <artifactId>maven-eclipse-plugin</artifactId>" + NL + "                <version>2.6</version>" + NL + "                <configuration>" + NL + "                    <downloadSources>true</downloadSources>";
  protected final String TEXT_16 = NL + "                    <wtpversion>1.5</wtpversion>" + NL + "                    <wtpapplicationxml>true</wtpapplicationxml>";
  protected final String TEXT_17 = NL + "                    <additionalProjectnatures>";
  protected final String TEXT_18 = NL + "                        <projectnature>org.springframework.ide.eclipse.core.springnature</projectnature>";
  protected final String TEXT_19 = NL + "                        <projectnature>com.atlassw.tools.eclipse.checkstyle.CheckstyleNature</projectnature>";
  protected final String TEXT_20 = NL + "                        <projectnature>edu.umd.cs.findbugs.plugin.eclipse.findbugsNature</projectnature>";
  protected final String TEXT_21 = NL + "                        <projectnature>org.hibernate.eclipse.console.hibernateNature</projectnature>";
  protected final String TEXT_22 = NL + "                    </additionalProjectnatures>" + NL + "                    <additionalBuildcommands>";
  protected final String TEXT_23 = NL + "                        <buildcommand>org.springframework.ide.eclipse.core.springbuilder</buildcommand>";
  protected final String TEXT_24 = NL + "                        <buildcommand>com.atlassw.tools.eclipse.checkstyle.CheckstyleBuilder</buildcommand>";
  protected final String TEXT_25 = NL + "                        <buildcommand>edu.umd.cs.findbugs.plugin.eclipse.findbugsBuilder</buildcommand>";
  protected final String TEXT_26 = NL + "                        <buildcommand>org.hibernate.eclipse.console.hibernateBuilder</buildcommand>";
  protected final String TEXT_27 = NL + "                    </additionalBuildcommands>" + NL + "                </configuration>" + NL + "            </plugin>" + NL + "        </plugins>" + NL + "    </build>" + NL + "\t<reporting>" + NL + "\t\t<plugins>" + NL + "\t\t    <!-- Running unit tests and generate coverage report -->" + NL + "\t\t\t<plugin>" + NL + "\t\t\t\t<artifactId>maven-jxr-plugin</artifactId>" + NL + "\t\t\t</plugin>" + NL + "\t\t\t<plugin>" + NL + "\t\t\t\t<artifactId>maven-surefire-report-plugin</artifactId>" + NL + "\t\t\t</plugin>" + NL + "\t\t\t<plugin>" + NL + "\t\t\t\t<groupId>org.codehaus.mojo</groupId>" + NL + "\t\t\t\t<artifactId>cobertura-maven-plugin</artifactId>" + NL + "\t\t\t\t<version>2.2</version>" + NL + "\t\t\t</plugin>";
  protected final String TEXT_28 = NL + "\t\t\t<!-- Generating FindBugs report -->" + NL + "\t\t\t<plugin>" + NL + "\t\t\t\t<groupId>org.codehaus.mojo</groupId>" + NL + "\t\t\t\t<artifactId>findbugs-maven-plugin</artifactId>" + NL + "\t\t\t\t<version>2.1</version>" + NL + "\t\t\t\t<configuration>" + NL + "\t\t\t\t  <xmlOutput>true</xmlOutput>" + NL + "\t\t\t\t  <threshold>Low</threshold>" + NL + "\t\t\t\t  <effort>Max</effort>" + NL + "\t\t\t\t  <excludeFilterFile>findbugs-exclude.xml</excludeFilterFile>" + NL + "\t\t\t\t</configuration>" + NL + "\t\t\t</plugin>";
  protected final String TEXT_29 = NL + "\t\t\t<plugin>" + NL + "\t\t\t\t<groupId>org.apache.maven.plugins</groupId>" + NL + "\t\t\t\t<artifactId>maven-checkstyle-plugin</artifactId>" + NL + "\t\t\t\t<configuration>" + NL + "\t\t\t\t\t<configLocation>src/main/config/checkstyle/checkstyle.xml</configLocation>" + NL + "\t\t\t\t</configuration>" + NL + "\t\t\t</plugin>";
  protected final String TEXT_30 = NL + "\t\t</plugins>" + NL + "\t</reporting>" + NL + "</project>";
  protected final String TEXT_31 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     NewProjectModel model = (NewProjectModel) argument; 
    stringBuffer.append(TEXT_2);
    stringBuffer.append(model.getGroupId());
    stringBuffer.append(TEXT_3);
    stringBuffer.append(model.getArtifactId());
    stringBuffer.append(TEXT_4);
    stringBuffer.append(model.isWebProject()?"war":"jar");
    stringBuffer.append(TEXT_5);
    stringBuffer.append(model.getArtifactId());
    stringBuffer.append(TEXT_6);
     if (model.isSpringSupport()) {
    stringBuffer.append(TEXT_7);
     } 
     if (model.isHibernateSupport()) {
    stringBuffer.append(TEXT_8);
     } 
    stringBuffer.append(TEXT_9);
     if (model.isWebProject()) {
    stringBuffer.append(TEXT_10);
     } 
     if (model.isSpringSupport()) {
    stringBuffer.append(TEXT_11);
     } 
    stringBuffer.append(TEXT_12);
    stringBuffer.append(model.getArtifactId());
    stringBuffer.append(TEXT_13);
     if (model.isWebProject()) {
    stringBuffer.append(TEXT_14);
     } 
    stringBuffer.append(TEXT_15);
     if (model.isWebProject()) {
    stringBuffer.append(TEXT_16);
     } 
    stringBuffer.append(TEXT_17);
     if (model.isSpringSupport()) {
    stringBuffer.append(TEXT_18);
     } 
     if (model.isCheckstyleSupport()) {
    stringBuffer.append(TEXT_19);
     } 
     if (model.isFindBugsSupport()) {
    stringBuffer.append(TEXT_20);
     } 
     if (model.isHibernateSupport()) {
    stringBuffer.append(TEXT_21);
     } 
    stringBuffer.append(TEXT_22);
     if (model.isSpringSupport()) {
    stringBuffer.append(TEXT_23);
     } 
     if (model.isCheckstyleSupport()) {
    stringBuffer.append(TEXT_24);
     } 
     if (model.isFindBugsSupport()) {
    stringBuffer.append(TEXT_25);
     } 
     if (model.isHibernateSupport()) {
    stringBuffer.append(TEXT_26);
     } 
    stringBuffer.append(TEXT_27);
     if (model.isFindBugsSupport()) {
    stringBuffer.append(TEXT_28);
     } 
     if (model.isCheckstyleSupport()) {
    stringBuffer.append(TEXT_29);
     } 
    stringBuffer.append(TEXT_30);
    stringBuffer.append(TEXT_31);
    return stringBuffer.toString();
  }
}
