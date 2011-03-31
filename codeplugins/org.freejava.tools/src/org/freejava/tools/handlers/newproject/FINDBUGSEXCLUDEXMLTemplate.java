package org.freejava.tools.handlers.newproject;

public class FINDBUGSEXCLUDEXMLTemplate
{
  protected static String nl;
  public static synchronized FINDBUGSEXCLUDEXMLTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    FINDBUGSEXCLUDEXMLTemplate result = new FINDBUGSEXCLUDEXMLTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<FindBugsFilter>" + NL + "  <Match>" + NL + "    <Class name=\"~.*Test$\"/>" + NL + "  </Match>" + NL + "  <Match>" + NL + "    <Package name=\"~test\\..*\"/>" + NL + "  </Match>" + NL + "" + NL + " <!-- always use below Matches -->" + NL + " <Match>" + NL + "   <Bug code=\"Nm,Se,EI,EI2,MS,SIC,IS,SBSC,REC,SnVI\" />" + NL + " </Match>" + NL + " <Match>" + NL + "   <Bug pattern=\"DM_CONVERT_CASE,DB_DUPLICATE_SWITCH_CLAUSES,PZLA_PREFER_ZERO_LENGTH_ARRAYS,SF_SWITCH_NO_DEFAULT\" />" + NL + " </Match>" + NL + "" + NL + "" + NL + " <!-- use below Match ONLY if your project using Java version < 1.5 -->" + NL + " <!--" + NL + " <Match>" + NL + "   <Bug pattern=\"DM_NUMBER_CTOR\" />" + NL + " </Match>" + NL + " -->" + NL + "" + NL + " <!-- use below Match ONLY if your project is VERY urgent -->" + NL + " <!--" + NL + " <Match>" + NL + "   <Bug category=\"SECURITY,BAD_PRACTICE,STYLE,PERFORMANCE,MALICIOUS_CODE,MT_CORRECTNESS,I18N,EXPERIMENTAL\" />" + NL + " </Match>" + NL + " -->" + NL + "" + NL + "" + NL + "</FindBugsFilter>";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    return stringBuffer.toString();
  }
}
