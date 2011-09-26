package org.freejava.seus;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.spi.LoggingEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.sonyericsson.cs.logging.log4jExtension.LogEventObject;

@Aspect
public class MyAspect {

	@Around("execution(private void com.sonyericsson.cs.logging.log4jExtension.g.g(..))")
	public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("BEFORE EXECUTE JOINTPOINT");
		Object retVal = pjp.proceed();
		return retVal;
	}

	@Around("execution(public void org.apache.log4j.helpers.BoundedFIFO.put(..))")
	public Object doPut(ProceedingJoinPoint pjp) throws Throwable {
		LoggingEvent evt =  (LoggingEvent) pjp.getArgs()[0];
		LogEventObject aEvent = (LogEventObject) evt.getMessage();
		String s = "";
		for (int  i  = 0; i < aEvent.getArgs().length; i++) {
			s += aEvent.getArgs()[i];
		}

		String log = (evt.getLevel().toString() + "["+ evt.getThreadName()+"]"+ "[" + evt.getLoggerName() +"]:" + s);
		FileWriter writer = new FileWriter(new File("C:\\sesu.log"), true);
		writer.write(log + "\n");
		writer.flush();
		writer.close();
		System.out.println(log);

		Object retVal = pjp.proceed();
		return retVal;
	}
	@Around("execution(public void org.slf4j.impl.CsLoggerAdapter.*(..))")
	public Object doLog(ProceedingJoinPoint pjp) throws Throwable {
		if (pjp.getArgs().length > 0) {
			String s =  (String) pjp.getArgs()[0];
			System.out.println("--SLF4J:" + s);
		}
		Object retVal = pjp.proceed();
		return retVal;
	}

	@Around("execution(public void com.sonyericsson.cs.logging.LoggingHandler.initDestinationFile(..))")
	public Object doinitDestinationFile(ProceedingJoinPoint pjp) throws Throwable {
		if (pjp.getArgs().length > 0) {
			String s =  (String) pjp.getArgs()[0];
			System.out.println("--initDestinationFile:" + s);
		}
		Object retVal = pjp.proceed();
		return retVal;
	}

}
