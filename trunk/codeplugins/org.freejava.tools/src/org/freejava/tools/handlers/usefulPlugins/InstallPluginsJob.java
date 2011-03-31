package org.freejava.tools.handlers.usefulPlugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.configuration.ILocalSite;
import org.eclipse.update.core.SiteManager;

public class InstallPluginsJob extends Job {
    private List<Function> functions;
    private List<String> failedFeatures;

    public InstallPluginsJob(List<Function> functions) {
        super("Batch installing useful plugins" );
        this.functions = functions;
    }

    public List<String> getFailedFeatures() {
        return failedFeatures;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Installing plugins", IProgressMonitor.UNKNOWN);

        IStatus status;
        try {
            String localSite = getLocalSite();
            failedFeatures = new ArrayList<String>();

            List<FeatureUpdater> tasks = new ArrayList<FeatureUpdater>();
            for (Function function : functions) {
                String updateURL = function.getUpdateSite();
                List<String> features = function.getFeatureIds();
                tasks.add(new FeatureUpdater(features, updateURL, localSite));

            }

            for (FeatureUpdater task : tasks) {
                monitor.subTask("Installing feature(s): " + task.getFeatures());
                failedFeatures.addAll(task.installFeatures());
                if (monitor.isCanceled()) break;
            }

            status = Status.OK_STATUS;
        } catch (Exception e) {
            e.printStackTrace();
            status = Status.CANCEL_STATUS;
        }
        return status;
    }

    private String getLocalSite() throws CoreException {
        String result = null;
        ILocalSite ls = SiteManager.getLocalSite();
        IConfiguredSite[] sites = ls.getCurrentConfiguration().getConfiguredSites();
        for (IConfiguredSite site : sites) {
            String localSite = site.getSite().getURL().getFile();
            if (new File(localSite, "eclipse.exe").exists()
                    || new File(localSite, "eclipse").exists()) {
                result = localSite;
                break;
            }
        }
        return result;
    }

//    private void updateCheckstyle(IProgressMonitor monitor) throws Exception {
//        String updateURL = "http://eclipse-cs.sourceforge.net/update";
//        String id = "com.atlassw.tools.eclipse.checkstyle";
////        String updateURL = "http://findbugs.cs.umd.edu/eclipse";
////        String id = "edu.umd.cs.findbugs.plugin.eclipse";
///*
//
//        boolean shouldUpdate = false;
//        boolean shouldInstall = false;
//        List<IInstallFeatureOperation> installOps = new ArrayList<IInstallFeatureOperation>();
//
//        ISite site = SiteManager.getSite(updateURL, monitor);
//        IFeatureReference[] frs = site.getFeatureReferences();
//        IFeatureReference targetFeature = null;
//        PluginVersionIdentifier targetVersion = null;
//        for (IFeatureReference fr : frs) {
//            VersionedIdentifier vi = fr.getVersionedIdentifier();
//            if (vi.getIdentifier().equals(id)) {
//                if (targetFeature == null || vi.getVersion().isGreaterThan(targetVersion)) {
//                    targetFeature = fr;
//                    targetVersion = vi.getVersion();
//                }
//            }
//        }
//        System.out.println(targetFeature.getVersionedIdentifier().getIdentifier()
//                + targetVersion);
//
//        IPlatformConfiguration c = ConfiguratorUtils.getCurrentPlatformConfiguration();
//        IFeatureEntry feature = c.findConfiguredFeatureEntry(id);
//        if (feature != null) {
//            VersionedIdentifier existingVersion = new VersionedIdentifier(id, feature.getFeatureVersion());
//            if (targetVersion.isGreaterThan(existingVersion.getVersion())) {
//                shouldUpdate = true;
//            }
//        } else {
//            shouldInstall = true;
//        }
//
//        ILocalSite ls = SiteManager.getLocalSite();
//
//        if (shouldInstall || shouldUpdate) {
//        	if (shouldInstall) {
//        		IConfiguredSite[] sites = ls.getCurrentConfiguration().getConfiguredSites();
//				IConfiguredSite ics = sites[0];
//
//				installOps.add(
//						OperationsManager.getOperationFactory().createInstallOperation(
//								ics,                         // target site
//								targetFeature.getFeature(new NullProgressMonitor()), // feature to install
//								(IFeatureReference[]) null,  // optionalFeatures
//								(IFeature[]) null,           // unconfiguredOptionalFeatures
//								(IVerificationListener) null // verifier
//								));
//        	}
//        }
//		if (installOps.size() > 0) {
//			for (IInstallFeatureOperation featToInstall : installOps) {
//				featToInstall.execute(
//						monitor,
//						(IOperationListener) null    // listener
//						);
//			}
//
//			ls.save();
//		}
//
//
//        System.out.println("shouldUpdate=" + shouldUpdate);
//        System.out.println("shouldInstall=" + shouldInstall);
//        */
//        List<String> features = new ArrayList<String>();
//        features.add(id);
//        ILocalSite ls = SiteManager.getLocalSite();
//		IConfiguredSite[] sites = ls.getCurrentConfiguration().getConfiguredSites();
//		IConfiguredSite ics = sites[0];
//        String localSite = ics.getSite().getURL().getFile();
//        FeatureUpdater upd =  new FeatureUpdater(features, updateURL, localSite);
//        upd.installSiteUpdate();
//    }
}