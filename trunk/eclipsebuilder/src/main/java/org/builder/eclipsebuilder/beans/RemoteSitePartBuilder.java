package org.builder.eclipsebuilder.beans;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class RemoteSitePartBuilder implements PartBuilder {
    private static Logger logger = Logger.getLogger(RemoteSitePartBuilder.class);

    protected List<String> features;

    private String remoteSite;

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public void setRemoteSite(String remoteSite) {
        this.remoteSite = remoteSite;
    }

    public void build(EclipseBuilderContext context) throws Exception {
        File eclipseDir = new File(context.getEclipseHome(), "eclipse");
        installSiteUpdate(remoteSite, eclipseDir, features);
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

    private void installSiteUpdate(String url, File eclipse, List<String> features) throws Exception {
        // java -jar plugins/org.eclipse.equinox.launcher_<version>.jar -application  org.eclipse.update.core.standaloneUpdate -command search -from remote_site_url
        // java -jar plugins/org.eclipse.equinox.launcher_<version>.jar -application  org.eclipse.update.core.standaloneUpdate -command install -featureId feature_id -version version -from remote_site_url [-to target_site_dir]
        logger.info("Begin installing from remote site:" + url);

        // List features and versions
        File plugins = new File(eclipse, "plugins");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.getName().startsWith("org.eclipse.equinox.launcher_");
            }
        };
        File[] files = plugins.listFiles(fileFilter);
        Process p = Runtime.getRuntime().exec(
                new String[]{
                        getJavaCommand(), "-jar", files[0].getAbsolutePath(),
                        "-application",  "org.eclipse.update.core.standaloneUpdate",
                        "-command", "search",
                        "-from", url});
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
                feature2Version.put(splits[splits.length - 2], splits[splits.length - 1]);
            }
        }
        for (String feature: features) {
            logger.info("Installing feature: " + feature + "; version: " + feature2Version.get(feature));
            p = Runtime.getRuntime().exec(
                    new String[]{
                            getJavaCommand(), "-jar", files[0].getAbsolutePath(),
                            "-application",  "org.eclipse.update.core.standaloneUpdate",
                            "-command", "install",
                            "-featureId", feature,
                            "-version", feature2Version.get(feature),
                            "-from", url});
            p.waitFor();

        }
        logger.info("Installing site completed.");
    }
}
