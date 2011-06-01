package sample.core;

import java.util.List;

public class Plugin {
    private String folderName;
    private String updateSite;
    private List<String> featureIds;

    public String getUpdateSite() {
        return updateSite;
    }
    public void setUpdateSite(String updateSite) {
        this.updateSite = updateSite;
    }
    public List<String> getFeatureIds() {
        return featureIds;
    }
    public void setFeatureIds(List<String> featureIds) {
        this.featureIds = featureIds;
    }
    public String getFolderName() {
        return folderName;
    }
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


}
