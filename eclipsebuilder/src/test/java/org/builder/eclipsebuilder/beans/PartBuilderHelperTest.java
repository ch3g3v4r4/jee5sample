package org.builder.eclipsebuilder.beans;

import junit.framework.TestCase;

import org.builder.eclipsebuilder.beans.Configuration.BuildType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PartBuilderHelperTest extends TestCase {

    private PartBuilderHelper builder;

    protected void setUp() throws Exception {
        ApplicationContext  ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
        builder = new PartBuilderHelper();
        builder.setDownloadManager((DownloadManager) ctx.getBean("downloadManager"));
        builder.setWebBrowser( (WebBrowser) ctx.getBean("webBrowser"));
        super.setUp();
    }


    public void testGetDownloadAndChecksumLinks() throws Exception {
        String[] links;
        String url;
        String artifactId;
        BuildType buildType;
/*
        url = "http://download.eclipse.org/eclipse/downloads/";
        artifactId = "eclipse-SDK";
        buildType = BuildType.STABLE;
        links = builder.getDownloadAndChecksumLinks(url, artifactId, buildType);
        assertEquals("http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip&url=http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip&mirror_id=1", links[0]);
        assertEquals("http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/checksum/eclipse-SDK-3.4M5-win32.zip.md5", links[1]);

        url = "http://download.eclipse.org/webtools/downloads/";
        artifactId = "wtp-sdk";
        buildType = BuildType.STABLE;
        links = builder.getDownloadAndChecksumLinks(url, artifactId, buildType);
        assertEquals("http://www.eclipse.org/downloads/download.php?file=/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/wtp-sdk-S-3.0M5-20080218021547.zip&url=http://download.eclipse.org/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/wtp-sdk-S-3.0M5-20080218021547.zip&mirror_id=1", links[0]);
        assertEquals("http://download.eclipse.org/webtools/downloads/drops/R3.0/S-3.0M5-20080218021547/checksum/wtp-sdk-S-3.0M5-20080218021547.zip.md5", links[1]);

        url = "http://www.eclipse.org/modeling/emf/downloads/";
        artifactId = "emf-sdo-xsd-SDK";
        buildType = BuildType.STABLE;
        links = builder.getDownloadAndChecksumLinks(url, artifactId, buildType);
        assertEquals("http://www.eclipse.org/downloads/download.php?file=/modeling/emf/emf/downloads/drops/2.4.0/S200802090050/emf-sdo-xsd-SDK-2.4.0M5.zip&url=http://download.eclipse.org/modeling/emf/emf/downloads/drops/2.4.0/S200802090050/emf-sdo-xsd-SDK-2.4.0M5.zip&mirror_id=1", links[0]);
        assertEquals("http://download.eclipse.org/modeling/emf/emf/downloads/drops/2.4.0/S200802090050/emf-sdo-xsd-SDK-2.4.0M5.zip.md5", links[1]);

        url = "http://www.eclipse.org/tptp/home/downloads/";
        artifactId = "tptp.sdk-TPTP";
        buildType = BuildType.STABLE;
        links = builder.getDownloadAndChecksumLinks(url, artifactId, buildType);
        assertEquals("http://www.eclipse.org/downloads/download.php?file=/tptp/4.4.0.3/TPTP-4.4.0.3/tptp.sdk-TPTP-4.4.0.3.zip&url=http://download.eclipse.org/tptp/4.4.0.3/TPTP-4.4.0.3/tptp.sdk-TPTP-4.4.0.3.zip&mirror_id=1", links[0]);
        assertNull(links[1]);
*/
        url = "http://www.eclipse.org/gef/downloads/";
        artifactId = "GEF-ALL";
        buildType = BuildType.STABLE;
        links = builder.getDownloadAndChecksumLinks(url, artifactId, buildType);
        assertEquals("http://www.eclipse.org/downloads/download.php?file=/tools/gef/downloads/drops/3.4.0/S200802062130/GEF-ALL-3.4.0M5.zip&url=http://download.eclipse.org/tools/gef/downloads/drops/3.4.0/S200802062130/GEF-ALL-3.4.0M5.zip&mirror_id=1", links[0]);
        assertEquals("http://download.eclipse.org/tools/gef/downloads/drops/3.4.0/S200802062130/GEF-ALL-3.4.0M5.zip.md5", links[1]);

        url = "http://www.eclipse.org/datatools/downloads.php";
        artifactId = "dtp-sdk";
        buildType = BuildType.STABLE;
        links = builder.getDownloadAndChecksumLinks(url, artifactId, buildType);
        assertEquals("http://www.eclipse.org/downloads/download.php?file=/datatools/downloads/1.6/dtp-sdk_1.6M5.zip&url=http://download.eclipse.org/datatools/downloads/1.6/dtp-sdk_1.6M5.zip&mirror_id=1", links[0]);
        assertNull(links[1]);

    }

}
