package sample.core;

import java.util.List;

public class Launch {
    private String launchName;
    private List<String> dropinsNames;

    public String getLaunchName() {
        return launchName;
    }
    public void setLaunchName(String launchName) {
        this.launchName = launchName;
    }
    public List<String> getDropinsNames() {
        return dropinsNames;
    }
    public void setDropinsNames(List<String> dropinsNames) {
        this.dropinsNames = dropinsNames;
    }
}
