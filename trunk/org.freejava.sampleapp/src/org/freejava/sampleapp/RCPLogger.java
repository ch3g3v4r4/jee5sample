package org.freejava.sampleapp;

import org.freejava.sampleapp.log.ILogger;

public class RCPLogger implements ILogger {

	@Override
	public void logError(String message, Throwable ex) {
		Activator.logError(message, ex);
	}

	@Override
	public void logInfo(String message, Throwable ex) {
		Activator.logInfo(message, ex);
	}

	@Override
	public void logWarning(String message, Throwable ex) {
		Activator.logWarning(message, ex);
	}

}
