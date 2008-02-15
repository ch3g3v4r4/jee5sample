package org.builder.eclipsebuilder;

import org.builder.eclipsebuilder.beans.EclipseBuilderConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args )
    {
        ApplicationContext  ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
        EclipseBuilderConfiguration config = (EclipseBuilderConfiguration) ctx.getBean("EclipseBuilderConfiguration");
        System.out.println( "Hello World!" );
    }
}
