package org.fcg.tools.mi.popup.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.cpd.CSVRenderer;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Renderer;
import net.sourceforge.pmd.cpd.SimpleRenderer;
import net.sourceforge.pmd.cpd.XMLRenderer;
import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.dialogs.CPDCheckDialog;
import net.sourceforge.pmd.ui.views.CPDView;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

public class CPDAction implements IObjectActionDelegate {
    private static final String XML_KEY = "XML";
    private static final String SIMPLE_KEY = "Simple Text";
    private static final String CSV_KEY = "CSV";
    private IWorkbenchPart targetPart;
	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public CPDAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		if (!(selection instanceof IStructuredSelection)) {
			return;
		}

        final IWorkbenchPartSite site = targetPart.getSite();
        final ISelection sel = site.getSelectionProvider().getSelection();
        final Shell shell = site.getShell();
        final String[] languages = {
                LanguageFactory.JAVA_KEY,
                LanguageFactory.CPP_KEY,
                LanguageFactory.C_KEY,
                LanguageFactory.JSP_KEY,
                LanguageFactory.PHP_KEY,
                LanguageFactory.RUBY_KEY
        };

        final String[] formats = {
                SIMPLE_KEY,
                XML_KEY,
                CSV_KEY
        };

        final CPDCheckDialog dialog = new CPDCheckDialog(shell, languages, formats);

        if (dialog.open() != Dialog.OK) return;

        final String selectedLanguage = dialog.getSelectedLanguage();
        final int tilesize = dialog.getTileSize();
        final boolean createReport = dialog.isCreateReportSelected();
        final Renderer selectedRenderer = this.createRenderer(dialog.getSelectedFormat());
        final String fileName = this.createFileName(dialog.getSelectedFormat());
        final CPDView view = showView();

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		List packages = new ArrayList();
		IProject project = null;
		for (Iterator iterator = structuredSelection.iterator(); iterator.hasNext();) {
			IPackageFragment packageSelection = (IPackageFragment) iterator.next();
			if (project == null) project = packageSelection.getJavaProject().getProject();
			if (packageSelection.getJavaProject().getProject() == project) {
				// just check packages of same project
				packages.add(packageSelection);
			}
		}
        final PackageDetectCutAndPasteCmd detectCmd = new PackageDetectCutAndPasteCmd();
        detectCmd.setProject(project);
        detectCmd.setCreateReport(createReport);
        detectCmd.setLanguage(selectedLanguage);
        detectCmd.setMinTileSize(tilesize);
        detectCmd.setRenderer(selectedRenderer);
        detectCmd.setReportName(fileName);
        detectCmd.setUserInitiated(true);
        detectCmd.addPropertyListener(view);
        try {
			detectCmd.performExecute();
		} catch (CommandException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

    /**
     * Shows the view.
     * @param matches
     */
    private CPDView showView() {
        CPDView view = null;
        try {
            final IWorkbenchPage workbenchPage = targetPart.getSite().getPage();
            view = (CPDView) workbenchPage.showView(PMDUiConstants.ID_CPDVIEW);
        } catch (PartInitException pie) {

        }
        return view;
    }

    /**
     * Creates a renderer from a key.
     * @param rendererKey xml, simple or cvs key
     * @return Renderer
     */
    private Renderer createRenderer(final String rendererKey) {
        Renderer renderer = null;
        if (XML_KEY.equals(rendererKey)) {
            renderer = new XMLRenderer();
        } else if (SIMPLE_KEY.equals(rendererKey)) {
            renderer = new SimpleRenderer();
        } else if (CSV_KEY.equals(rendererKey)) {
            renderer = new CSVRenderer();
        }
        return renderer;
    }

    /**
     * Creates a filename according to the renderer.
     * @param rendererKey xml, simple or cvs key
     * @return file name
     */
    private String createFileName(String rendererKey) {
        String fileName = null;
        if (XML_KEY.equals(rendererKey)) {
            fileName = PMDRuntimeConstants.XML_CPDREPORT_NAME;
        } else if (CSV_KEY.equals(rendererKey)) {
            fileName = PMDRuntimeConstants.CSV_CPDREPORT_NAME;
        } else {
            fileName = PMDRuntimeConstants.SIMPLE_CPDREPORT_NAME;
        }
        return fileName;
    }
}
