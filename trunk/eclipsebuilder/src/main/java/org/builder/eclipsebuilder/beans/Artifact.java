package org.builder.eclipsebuilder.beans;

import org.builder.eclipsebuilder.beans.Configuration.BuildType;

public class Artifact {
    private BuildType buildType;
    private String version;
    private String buildDate;
    private String artifactId;
    private String fileName;

    public BuildType getBuildType() {
        return buildType;
    }
    public void setBuildType(BuildType buildType) {
        this.buildType = buildType;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getBuildDate() {
        return buildDate;
    }
    public void setBuildDate(String buildDate) {
        this.buildDate = buildDate;
    }
    public String getArtifactId() {
        return artifactId;
    }
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
