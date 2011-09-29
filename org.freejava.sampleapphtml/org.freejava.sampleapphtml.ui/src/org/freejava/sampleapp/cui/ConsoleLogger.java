package org.freejava.sampleapp.cui;

import org.freejava.sampleapp.log.ILogger;

public class ConsoleLogger implements ILogger {

	
	public void logError(String message, Throwable ex) {
		System.out.println("ERROR:" + message);

	}

	
	public void logInfo(String message, Throwable ex) {
		System.out.println("INFO:" + message);

	}

	
	public void logWarning(String message, Throwable ex) {
		System.out.println("WARN:" + message);

	}

}
