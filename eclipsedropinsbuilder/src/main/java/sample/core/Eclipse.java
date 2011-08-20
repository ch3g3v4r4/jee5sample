package sample.core;

import java.util.List;

public class Eclipse {
    private String workDir;
    private String url;
    private String profile;
    private List<Plugin> plugins;
    private List<Profile> profiles;

    public String getWorkDir() {
        return workDir;
    }
    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfile() {
        return profile;
    }
    public void setProfile(String profile) {
        this.profile = profile;
    }
    public List<Plugin> getPlugins() {
        return plugins;
    }
    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }
    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
}
