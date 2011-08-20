package sample.core;

import java.util.List;

public class Profile {
    private String profileName;
    private List<String> dropinsNames;

    public String getProfileName() {
        return profileName;
    }
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
    public List<String> getDropinsNames() {
        return dropinsNames;
    }
    public void setDropinsNames(List<String> dropinsNames) {
        this.dropinsNames = dropinsNames;
    }
}
