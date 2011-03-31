package org.freejava.tools.handlers.usefulPlugins;

import java.util.List;

public class Function {
    private String name;
    private boolean defaultChecked;
    private String updateSite;
    private List<String> featureIds;

    public Function(String name, boolean defaultChecked, String updateSite,
            List<String> featureIds) {
        super();
        this.name = name;
        this.defaultChecked = defaultChecked;
        this.updateSite = updateSite;
        this.featureIds = featureIds;
    }

    public String getName() {
        return name;
    }

    public boolean isDefaultChecked() {
        return defaultChecked;
    }

    public String getUpdateSite() {
        return updateSite;
    }

    public List<String> getFeatureIds() {
        return featureIds;
    }

}
