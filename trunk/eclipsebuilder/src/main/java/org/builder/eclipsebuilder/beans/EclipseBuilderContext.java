package org.builder.eclipsebuilder.beans;

import java.io.File;

import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class EclipseBuilderContext {

    private File eclipseHome;

    private File cacheHome;

    private BuildType buildType;

    public File getEclipseHome() {
        return eclipseHome;
    }

    public void setEclipseHome(File eclipseHome) {
        this.eclipseHome = eclipseHome;
    }

    public File getCacheHome() {
        return cacheHome;
    }

    public void setCacheHome(File cacheHome) {
        this.cacheHome = cacheHome;
    }

    public BuildType getBuildType() {
        return buildType;
    }

    public void setBuildType(BuildType buildType) {
        this.buildType = buildType;
    }

}
