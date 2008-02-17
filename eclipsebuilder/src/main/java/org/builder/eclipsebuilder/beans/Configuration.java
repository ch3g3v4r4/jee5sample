package org.builder.eclipsebuilder.beans;


public class Configuration {

	public static enum BuildType { RELEASE, STABLE, INTEGRATION, NIGHTLY };

	private BuildType buildType;
	private String cacheDir;
	private String eclipseDir;

	public BuildType getBuildType() {
		return buildType;
	}

	public void setBuildType(BuildType buildType) {
		this.buildType = buildType;
	}

	public String getCacheDir() {
		return cacheDir;
	}

	public void setCacheDir(String cacheDir) {
		this.cacheDir = cacheDir;
	}

	public String getEclipseDir() {
		return eclipseDir;
	}

	public void setEclipseDir(String eclipseDir) {
		this.eclipseDir = eclipseDir;
	}

}
