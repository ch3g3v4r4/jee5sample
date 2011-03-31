package org.freejava.tools.handlers.newproject;

public class HIBERNATEPREFSTemplate
{
  protected static String nl;
  public static synchronized HIBERNATEPREFSTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    HIBERNATEPREFSTemplate result = new HIBERNATEPREFSTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "#Sat Aug 23 08:42:58 ICT 2008" + NL + "default.configuration=" + NL + "eclipse.preferences.version=1" + NL + "hibernate3.enabled=true";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    return stringBuffer.toString();
  }
}
