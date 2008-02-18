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
        mgr.setUrl(new URL("http://www.eclipse.org/downloads/download.php?file=/eclipse/downloads/drops/R-3.3.1.1-200710231652/eclipse-CVS-Client-SDK-3.3.1.1.zip&url=http://download.eclipse.org/eclipse/downloads/drops/R-3.3.1.1-200710231652/eclipse-CVS-Client-SDK-3.3.1.1.zip&mirror_id=1"));
        Thread t = new Thread(mgr);
        long t1 = System.currentTimeMillis();
        t.start();
        t.join();
        long t2 = System.currentTimeMillis();
        System.out.println("time:" + (t2-t1)/1000);
        EclipseBuilder builder = (EclipseBuilder) ctx.getBean("eclipseBuilder");
        //builder.build();
    }
}
