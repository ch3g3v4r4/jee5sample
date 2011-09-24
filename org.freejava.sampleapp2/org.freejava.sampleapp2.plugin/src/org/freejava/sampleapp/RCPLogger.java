package org.freejava.sampleapp;

import org.freejava.sampleapp.log.ILogger;

public class RCPLogger implements ILogger {

	
	public void logError(String message, Throwable ex) {
		Activator.logError(message, ex);
	}

	
	public void logInfo(String message, Throwable ex) {
		Activator.logInfo(message, ex);
	}

	
	public void logWarning(String message, Throwable ex) {
		Activator.logWarning(message, ex);
	}

}
