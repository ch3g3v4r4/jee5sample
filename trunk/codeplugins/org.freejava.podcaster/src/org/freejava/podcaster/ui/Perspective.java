package org.freejava.podcaster.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
    public static final String LEFT_FOLDER = "left";
    public static final String BOTTOM_LEFT_FOLDER = "bottomLeft";
    public static final String RIGHT_FOLDER = "right";

    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(true);
        String editorArea = layout.getEditorArea();
        layout.createPlaceholderFolder(LEFT_FOLDER, IPageLayout.LEFT, 0.3f,
                editorArea);
        layout.createPlaceholderFolder(BOTTOM_LEFT_FOLDER, IPageLayout.BOTTOM,
                0.4f, LEFT_FOLDER);
        layout.createPlaceholderFolder(RIGHT_FOLDER, IPageLayout.BOTTOM, 0.7f,
                editorArea);
    }
}
