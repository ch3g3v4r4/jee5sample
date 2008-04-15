package org.builder.eclipsebuilder.beans;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class GanimedeNoP2PartBuilder extends PartBuilderHelper implements PartBuilder {
    private static Logger logger = Logger.getLogger(GanimedeNoP2PartBuilder.class);

    @Override
    public void build(EclipseBuilderContext context) throws Exception {
        super.build(context);
        logger.info("Removing p2 from Ganimede build...");

        // Keep the original product and buildId
        File eclipseDir = new File(context.getEclipseHome(), "eclipse");
        Properties properties = new Properties();
        InputStream is = new FileInputStream(new File(eclipseDir, "configuration/config.ini"));
        properties.load(is);
        is.close();
        String productProp = properties.getProperty("eclipse.product");
        String buildIdProp = properties.getProperty("eclipse.buildId");

        // Remove p2-related stuff
        removeP2Files(eclipseDir);

        // Restore eclipse.ini and configuration.ini
        createEclipseIniAndConfigIniFiles(eclipseDir, productProp, buildIdProp);
        logger.info("Removing p2 from Ganimede build completed.");
    }

    private void createEclipseIniAndConfigIniFiles(File eclipseDir,
            String productProp, String buildIdProp) throws IOException,
            FileNotFoundException {
        logger.info("Copying eclipse.ini file..");
        IOUtils.copy(GanimedeNoP2PartBuilder.class.getResourceAsStream("/eclipse.ini"),
                new FileOutputStream(new File(eclipseDir, "eclipse.ini")));

        logger.info("Creating config.ini file..");
        BufferedReader reader = new BufferedReader(new InputStreamReader(GanimedeNoP2PartBuilder.class.getResourceAsStream("/config.ini")));
        File configurationDir = new File(eclipseDir, "configuration");
        configurationDir.mkdir();
        File configFile = new File(configurationDir, "config.ini");
        BufferedWriter out = new BufferedWriter(new FileWriter(configFile));
        String str;
        while ((str = reader.readLine()) != null) {
            if (str.startsWith("eclipse.product=")) {
                str = "eclipse.product=" + productProp;
            } else  if (str.startsWith("eclipse.buildId=")) {
                str = "eclipse.buildId=" + buildIdProp;
            }
            out.write(str);out.newLine();
        }
        out.close();
        reader.close();
    }

    private void removeP2Files(File eclipseDir) {
        List<File> toRemove = new ArrayList<File>();
        toRemove.add(new File(eclipseDir, "eclipse.ini"));
        toRemove.add(new File(eclipseDir, "dropins"));
        toRemove.add(new File(eclipseDir, "p2"));
        toRemove.add(new File(eclipseDir, "configuration"));
        FileFilter featuresFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.getName().contains("p2");
            }
        };
        File[] files = new File(eclipseDir, "features").listFiles(featuresFilter);
        toRemove.addAll(Arrays.asList(files));
        FileFilter pluginsFilter = new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();
                return name.startsWith("org.eclipse.equinox.p2")
                           || name.startsWith("org.eclipse.ecf")
                           || name.contains("frameworkadmin")
                           || name.contains("sat4j")
                           || name.contains("simpleconfigurator.manipulator");
            }
        };
        files = new File(eclipseDir, "plugins").listFiles(pluginsFilter);
        toRemove.addAll(Arrays.asList(files));
        for (File file : toRemove) {
            if (file.exists()) {
                if (file.isDirectory()) {
                    logger.info("Removing directory: " + file.getAbsolutePath());
                    deleteDir(file);
                } else {
                    logger.info("Removing file: " + file.getAbsolutePath());
                    file.delete();
                }
            }
        }
    }
}
