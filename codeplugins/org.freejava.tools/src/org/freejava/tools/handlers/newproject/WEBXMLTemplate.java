package org.freejava.tools.handlers.newproject;

public class WEBXMLTemplate
{
  protected static String nl;
  public static synchronized WEBXMLTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    WEBXMLTemplate result = new WEBXMLTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  protected final String TEXT_2 = NL + "<web-app version=\"2.4\" xmlns=\"http://java.sun.com/xml/ns/j2ee\"" + NL + "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + NL + "        xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee" + NL + "        http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\">" + NL;
  protected final String TEXT_3 = NL + "    <context-param>" + NL + "        <param-name>contextConfigLocation</param-name>" + NL + "        <param-value>classpath:applicationContext.xml</param-value>" + NL + "    </context-param>";
  protected final String TEXT_4 = NL;
  protected final String TEXT_5 = NL + "    <listener>" + NL + "        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>" + NL + "    </listener>";
  protected final String TEXT_6 = NL + NL + "</web-app>";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
     NewProjectModel model = (NewProjectModel) argument; 
    stringBuffer.append(TEXT_2);
     if (model.isSpringSupport()) { 
    stringBuffer.append(TEXT_3);
     } 
    stringBuffer.append(TEXT_4);
     if (model.isSpringSupport()) { 
    stringBuffer.append(TEXT_5);
     } 
    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
