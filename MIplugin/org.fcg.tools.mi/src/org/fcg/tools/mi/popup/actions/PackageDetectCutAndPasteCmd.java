package org.fcg.tools.mi.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.Language;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Renderer;
import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.cmd.AbstractDefaultCommand;
import net.sourceforge.pmd.runtime.cmd.CPDVisitor;
import net.sourceforge.pmd.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.runtime.properties.PropertiesException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPropertyListener;

public class PackageDetectCutAndPasteCmd extends AbstractDefaultCommand {
    private static final Logger log = Logger.getLogger(PackageDetectCutAndPasteCmd.class);
    private IProject project;
    private Language language;
    private int minTileSize;
    private Renderer renderer;
    private String reportName;
    private boolean createReport;
    private List listenerList;
    private List packages;

    /**
     * Default Constructor
     */
    public PackageDetectCutAndPasteCmd() {
        super();
        this.setDescription("Detect Cut & paste for a project");
        this.setName("DetectCutAndPaste");
        this.setOutputProperties(true);
        this.setReadOnly(false);
        this.setTerminated(false);
        this.listenerList = new ArrayList();
        this.packages = null;
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        try {
            // find the files
            final List files = findFiles();

            if (files.size() == 0) {
                PMDRuntimePlugin.getDefault().logInformation("No files found to specified language.");
            } else {
                PMDRuntimePlugin.getDefault().logInformation("Found " + files.size() + " files to the specified language. Performing CPD.");
            }
            setStepsCount(files.size());
            beginTask("Finding suspect Cut And Paste", getStepsCount()*2);

            if (!isCanceled()) {
                // detect cut and paste
                final CPD cpd = detectCutAndPaste(files);

                if (!isCanceled()) {
                    // if the command was not canceled
                    if (this.createReport) {
                        // create the report optionally
                        this.renderReport(cpd.getMatches());
                    }

                    // trigger event propertyChanged for all listeners
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            final Iterator listenerIterator = listenerList.iterator();
                            while (listenerIterator.hasNext()) {
                                final IPropertyListener listener = (IPropertyListener) listenerIterator.next();
                                listener.propertyChanged(cpd.getMatches(), PMDRuntimeConstants.PROPERTY_CPD);
                            }
                        }
                    });
                }
            }
        } catch (CoreException e) {
            log.debug("Core Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        } catch (PropertiesException e) {
            log.debug("Properties Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        } finally {
            this.setTerminated(true);
        }
    }

    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        this.setProject(null);
        this.setTerminated(false);
        this.setReportName(null);
        this.setRenderer(null);
        this.setLanguage(LanguageFactory.JAVA_KEY);
        this.setMinTileSize(PMDRuntimePlugin.getDefault().loadPreferences().getMinTileSize());
        this.setCreateReport(false);
        this.addPropertyListener(null);
        this.listenerList = new ArrayList();
        this.packages = null;
    }

    /**
     * @param language The language to set.
     */
    public void setLanguage(final String language) {
        this.language = new LanguageFactory().createLanguage(language);
    }

    /**
     * @param tilesize The tilesize to set.
     */
    public void setMinTileSize(final int tilesize) {
        this.minTileSize = tilesize;
    }

    /**
     * @param project The project to set.
     */
    public void setProject(final IProject project) {
        this.project = project;
    }

    /**
     * @param renderer The renderer to set.
     */
    public void setRenderer(final Renderer renderer) {
        this.renderer = renderer;
    }

    /**
     * @param reportName The reportName to set.
     */
    public void setReportName(final String reportName) {
        this.reportName = reportName;
    }

    /**
     * @param render render a report or not.
     */
    public void setCreateReport(final boolean render) {
        this.createReport = render;
    }

    /**
     * Adds an object that wants to get an event after the command is finished.
     * @param listener the property listener to set.
     */
    public void addPropertyListener(IPropertyListener listener) {
        this.listenerList.add(listener);
    }

	public void setPackages(List packages) {
		this.packages = packages;
	}

	/**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    public boolean isReadyToExecute() {
        return (this.project != null)
            && (this.language != null)
            && (!this.createReport // need a renderer and reportname if a report should be created
                    || ((this.renderer != null) && (this.reportName != null)));
    }

    /**
     * Finds all files in a project based on a language.
     * Uses internally the CPDVisitor.
     * @return List of files
     * @throws PropertiesException
     * @throws CoreException
     */
    private List findFiles() throws PropertiesException, CoreException {
    	List result;
    	if (packages == null) {
	        final IProjectProperties properties = PMDRuntimePlugin.getDefault().loadProjectProperties(project);
	        final CPDVisitor visitor = new CPDVisitor();
	        visitor.setWorkingSet(properties.getProjectWorkingSet());
	        visitor.setIncludeDerivedFiles(properties.isIncludeDerivedFiles());
	        visitor.setLanguage(language);
	        visitor.setFiles(new ArrayList());
	        this.project.accept(visitor);
	        result = visitor.getFiles();
    	} else {
    		result = new ArrayList();
    		for (Iterator it = packages.iterator(); it.hasNext();) {
				IPackageFragment packageSelection = (IPackageFragment) it.next();
				IJavaElement[] elems = packageSelection.getChildren();
				for (int i = 0; i < elems.length; i++) {
					IJavaElement javaElement = elems[i];
					if (javaElement instanceof ICompilationUnit) {
						ICompilationUnit unit = (ICompilationUnit) javaElement;
						result.add(new File(unit.getUnderlyingResource().getLocation().toOSString()));
					}
				}
			}
    	}
    	return result;
    }

    /**
     * Run the cut and paste detector. At first all files have to be added
     * to the cpd. Then the CPD can be executed.
     * @param files List of files to be checked.
     * @return the CPD itself for retrieving the matches.
     * @throws CoreException
     */
    private CPD detectCutAndPaste(final List files) {
        log.debug("Searching for project files");
        final CPD cpd = new CPD(minTileSize, language);

        subTask("Adding files for the CPD");
        final Iterator fileIterator = files.iterator();
        while (fileIterator.hasNext() && !isCanceled()) {
            final File file = (File) fileIterator.next();
            try {
                cpd.add(file);
                worked(1);
            } catch (IOException e) {
                log.warn("IOException when adding file " + file.getName() + " to CPD. Continuing.", e);
            }
        }

        if (!isCanceled()) {
            subTask("Performing CPD");
            log.debug("Performing CPD");
            cpd.go();
            worked(getStepsCount());
        }

        return cpd;
    }

    /**
     * Renders a report using the matches of the CPD. Creates a report folder
     * and report file.
     * @param matches matches of the CPD
     * @throws CommandException
     */
    private void renderReport(Iterator matches) throws CommandException {
        try {
            log.debug("Rendering CPD report");
            subTask("Rendering CPD report");
            final String reportString = this.renderer.render(matches);

            // Create the report folder if not already existing
            log.debug("Create the report folder");
            final IFolder folder = this.project.getFolder(PMDRuntimeConstants.REPORT_FOLDER);
            if (!folder.exists()) {
                folder.create(true, true, this.getMonitor());
            }

            // Create the report file
            log.debug("Create the report file");
            final IFile reportFile = folder.getFile(this.reportName);
            final InputStream contentsStream = new ByteArrayInputStream(reportString.getBytes());
            if (reportFile.exists()) {
                log.debug("   Overwritting the report file");
                reportFile.setContents(contentsStream, true, false, this.getMonitor());
            } else {
                log.debug("   Creating the report file");
                reportFile.create(contentsStream, true, this.getMonitor());
            }
            reportFile.refreshLocal(IResource.DEPTH_INFINITE, this.getMonitor());
            contentsStream.close();
        } catch (CoreException e) {
            log.debug("Core Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        } catch (IOException e) {
            log.debug("IO Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        }
    }}
