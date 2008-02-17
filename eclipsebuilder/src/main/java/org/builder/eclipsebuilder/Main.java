package org.builder.eclipsebuilder;

import java.io.File;
import java.net.URL;

import org.builder.eclipsebuilder.beans.DownloadManager;
import org.builder.eclipsebuilder.beans.EclipseBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Main
{
    public static void main( String[] args ) throws Exception
    {
        ApplicationContext  ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
        DownloadManager mgr = (DownloadManager) ctx.getBean("downloadManager");
        mgr.setFolder(new File("C:\\"));
        mgr.setUrl(new URL("http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip&url=http://download.eclipse.org/eclipse/downloads/drops/S-3.4M5-200802071530/eclipse-SDK-3.4M5-win32.zip&mirror_id=1"));
        Thread t = new Thread(mgr);
        t.start();
        t.join();

        EclipseBuilder builder = (EclipseBuilder) ctx.getBean("eclipseBuilder");
        builder.build();
    }
}
