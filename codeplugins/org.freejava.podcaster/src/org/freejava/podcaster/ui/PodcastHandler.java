package org.freejava.podcaster.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler class for Play Podcast action.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class PodcastHandler extends AbstractHandler {
    /**
     * The constructor.
     */
    public PodcastHandler() {
    }

    /**
     * the command has been executed, so extract extract the needed information
     * from the application context.
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            IWorkbenchWindow window = HandlerUtil
                    .getActiveWorkbenchWindowChecked(event);
            IPerspectiveRegistry reg = PlatformUI.getWorkbench()
                    .getPerspectiveRegistry();
            window
                    .getActivePage()
                    .setPerspective(
                            reg.findPerspectiveWithId("org.freejava.podcaster.perspective"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
