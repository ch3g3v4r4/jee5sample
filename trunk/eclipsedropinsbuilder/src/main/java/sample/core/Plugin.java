package sample.core;

import java.util.List;

public class Plugin {
    private String dropinsName;
    private List<String> updateSites;
    private List<String> featureIds;
    private String url;


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
    public String getDropinsName() {
        return dropinsName;
    }
    public void setDropinsName(String dropinsName) {
        this.dropinsName = dropinsName;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }



}
