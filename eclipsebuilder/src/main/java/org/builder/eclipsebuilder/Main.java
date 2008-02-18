package org.builder.eclipsebuilder;

import org.builder.eclipsebuilder.beans.EclipseBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Main
{
    public static void main( String[] args ) throws Exception
    {
        ApplicationContext  ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
        EclipseBuilder builder = (EclipseBuilder) ctx.getBean("eclipseBuilder");
        builder.build();
    }
}
