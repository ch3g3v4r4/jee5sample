package org.freejava.tools.handlers.usefulPlugins;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.freejava.tools.Activator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InstallPluginsDialog extends TitleAreaDialog {

    private List<Button> buttons = new ArrayList<Button>();

    public InstallPluginsDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Install Plugins");
    }

    @Override
    protected Control createContents(Composite parent) {
        Control container = super.createContents(parent);
        setTitle("Select Plugins");
        setMessage("Selected plugins will be installed automatically.");
        return container;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);

        createForm(parent);

        return container;
    }

    private Composite createForm(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        composite.setLayout(layout);

        Label label;
        GridData gridData;

        List<Function> functions = parseFunctions();
        for (Function function : functions) {
            label = new Label(composite, SWT.LEFT);
            label.setText(function.getName());
            Button button = new Button(composite, SWT.CHECK);
            button.setSelection(function.isDefaultChecked());
            gridData = new GridData();
            gridData.horizontalSpan = 2;
            button.setLayoutData(gridData);
            button.setData(function);
            buttons.add(button);
        }

        return composite;
    }

    private List<Function> parseFunctions() {
        List<Function> result = new ArrayList<Function>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            InputStream is = InstallPluginsDialog.class.getResourceAsStream("/xml/functions.xml");
            try {
                Document doc = factory.newDocumentBuilder().parse(is);
                Element rootElement = doc.getDocumentElement();
                NodeList functions = rootElement.getChildNodes();
                for (int i = 0; i < functions.getLength(); i++) {
                    if (functions.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element function = (Element) functions.item(i);
                        result.add(parseFunction(function));
                    }
                }
            } finally {
                if (is != null) is.close();
            }
        } catch (Exception e) {
            Activator.logError("Cannot load metadata file for installing plugins", e);
            e.printStackTrace();
        }
        return result;
    }

    private Function parseFunction(Element function) {
        Function result;
        String name = function.getElementsByTagName("name").item(0).getTextContent().trim();
        String defaultStr = function.getElementsByTagName("default").item(0).getTextContent().trim();
        String site = function.getElementsByTagName("site").item(0).getTextContent().trim();
        List<String> featureIds = new ArrayList<String>();
        NodeList functionIdList = function.getElementsByTagName("featureId");
        for (int i = 0 ; i < functionIdList.getLength(); i++) {
            featureIds.add(functionIdList.item(i).getTextContent().trim());
        }
        result = new Function(name, Boolean.valueOf(defaultStr), site, featureIds);
        return result;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        return buttonBar;
    }

    @Override
    protected void okPressed() {
        List<Function> functions = new ArrayList<Function>();
        for (Button button : buttons) {
            if (button.getSelection()) {
                Function function = (Function) button.getData();
                functions.add(function);
            }
        }
        InstallPluginsJob job = new InstallPluginsJob(functions);
        job.addJobChangeListener(new InstallPluginsJobAdapter(getParentShell()));
        job.schedule();
        super.okPressed();
    }

    private static class InstallPluginsJobAdapter extends JobChangeAdapter {
        private Shell shell;
        public InstallPluginsJobAdapter(Shell shell) {
            this.shell = shell;
        }
        public void done(final IJobChangeEvent event) {
            Display.getDefault().asyncExec(new InstallPluginsJobAdapterRunnable(shell, event));
        }
    }

    private static class InstallPluginsJobAdapterRunnable implements Runnable {
        private Shell shell;
        private IJobChangeEvent event;
        public InstallPluginsJobAdapterRunnable(Shell shell, IJobChangeEvent event) {
            this.shell = shell;
            this.event = event;
        }
        public void run() {
            InstallPluginsJob job = (InstallPluginsJob) event.getJob();
            IStatus status = event.getJob().getResult();
            String message;
            boolean error = true;
            if (status.equals(Status.OK_STATUS)) {
                if (job.getFailedFeatures().isEmpty()) {
                    message = "Completed successfully! Restart your Eclipse to see new features.";
                    error = false;
                } else {
                    message = "Could not install feature(s): " + job.getFailedFeatures();
                }
            } else {
                if (job.getFailedFeatures().isEmpty()) {
                    message = "Failed!";
                } else {
                    message = "Failed and could not install feature(s): " + job.getFailedFeatures();
                }
            }
            if (error) {
                MessageDialog.openError(shell, "Plugin Installation", message);
            } else {
                MessageDialog.openInformation(shell, "Plugin Installation", message);
            }
        }
    }
}

