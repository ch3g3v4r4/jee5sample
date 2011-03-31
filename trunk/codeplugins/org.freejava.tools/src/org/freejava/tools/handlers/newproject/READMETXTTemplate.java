package org.freejava.tools.handlers.newproject;

public class READMETXTTemplate
{
  protected static String nl;
  public static synchronized READMETXTTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    READMETXTTemplate result = new READMETXTTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "README";
  protected final String TEXT_2 = NL + NL + "1. Setup development environment" + NL + "" + NL + "1.1 Install Eclipse IDE and plugins" + NL + "  - Install Eclipse from www.eclipse.com";
  protected final String TEXT_3 = NL + "  - Install WTP plugins from Update Site http://download.eclipse.org/webtools/updates/";
  protected final String TEXT_4 = NL + "  - Install SpringIDE plugins from Update Site http://springide.org/updatesite";
  protected final String TEXT_5 = NL + "  - Install Checkstyle plugin from Update Site http://eclipse-cs.sourceforge.net/update";
  protected final String TEXT_6 = NL + "  - Install FindBugs plugin from Update Site http://findbugs.cs.umd.edu/eclipse/";
  protected final String TEXT_7 = NL + "  - Install Hibernate plugins from Update Site http://download.jboss.org/jbosstools/updates/development/";
  protected final String TEXT_8 = NL + NL + "1.2 Generate Eclipse project:" + NL + "  > cd <project directory>" + NL + "  > mvn eclipse:eclipse" + NL + "" + NL + "1.3 Import project to Eclipse" + NL + "" + NL + "1.4 In Eclipse, create M2_REPO classpath variable to point to Maven local repository" + NL + "" + NL + "Note: See screencast at http://winkjava.110mb.com/new_project/newmavenproject.htm" + NL + "" + NL + "2. Build" + NL + "" + NL + "  > mvn package";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     NewProjectModel model = (NewProjectModel) argument; 
    stringBuffer.append(TEXT_2);
     if (model.isWebProject()) {
    stringBuffer.append(TEXT_3);
     } 
     if (model.isSpringSupport()) {
    stringBuffer.append(TEXT_4);
     } 
     if (model.isCheckstyleSupport()) {
    stringBuffer.append(TEXT_5);
     } 
     if (model.isFindBugsSupport()) {
    stringBuffer.append(TEXT_6);
     } 
     if (model.isHibernateSupport()) {
    stringBuffer.append(TEXT_7);
     } 
    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}
