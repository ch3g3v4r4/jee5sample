package org.fcg.tools.mi.popup.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.NoSuchValueException;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;

public class MaintainabilityIndexAction implements IObjectActionDelegate {

	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public MaintainabilityIndexAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		IPackageFragment packageSelection = (IPackageFragment) structuredSelection
				.getFirstElement();

		try {
			List files = findFiles(packageSelection);
			File listFile = File.createTempFile("milist", ".txt");
			BufferedWriter listWriter = new BufferedWriter(new FileWriter(listFile));
			String newline = System.getProperty("line.separator");
			for (Iterator it = files.iterator(); it.hasNext();) {
				File f = (File) it.next();
				listWriter.write(f.getAbsolutePath() + newline);
			}
			listWriter.close();


	        // D:/Program Files/STI/bin/pc-win95/undjava.exe -create -db p -add @l.txt
			File project = File.createTempFile("miproject", ".udj");
	        String undjavaCmd = new File(getUndJavaPc95Dir(), "undjava.exe").getAbsolutePath();

	        StringBuffer output = new StringBuffer();

	        // Create a project and add files to it
	        String[] commands = new String[]{undjavaCmd, "-create", "-db", project.getAbsolutePath(), "-add", "@" + listFile};
	        Process child = Runtime.getRuntime().exec(commands);
	        InputStream in = child.getInputStream();
	        int c;
	        while ((c = in.read()) != -1) {
	        	output.append((char)c);
	        }
	        in.close();
	        child.waitFor();

	        // remove temp file
	        listFile.delete();

	        // Analyze the files
	        // undjava -db myproject.udj -rebuild
	        commands = new String[]{undjavaCmd, "-rebuild", "-db", project.getAbsolutePath()};
	        child = Runtime.getRuntime().exec(commands);
	        in = child.getInputStream();
	        while ((c = in.read()) != -1) {
	        	output.append((char)c);
	        }
	        in.close();
	        child.waitFor();

            // "D:\Program Files\STI\bin\pc-win95\\uperl" "D:\Program Files\STI\sample\scripts\acj_maint_index_halstead.pl" -db p.udj
	        String uperlCmd = new File(getUndJavaPc95Dir(), "uperl.exe").getAbsolutePath();
	        String miScriptPath = getMIScriptPath();
	        commands = new String[]{uperlCmd, miScriptPath, "-db", project.getAbsolutePath()};
	        child = Runtime.getRuntime().exec(commands);
	        in = child.getInputStream();
	        while ((c = in.read()) != -1) {
	        	output.append((char)c);
	        }
	        in.close();
	        child.waitFor();

	        // remove temp file
	        project.delete();

	        MessageConsole myConsole = findConsole("Maintainability Index Console");
			MessageConsoleStream out = myConsole.newMessageStream();
			out.println(output.toString());
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			String id = IConsoleConstants.ID_CONSOLE_VIEW;
			IConsoleView view = (IConsoleView) page.showView(id);
			view.display(myConsole);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getMIScriptPath() {
		String result;
		try {
			String installDir = getUnderstandForJavaInstallDir();
			result = installDir + File.separator + "sample" + File.separator
					+ "scripts" + File.separator + "acj_maint_index_halstead.pl";
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	private String getUndJavaPc95Dir() {
		String result;
		try {
			String installDir = getUnderstandForJavaInstallDir();
			result = installDir + File.separator + "bin" + File.separator + "pc-win95";
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	private String getUnderstandForJavaInstallDir() throws NoSuchKeyException,
			RegistryException, NoSuchValueException {
		// HKEY_CURRENT_USER\Software\Scientific Toolworks, Inc.\UnderstandJava
		//  InstallDir = D:\Program Files\STI
		RegistryKey topKey = Registry.getTopLevelKey("HKEY_CURRENT_USER");
		RegistryKey subKey = topKey.openSubKey("Software\\Scientific Toolworks, Inc.\\UnderstandJava");
		String installDir = subKey.getStringValue("InstallDir");
		return installDir;
	}

	private List findFiles(IPackageFragment packageSelection)
			throws JavaModelException {
		List result = new ArrayList();
		IJavaElement[] elems = packageSelection.getChildren();
		for (int i = 0; i < elems.length; i++) {
			IJavaElement javaElement = elems[i];
			if (javaElement instanceof ICompilationUnit) {
				ICompilationUnit unit = (ICompilationUnit) javaElement;
				result.add(new File(unit.getUnderlyingResource().getLocation().toOSString()));
			}
		}
		return result;
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
}
