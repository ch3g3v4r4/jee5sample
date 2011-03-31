package org.freejava.tools.handlers.newproject;

import java.io.File;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewProjectDialog extends TitleAreaDialog {

	private Text directory;
	private Text groupId;
	private Text artifactId;

	private Button webProject;
	private Button checkstyleSupport;
	private Button findBugsSupport;
	private Button springSupport;
	private Button hibernateSupport;

	private NewProjectModel model;

	public NewProjectDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("New Maven Project");
	}

	@Override
	protected Control createContents(Composite parent) {
		Control container = super.createContents(parent);
		setTitle("Enter Project Options");
		setMessage("A Maven-based web project will be created using the following options.");
		return container;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		createForm(parent);

		return container;
	}

	private Composite createForm(Composite parent) {

		this.model = new NewProjectModel();
		this.model.setWebProject(true);
		this.model.setCheckstyleSupport(true);
		this.model.setFindBugsSupport(true);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		Label label;
		GridData gridData;

		// row 1: Target Directory

		label = new Label(composite, SWT.LEFT);
		label.setText("Target directory:");
		directory = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		directory.setLayoutData(gridData);
		Button browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(getShell());
				dirDialog.setText("Select target directory");
				String selectedDir = dirDialog.open();
				if (selectedDir != null) {
					directory.setText(selectedDir);
				}
			}

			public void mouseUp(MouseEvent e) {
			}
		});
		//binding values to model
		DataBindingContext dbc = new DataBindingContext();
		IObservableValue modelObservable = BeansObservables.observeValue(model, "targetDirectory");
		dbc.bindValue(SWTObservables.observeText(directory, SWT.Modify), modelObservable, null, null);

		// row 2: Group ID

		label = new Label(composite, SWT.LEFT);
		label.setText("Group ID:");
		groupId = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		groupId.setLayoutData(gridData);
		//binding values to model
		modelObservable = BeansObservables.observeValue(model, "groupId");
		dbc.bindValue(SWTObservables.observeText(groupId, SWT.Modify), modelObservable, null, null);

		// row 3: Artifact ID

		label = new Label(composite, SWT.LEFT);
		label.setText("Artifact ID:");
		artifactId = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		artifactId.setLayoutData(gridData);
		//binding values to model
		modelObservable = BeansObservables.observeValue(model, "artifactId");
		dbc.bindValue(SWTObservables.observeText(artifactId, SWT.Modify), modelObservable, null, null);

		// row 4: Is a Web Project

		label = new Label(composite, SWT.LEFT);
		label.setText("Is a web project:");
		webProject = new Button(composite, SWT.CHECK);
		webProject.setSelection(true);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		webProject.setLayoutData(gridData);
		//binding values to model
		modelObservable = BeansObservables.observeValue(model, "webProject");
		dbc.bindValue(SWTObservables.observeSelection(webProject), modelObservable, null, null);

		// row 5: Checkstyle support

		label = new Label(composite, SWT.LEFT);
		label.setText("Checkstyle support:");
		checkstyleSupport = new Button(composite, SWT.CHECK);
		//checkstyleSupport.setSelection(true);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		checkstyleSupport.setLayoutData(gridData);
		//binding values to model
		modelObservable = BeansObservables.observeValue(model, "checkstyleSupport");
		dbc.bindValue(SWTObservables.observeSelection(checkstyleSupport), modelObservable, null, null);

		// row 7: FindBugs support

		label = new Label(composite, SWT.LEFT);
		label.setText("FindBugs support:");
		findBugsSupport = new Button(composite, SWT.CHECK);
		//findBugsSupport.setSelection(true);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		findBugsSupport.setLayoutData(gridData);
		//binding values to model
		modelObservable = BeansObservables.observeValue(model, "findBugsSupport");
		dbc.bindValue(SWTObservables.observeSelection(findBugsSupport), modelObservable, null, null);

		// row 9: Spring support

		label = new Label(composite, SWT.LEFT);
		label.setText("Spring support:");
		springSupport = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		springSupport.setLayoutData(gridData);
		//binding values to model
		modelObservable = BeansObservables.observeValue(model, "springSupport");
		dbc.bindValue(SWTObservables.observeSelection(springSupport), modelObservable, null, null);


		// row 10: Hibernate support

		label = new Label(composite, SWT.LEFT);
		label.setText("Hibernate support:");
		hibernateSupport = new Button(composite, SWT.CHECK);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		hibernateSupport.setLayoutData(gridData);
		//binding values to model
		modelObservable = BeansObservables.observeValue(model, "hibernateSupport");
		dbc.bindValue(SWTObservables.observeSelection(hibernateSupport), modelObservable, null, null);


		// Set OK button status
		directory.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (e.getSource() == directory) {
					getButton(IDialogConstants.OK_ID).setEnabled(validate());
				}
			}
		});
		groupId.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (e.getSource() == groupId) {
					getButton(IDialogConstants.OK_ID).setEnabled(validate());
				}
			}
		});
		artifactId.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (e.getSource() == artifactId) {
					getButton(IDialogConstants.OK_ID).setEnabled(validate());
				}
			}
		});

		return composite;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control buttonBar = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		return buttonBar;
	}

	private boolean validate() {
		String dirText = directory.getText();
		String groupText = groupId.getText();
		String artifactText = artifactId.getText();
		boolean result = new File(dirText).exists()
				&& new File(dirText).isDirectory() && (groupText != null)
				&& (!groupText.trim().equals("")) && (artifactText != null)
				&& (!artifactText.trim().equals(""));
		return result;
	}

	@Override
	protected void okPressed() {
		ProjectManager pm = new ProjectManager();

		if (pm.createProject(model)) {
			MessageDialog.openInformation(this.getShell(), "Project Creation", "Project is created successfully!");
			super.okPressed();
		} else {
			MessageDialog.openError(this.getShell(), "Project Creation", "Project could not be created!");
		}
	}

}
