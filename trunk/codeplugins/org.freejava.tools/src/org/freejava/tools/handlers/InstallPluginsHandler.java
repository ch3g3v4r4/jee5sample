package org.freejava.tools.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.freejava.tools.handlers.usefulPlugins.InstallPluginsDialog;

/**
 * Handler class for Create New Project action.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class InstallPluginsHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public InstallPluginsHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		new InstallPluginsDialog(window.getShell()).open();

		return null;
	}
}
