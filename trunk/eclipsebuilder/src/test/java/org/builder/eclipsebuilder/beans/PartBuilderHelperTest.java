package org.builder.eclipsebuilder.beans;

import java.io.File;

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

    public void testDownload() throws Exception {
        String url;
        String artifactId;
        BuildType buildType;
        File cacheFolder = new File("/cache");
        File targetFolder = new File("/eclipse");

        url = "http://download.eclipse.org/eclipse/downloads/";
        artifactId = "eclipse-SDK";
        buildType = BuildType.STABLE;
        builder.download(url, artifactId, buildType, cacheFolder, targetFolder);

        url = "http://download.eclipse.org/webtools/downloads/";
        artifactId = "wtp-sdk";
        buildType = BuildType.STABLE;
        builder.download(url, artifactId, buildType, cacheFolder, targetFolder);

        url = "http://www.eclipse.org/modeling/emf/downloads/";
        artifactId = "emf-sdo-xsd-SDK";
        buildType = BuildType.STABLE;
        builder.download(url, artifactId, buildType, cacheFolder, targetFolder);

        url = "http://www.eclipse.org/tptp/home/downloads/downloads.php";
        artifactId = "tptp.sdk";
        buildType = BuildType.STABLE;
        builder.download(url, artifactId, buildType, cacheFolder, targetFolder);

    }

}
