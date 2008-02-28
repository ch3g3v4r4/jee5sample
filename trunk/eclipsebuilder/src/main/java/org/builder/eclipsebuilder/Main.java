package org.builder.eclipsebuilder;

import org.apache.log4j.Logger;
import org.builder.eclipsebuilder.beans.EclipseBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Main {

    private static Logger logger = Logger.getLogger(Main.class);

    public static void main( String[] args ) throws Exception
    {
        ApplicationContext  ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
        EclipseBuilder builder = (EclipseBuilder) ctx.getBean("eclipseBuilder");
        logger.info("Building Eclipse IDE.");
        builder.build();
        logger.info("Please also install JSEclipse from http://labs.adobe.com/technologies/jseclipse/, account: hathanhthai@yahoo.com/thaiha");
        
    }
}
