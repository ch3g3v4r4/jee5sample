package org.freejava.tools.handlers.newproject;

public class APPLICATIONCONTEXTXMLTemplate
{
  protected static String nl;
  public static synchronized APPLICATIONCONTEXTXMLTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    APPLICATIONCONTEXTXMLTemplate result = new APPLICATIONCONTEXTXMLTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
  protected final String TEXT_2 = NL + "<beans xmlns=\"http://www.springframework.org/schema/beans\"" + NL + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + NL + "    xmlns:p=\"http://www.springframework.org/schema/p\"" + NL + "    xmlns:aop=\"http://www.springframework.org/schema/aop\"" + NL + "    xmlns:context=\"http://www.springframework.org/schema/context\"" + NL + "    xmlns:jee=\"http://www.springframework.org/schema/jee\"" + NL + "    xmlns:tx=\"http://www.springframework.org/schema/tx\"" + NL + "    xmlns:util=\"http://www.springframework.org/schema/util\"" + NL + "    xsi:schemaLocation=\"" + NL + "            http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-2.5.xsd" + NL + "            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd" + NL + "            http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-2.5.xsd" + NL + "            http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee-2.5.xsd" + NL + "            http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd" + NL + "            http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-2.5.xsd\">" + NL + "" + NL + "" + NL + "</beans>";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    return stringBuffer.toString();
  }
}
