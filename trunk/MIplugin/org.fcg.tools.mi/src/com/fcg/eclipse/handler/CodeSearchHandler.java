package com.fcg.eclipse.handler;

import java.net.URL;
import java.net.URLEncoder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;

public class CodeSearchHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		ITextSelection tsel = null;
		if (selection instanceof ITextSelection) {
			tsel = (ITextSelection) selection;
		} else {
			return null;
		}
		String selectedText = tsel.getText();

		try {
			URL url = new URL("http://www.google.com/custom?domains=exampledepot.com&q="
				    + URLEncoder.encode(selectedText, "UTF-8")
				    + "&sitesearch=exampledepot.com&client=pub-6001183370374757&forid=1&ie=ISO-8859-1&oe=ISO-8859-1&cof=GALT%3A%23008000%3BGL%3A1%3BDIV%3A%23336699%3BVLC%3A663399%3BAH%3Acenter%3BBGC%3AFFFFFF%3BLBGC%3A336699%3BALC%3A0000FF%3BLC%3A0000FF%3BT%3A000000%3BGFNT%3A0000FF%3BGIMP%3A0000FF%3BFORID%3A1%3B&hl=en");
			int style = IWorkbenchBrowserSupport.AS_EDITOR
					| IWorkbenchBrowserSupport.LOCATION_BAR
					| IWorkbenchBrowserSupport.STATUS;
			IWebBrowser browser = WorkbenchBrowserSupport.getInstance()
					.createBrowser(style, "console", "Console", "Console");
			browser.openURL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
