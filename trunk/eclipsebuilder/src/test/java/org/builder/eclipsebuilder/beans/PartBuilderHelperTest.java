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

        String url = "http://download.eclipse.org/eclipse/downloads/";
        String artifactId = "eclipse-SDK";
        BuildType buildType = BuildType.STABLE;
        File cacheFolder = new File("/cache");
        File targetFolder = new File("/eclipse");
        builder.download(url, artifactId, buildType, cacheFolder, targetFolder);
    }

}
