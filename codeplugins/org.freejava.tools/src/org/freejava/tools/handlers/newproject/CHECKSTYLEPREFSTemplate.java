package org.freejava.tools.handlers.newproject;

public class CHECKSTYLEPREFSTemplate
{
  protected static String nl;
  public static synchronized CHECKSTYLEPREFSTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    CHECKSTYLEPREFSTemplate result = new CHECKSTYLEPREFSTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<fileset-config file-format-version=\"1.2.0\" simple-config=\"false\">" + NL + "    <local-check-config name=\"checkstyle\" location=\"src/main/config/checkstyle/checkstyle.xml\" type=\"project\" description=\"\">" + NL + "        <additional-data name=\"protect-config-file\" value=\"true\"/>" + NL + "    </local-check-config>" + NL + "    <fileset name=\"Checkstyle File Set\" enabled=\"true\" check-config-name=\"checkstyle\" local=\"true\">" + NL + "        <file-match-pattern match-pattern=\"^src[\\\\/]main[\\\\/]java[\\\\/]\" include-pattern=\"true\"/>" + NL + "    </fileset>" + NL + "    <filter name=\"FilesFromPackage\" enabled=\"true\">" + NL + "        <filter-data value=\"src/test/java\"/>" + NL + "        <filter-data value=\"target\"/>" + NL + "    </filter>" + NL + "</fileset-config>";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    return stringBuffer.toString();
  }
}
