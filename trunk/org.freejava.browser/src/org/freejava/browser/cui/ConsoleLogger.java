package org.freejava.browser.cui;

import org.freejava.browser.log.ILogger;

public class ConsoleLogger implements ILogger {

	@Override
	public void logError(String message, Throwable ex) {
		System.out.println("ERROR:" + message);

	}

	@Override
	public void logInfo(String message, Throwable ex) {
		System.out.println("INFO:" + message);

	}

	@Override
	public void logWarning(String message, Throwable ex) {
		System.out.println("WARN:" + message);

	}

}
