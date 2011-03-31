package org.freejava.tools.handlers.usefulPlugins;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.Platform;

public class FeatureUpdater {

    protected List<String> features;

    private String remoteSite;

    private String localSite;

    public FeatureUpdater(List<String> features, String remoteSite,
            String localSite) {
        super();
        this.features = features;
        this.remoteSite = remoteSite;
        this.localSite = localSite;
    }

    public List<String> getFeatures() {
        return features;
    }

    public List<String> installFeatures() throws Exception {
        List<String> result;
        if (Platform.getBundle("org.eclipse.equinox.p2.director.app") != null) {
            result = installFeaturesUsingP2Director();
        } else {
            result = installFeaturesUsingUpdateManagerCmd();
        }
        return result;
    }

    private boolean existFeature(String featureId) throws Exception {

        // Faster way, but not always reliable
        IBundleGroupProvider[] providers = Platform.getBundleGroupProviders();
        if (providers != null) {
            for (IBundleGroupProvider provider : providers) {
                IBundleGroup[] bundleGroups = provider.getBundleGroups();
                for (IBundleGroup group : bundleGroups) {
                    if (featureId.equals(group.getIdentifier())) {
                        return true;
                    }
                }
            }
        }

        // Slow way
        List<String> commandLine = getStandaloneUpdateCommand();
        commandLine.add("-command");
        commandLine.add("listFeatures");
        commandLine.add("-from");
        commandLine.add(localSite);

        String[] cmd = (String[])commandLine.toArray(new String[commandLine.size()]);
        Process p = Runtime.getRuntime().exec(cmd);
        InputStream in = p.getInputStream();
        StringBuffer sb = new StringBuffer();
        int c;
        while ((c = in.read()) != -1) {
            sb.append((char)c);
        }
        in.close();
        p.waitFor();
        String patternStr = "^(.*)$";
        Pattern pattern = Pattern.compile(patternStr, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(sb.toString());
        while (matcher.find()) {
            String line = matcher.group(1);
            for (String word : line.split(" ")) {
                if (featureId.equals(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getJavaCommand() {
        String osname = System.getProperty("os.name").toLowerCase();
        String commandName;
        if (osname.indexOf("windows") >= 0) {
            commandName = "javaw.exe";
        } else {
            commandName = "java";
        }
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + commandName;
    }

    private String getEclipsePath() {
        String result = null;
        String osname = System.getProperty("os.name").toLowerCase();
        if (osname.indexOf("windows") >= 0) {
            File eclipsec = new File(new File(this.localSite), "eclipse.exe");
            if (eclipsec.exists()) {
                result = eclipsec.getAbsolutePath();
            }
        }
        if (result == null) {
            File eclipse = new File(new File(this.localSite), "eclipse");
            if (eclipse.exists()) {
                result = eclipse.getAbsolutePath();
            }
        }


        return result;
    }


    private List<String> installFeaturesUsingP2Director() throws Exception {
        String eclipseCommand = getEclipsePath();
        List<String> failedFeatures = new ArrayList<String>();

        for (String feature: features) {

            // ignore if it was installed
            if (existFeature(feature)) continue;

            List<String> commandLine = new ArrayList<String>();
            commandLine.add(eclipseCommand);
            commandLine.add("-nosplash");
            commandLine.add("-application");
            commandLine.add("org.eclipse.equinox.p2.director.app.application");
            commandLine.add("-metadataRepository");
            commandLine.add(remoteSite);
            commandLine.add("-artifactRepository");
            commandLine.add(remoteSite);
            commandLine.add("-installIU");
            String installIU = feature;
            if (!installIU.endsWith(".feature.group")) {
                installIU += ".feature.group";
            }
            commandLine.add(installIU);
            commandLine.add("-version");
            commandLine.add(getLastestVersion(feature));
            System.out.println("executing:" + commandLine);
            String[] cmd = (String[])commandLine.toArray(new String[commandLine.size()]);
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            if (p.exitValue() != 0) {
                failedFeatures.add(feature);
            }
        }
        return failedFeatures;
    }

    private String getLastestVersion(String featureId) throws Exception {

        String latestVersion;

        List<String> commandLine = getStandaloneUpdateCommand();

        List<String> searchCommandLine = new ArrayList<String>(commandLine);
        searchCommandLine.add("-command");
        searchCommandLine.add("search");
        searchCommandLine.add("-from");
        searchCommandLine.add(remoteSite);
        String[] cmd = (String[])searchCommandLine.toArray(new String[searchCommandLine.size()]);
        Process p = Runtime.getRuntime().exec(cmd);
        // Get the input stream and read from it
        InputStream in = p.getInputStream();
        StringBuffer sb = new StringBuffer();
        int c;
        while ((c = in.read()) != -1) {
            sb.append((char)c);
        }
        in.close();
        p.waitFor();

        Map<String, String> feature2Version = new HashMap<String, String>();
        String patternStr = "^(.*)$";
        Pattern pattern = Pattern.compile(patternStr, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(sb.toString());
        while (matcher.find()) {
            String line = matcher.group(1);
            String[] splits = line.split(" ");
            String version = splits[splits.length - 1];
            Pattern numPattern = Pattern.compile("\\d");
            if (numPattern.matcher(version).find()) {
                String newVersion = splits[splits.length - 1];
                String existingVersion = feature2Version.get(splits[splits.length - 2]);
                if (existingVersion == null || existingVersion.compareTo(newVersion) < 0) {
                    feature2Version.put(splits[splits.length - 2], splits[splits.length - 1]);
                }
            }
        }
        latestVersion = feature2Version.get(featureId);
        if (latestVersion == null) {
        	System.out.println("Cannot find the version for feature:" + featureId);
        }
        return latestVersion;
    }

    private List<String> installFeaturesUsingUpdateManagerCmd() throws Exception {
        // java -jar plugins/org.eclipse.equinox.launcher_<version>.jar -application  org.eclipse.update.core.standaloneUpdate -command search -from remote_site_url
        // java -jar plugins/org.eclipse.equinox.launcher_<version>.jar -application  org.eclipse.update.core.standaloneUpdate -command install -featureId feature_id -version version -from remote_site_url [-to target_site_dir]
        List<String> failedFeatures = new ArrayList<String>();

        List<String> commandLine = getStandaloneUpdateCommand();
        for (String feature: features) {

            // ignore if it was installed
            if (existFeature(feature)) continue;

            List<String> installCommandLine = new ArrayList<String>(commandLine);
            installCommandLine.add("-command");
            installCommandLine.add("install");
            installCommandLine.add("-featureId");
            installCommandLine.add(feature);
            installCommandLine.add("-version");
            installCommandLine.add(getLastestVersion(feature));
            installCommandLine.add("-from");
            installCommandLine.add(remoteSite);
            String[] cmd2 = (String[])installCommandLine.toArray(new String[installCommandLine.size()]);
            Process p = Runtime.getRuntime().exec(cmd2);
            p.waitFor();
            if (p.exitValue() != 0) {
                failedFeatures.add(feature);
            }
        }

        return failedFeatures;
    }

    private List<String> getStandaloneUpdateCommand() {
        List<String> commandLine = new ArrayList<String>();

        String javaCommand = getJavaCommand();
        commandLine.add(javaCommand);
        // List features and versions
        File plugins = new File(new File(this.localSite), "plugins");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.getName().startsWith("org.eclipse.equinox.launcher_");
            }
        };
        File[] files = plugins.listFiles(fileFilter);
        commandLine.add("-jar");
        commandLine.add(files[0].getAbsolutePath());
        commandLine.add("-application");
        commandLine.add("org.eclipse.update.core.standaloneUpdate");

        return commandLine;
    }
}
