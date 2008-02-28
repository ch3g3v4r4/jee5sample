package org.builder.eclipsebuilder.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class CheckstylePartBuilder extends PartBuilderHelper implements PartBuilder {

    protected static Logger logger = Logger.getLogger(CheckstylePartBuilder.class);

    protected List<String> filter(List<String> urlList, String artifactId, BuildType buildType) throws Exception {
        // com.atlassw.tools.eclipse.checkstyle_4.4.0-bin.zip
        // com.atlassw.tools.eclipse.checkstyle_4.4.0-updatesite.zip
        List<String> resultLinks = new ArrayList<String>();
        Pattern p1 = Pattern.compile("com\\.atlassw\\.tools\\.eclipse\\.checkstyle\\_\\d+(\\.\\d+)*-bin\\.zip");
        Pattern p2 = Pattern.compile("com\\.atlassw\\.tools\\.eclipse\\.checkstyle\\_\\d+(\\.\\d+)*-updatesite\\.zip");
        for (String url : urlList) {
            if (url.indexOf("mirror_picker.php") != -1) continue;
            if (p1.matcher(url).find() || p2.matcher(url).find()) {
                resultLinks.add(url);
            }
        }

        return resultLinks;
    }

}
