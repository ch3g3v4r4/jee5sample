package org.freejava.tools.handlers.newproject;

public class SPRINGBEANSXMLTemplate
{
  protected static String nl;
  public static synchronized SPRINGBEANSXMLTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    SPRINGBEANSXMLTemplate result = new SPRINGBEANSXMLTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  protected final String TEXT_2 = NL + "<beansProjectDescription>" + NL + "    <version>1</version>" + NL + "    <configSuffixes>" + NL + "        <configSuffix>xml</configSuffix>" + NL + "    </configSuffixes>" + NL + "    <enableImports>false</enableImports>" + NL + "    <configs>" + NL + "        <config>src/main/resources/applicationContext.xml</config>" + NL + "    </configs>" + NL + "    <configSets>" + NL + "        <configSet>" + NL + "            <name>Spring Config Set</name>" + NL + "            <allowBeanDefinitionOverriding>true</allowBeanDefinitionOverriding>" + NL + "            <incomplete>false</incomplete>" + NL + "            <configs>" + NL + "                <config>src/main/resources/applicationContext.xml</config>" + NL + "            </configs>" + NL + "        </configSet>" + NL + "    </configSets>" + NL + "</beansProjectDescription>";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    return stringBuffer.toString();
  }
}
