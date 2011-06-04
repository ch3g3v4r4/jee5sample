package sample.core;

import java.util.List;

public class Plugin {
    private String name;
    private List<String> updateSites;
    private List<String> featureIds;
    private String url;
    private String dropin;


    public List<String> getUpdateSites() {
        return updateSites;
    }
    public void setUpdateSites(List<String> updateSites) {
        this.updateSites = updateSites;
    }
    public List<String> getFeatureIds() {
        return featureIds;
    }
    public void setFeatureIds(List<String> featureIds) {
        this.featureIds = featureIds;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDropin() {
        return dropin;
    }
    public void setDropin(String dropin) {
        this.dropin = dropin;
    }




}
