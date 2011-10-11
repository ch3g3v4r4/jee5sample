package org.freejava.sampleapp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public static final String ID = "org.freejava.sampleapp.perspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		layout.addStandaloneView(BrowserView.ID,  false, IPageLayout.TOP, 0.7f, editorArea);
		layout.addView("org.eclipse.pde.runtime.LogView",  IPageLayout.BOTTOM, 0.3f, editorArea);
	}

}
