package org.builder.eclipsebuilder.beans;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

public class EclipseBuilder {
    private static Logger logger = Logger.getLogger(EclipseBuilder.class);

    private Configuration configuration;
    private List<PartBuilder> partBuilders;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setParts(List<PartBuilder> partBuilders) {
        this.partBuilders = partBuilders;
    }


    public void build() throws Exception {

        EclipseBuilderContext context = new EclipseBuilderContext();
        File cacheHome = getCacheHome();
        context.setCacheHome(cacheHome);
        File eclipseHome = getEclipseHome();
        context.setEclipseHome(eclipseHome);
        context.setBuildType(this.configuration.getBuildType());

        for (PartBuilder builder : this.partBuilders) {
            logger.info("Building Eclipse part using " + builder.getClass().getName());
            builder.build(context);
        }
    }

    private File getEclipseHome() {
        // Validate eclipse home dir
        File eclipseHome = new File(configuration.getEclipseDir());
        boolean eclipseHomeValid;
        if (!eclipseHome.exists()) {
            eclipseHomeValid = eclipseHome.mkdir();
        }
        eclipseHomeValid = eclipseHome.isDirectory() && eclipseHome.canWrite();
        if (!eclipseHomeValid) {
            throw new IllegalArgumentException(
                    "Invalid or not-empty Eclipse directory: " + eclipseHome);
        }
        return eclipseHome;
    }

    private File getCacheHome() {
        // Create and validate cache dir if needed
        File cacheHome = new File(configuration.getCacheDir());
        boolean cacheHomeValid;
        if (!cacheHome.exists()) {
            cacheHomeValid = cacheHome.mkdir();
        } else {
            cacheHomeValid = cacheHome.isDirectory() && cacheHome.canWrite();
        }
        if (!cacheHomeValid) {
            throw new IllegalArgumentException("Invalid cache directory: "
                    + cacheHome);
        }
        return cacheHome;
    }
}
