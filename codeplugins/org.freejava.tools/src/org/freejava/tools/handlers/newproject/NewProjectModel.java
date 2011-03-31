package org.freejava.tools.handlers.newproject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class NewProjectModel {

    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    private String targetDirectory;
    private String groupId;
    private String artifactId;
    private boolean webProject;
    private boolean checkstyleSupport;
    private boolean findBugsSupport;
    private boolean springSupport;
    private boolean hibernateSupport;

    public String getTargetDirectory() {
        return targetDirectory;
    }
    public void setTargetDirectory(String targetDirectory) {
        changeSupport.firePropertyChange("targetDirectory", this.targetDirectory, this.targetDirectory = targetDirectory);
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        changeSupport.firePropertyChange("groupId", this.groupId, this.groupId = groupId);
    }
    public String getArtifactId() {
        return artifactId;
    }
    public void setArtifactId(String artifactId) {
        changeSupport.firePropertyChange("artifactId", this.artifactId, this.artifactId = artifactId);
    }
    public boolean isWebProject() {
        return webProject;
    }
    public void setWebProject(boolean webProject) {
        changeSupport.firePropertyChange("webProject", this.webProject, this.webProject = webProject);
    }
    public boolean isCheckstyleSupport() {
        return checkstyleSupport;
    }
    public void setCheckstyleSupport(boolean checkstyleSupport) {
        changeSupport.firePropertyChange("checkstyleSupport", this.checkstyleSupport, this.checkstyleSupport = checkstyleSupport);
    }
    public boolean isFindBugsSupport() {
        return findBugsSupport;
    }
    public void setFindBugsSupport(boolean findBugsSupport) {
        changeSupport.firePropertyChange("findBugsSupport", this.findBugsSupport, this.findBugsSupport = findBugsSupport);
    }
    public boolean isSpringSupport() {
        return springSupport;
    }
    public void setSpringSupport(boolean springSupport) {
        changeSupport.firePropertyChange("springSupport", this.springSupport, this.springSupport = springSupport);
    }
    public boolean isHibernateSupport() {
        return hibernateSupport;
    }
    public void setHibernateSupport(boolean hibernateSupport) {
        changeSupport.firePropertyChange("hibernateSupport", this.hibernateSupport, this.hibernateSupport = hibernateSupport);
    }
}
