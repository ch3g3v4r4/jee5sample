package org.freejava.browser.log;

public interface ILogger {
	void logError(String message, Throwable ex);
	void logWarning(String message, Throwable ex);
	void logInfo(String message, Throwable ex);
}
