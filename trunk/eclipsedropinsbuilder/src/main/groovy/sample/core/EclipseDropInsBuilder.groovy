package sample.core;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import sample.startup.Main;


class EclipseDropInsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(EclipseDropInsBuilder.class);

    public void build(Eclipse config) {

        def platformUrl = config.url
        def workDir = new File(config.workDir)

        def ant = new AntBuilder()
        ant.mkdir (dir: workDir)
        ant.get (src: platformUrl, dest: workDir, usetimestamp: true, verbose: true)
        ant.unzip (dest: new File(workDir, "original")) { fileset(dir: workDir){ include (name: "*.zip")} }

        // Define variables
        def url = ""
        def featureId = ""
        def originalEclipseDir = new File(workDir, "original/eclipse")
        def eclipseDir = new File(workDir, "eclipse")
        def pluginsHomeDir =  new File(workDir, "dropins")
        def pluginTargetDir = ""

        for (Plugin plugin : config.plugins) {
            copyPlugin(ant, plugin.updateSite, plugin.featureIds, originalEclipseDir, eclipseDir, new File(pluginsHomeDir, plugin.folderName))
        }

    }

    void copyPlugin(ant, url, featureIds, originalEclipseDir, eclipseDir, pluginTargetDir) {
        if (!pluginTargetDir.exists()) {
            def profile = "epp.package.jee"
            def isWindows = (System.getProperty("os.name").indexOf("Windows") != -1);
            def javaPath = System.getProperty("java.home") + "/bin/java" + (isWindows ? ".exe" : "")
            def directorCmd = new CommandLine(javaPath)
            ant.delete (dir: eclipseDir)
            ant.copy(todir: eclipseDir) {fileset(dir: originalEclipseDir)}
            def launcherPath = FileUtils.listFiles(new File(eclipseDir, "plugins"), new WildcardFileFilter("org.eclipse.equinox.launcher_*.jar"), FalseFileFilter.FALSE).get(0).absolutePath
            directorCmd.addArgument("-jar").addArgument(launcherPath)
            directorCmd.addArgument("-application").addArgument("org.eclipse.equinox.p2.director")
            directorCmd.addArgument("-profile").addArgument(profile)
            directorCmd.addArgument("-repository").addArgument(url)
            for (String featureId : featureIds) {
            ant.echo (message: "Will install " + featureId);
                directorCmd.addArgument("-installIU").addArgument(featureId)
            }
            def executor = new DefaultExecutor();
            executor.setExitValue(0);
            def exitValue = executor.execute(directorCmd);
            ant.copy(todir: new File(pluginTargetDir, "features")) {
                fileset(dir: new File(eclipseDir, "features"), includes: "**/*") {
                    present (present: "srconly", targetdir: new File(originalEclipseDir, "features"))
                }
            }
            ant.copy(todir: new File(pluginTargetDir, "plugins")) {
                fileset(dir: new File(eclipseDir, "plugins"), includes: "**/*") {
                    present (present: "srconly", targetdir: new File(originalEclipseDir, "plugins"))
                }
            }
        }
    }
}
