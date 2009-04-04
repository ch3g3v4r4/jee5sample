package org.freejava.browser;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.freejava.browser.perspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	@Override
	public void postStartup() {
		super.postStartup();
		new RCPLogger().logInfo("Application started.", null);
	}
	@Override
	public void postShutdown() {
		super.postShutdown();
		new RCPLogger().logInfo("Application shutdown.", null);
		//Platform.getLogFileLocation().toFile().delete();
	}
}
