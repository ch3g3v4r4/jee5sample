package org.freejava.sampleapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.ViewPart;

import com.google.common.io.Files;


public class BrowserView extends ViewPart {

	public static final String ID = "org.freejava.sampleapp.browserview";

	private Browser browser;
	private File tmpDir;

    private static BrowserView findBrowser(IWorkbenchWindow window) {
        IWorkbenchPage page = window.getActivePage();
        IViewPart view = page.findView(ID);
        if (view != null) {
            return (BrowserView) view;
        }
        IViewReference[] refs = page.getViewReferences();
        for (int i = 0; i < refs.length; i++) {
            if (ID.equals(refs[i].getId())) {
                return (BrowserView) refs[i].getPart(true);
            }
        }
        return null;
    }

	public void createPartControl(Composite parent) {
		browser = createBrowser(parent, getViewSite().getActionBars());
		try {
			Enumeration<URL> e = Activator.getDefault().getBundle().findEntries("ui", "*", true);
			tmpDir = Files.createTempDir();
			for (;e.hasMoreElements();) {
				URL url = e.nextElement();
				String urlpath = url.getFile();
				if (!urlpath.endsWith("/")) {
				    File file = createFile(tmpDir, urlpath);
				    InputStream is = url.openStream();
				    OutputStream os = new FileOutputStream(file);
				    IOUtils.copy(is, os);
				    IOUtils.closeQuietly(is);
				    IOUtils.closeQuietly(os);
				}
			}
			JettyServer.start("freejava");
			String url = "http://127.0.0.1:9999/ui/index.html";//new File(tmpDir, "ui/index.html").toURI().toURL().toString();
			browser.setUrl(url);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private File createFile(File parent, String filename) {
		String path = FilenameUtils.getPath(filename);
		String name = FilenameUtils.getName(filename);
		File dir = new File(parent, path);
		dir.mkdirs();
		return new File(dir, name);
	}

	public void setFocus() {
		if (browser != null && !browser.isDisposed()) {
			browser.setFocus();
		}
	}

	private Browser createBrowser(Composite parent, final IActionBars actionBars) {

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		parent.setLayout(gridLayout);
		for (int style : new int[]{SWT.WEBKIT, SWT.MOZILLA, SWT.NONE}) {
			try {
				browser = new Browser(parent, style);
				break;
			} catch (Exception e) {
			}
		}
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		browser.setLayoutData(data);

		browser.addProgressListener(new ProgressAdapter() {
			IProgressMonitor monitor = actionBars.getStatusLineManager().getProgressMonitor();
			boolean working = false;
			int workedSoFar;
			public void changed(ProgressEvent event) {

				if (event.total == 0) return;
				if (!working) {
					if (event.current == event.total) return;
					monitor.beginTask("", event.total); //$NON-NLS-1$
					workedSoFar = 0;
					working = true;
				}
				monitor.worked(event.current - workedSoFar);
				workedSoFar = event.current;
			}
			public void completed(ProgressEvent event) {

				monitor.done();
				working = false;
			}
		});
		browser.addStatusTextListener(new StatusTextListener() {
			IStatusLineManager status = actionBars.getStatusLineManager();
			public void changed(StatusTextEvent event) {

				status.setMessage(event.text);
			}
		});

		browser.addTitleListener(new TitleListener() {
            public void changed(TitleEvent event) {
                setPartName(event.title);
            }
        });
        browser.addOpenWindowListener(new OpenWindowListener() {
            public void open(WindowEvent event) {
                BrowserView.this.openWindow(event);
            }
        });
        // TODO: should handle VisibilityWindowListener.show and .hide events
        browser.addCloseWindowListener(new CloseWindowListener() {
            public void close(WindowEvent event) {
                BrowserView.this.close();
            }
        });

		return browser;
	}

    /**
     * Opens a new browser window.
     *
     * @param event the open window event
     */
    private void openWindow(WindowEvent event) {
        try {
            IWorkbench workbench = getSite().getWorkbenchWindow().getWorkbench();
            IWorkbenchWindow window = workbench.openWorkbenchWindow(Perspective.ID, null);
            Shell shell = window.getShell();
            if (event.location != null)
                shell.setLocation(event.location);
            if (event.size != null)
                shell.setLocation(event.size);
            BrowserView view = findBrowser(window);
            Assert.isNotNull(view);
            event.browser = view.browser;
        } catch (WorkbenchException e) {
            //Activator.getDefault().log(e);
        }
    }

    /**
     * Closes this browser view.  Closes the window too if there
     * are no non-secondary parts open.
     */
    private void close() {
        IWorkbenchPage page = getSite().getPage();
        IWorkbenchWindow window = page.getWorkbenchWindow();
        page.hideView(this);
        //if (Activator.getNonSecondaryParts(page).size() == 0) {
        //    page.closePerspective(page.getPerspective(), true, true);
        //}
        if (window.getActivePage() == null) {
            window.close();
        }
    }

    @Override
    public void dispose() {
    	try{
    		Files.deleteRecursively(tmpDir);
    	} catch (Exception e) {
		}
    	super.dispose();
    }

}