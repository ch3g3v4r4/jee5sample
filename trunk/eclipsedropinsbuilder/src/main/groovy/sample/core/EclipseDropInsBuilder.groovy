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

@Component
class EclipseDropInsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(EclipseDropInsBuilder.class);

//    @PostConstruct
//    public void init() {
//        LOGGER.debug("EclipseDropInsBuilder.init");
//    }
    public void build() {

        // Eclipse JEE zip file for x86_64
        def platformUrl = "http://mirror-fpt-telecom.fpt.net/eclipse/technology/epp/downloads/release/helios/SR2/eclipse-jee-helios-SR2-win32-x86_64.zip"
        // working firectory
        def workDir = new File("/tmp")

        // Download and unzip director app + JEE IDE app to working directory
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

        // AnyEditTools
        url = "http://andrei.gmxhome.de/eclipse/"
        featureId = "AnyEditTools.feature.group"
        pluginTargetDir =  new File(pluginsHomeDir, "AnyEditTools")
        copyPlugin(ant, url, featureId, originalEclipseDir, eclipseDir, pluginTargetDir)

    }

    void copyPlugin(ant, url, featureId, originalEclipseDir, eclipseDir, pluginTargetDir) {

        def profile = "epp.package.jee"
        def isWindows = (System.getProperty("os.name").indexOf("Windows") != -1);
        def javaPath = System.getProperty("java.home") + "/bin/java" + (isWindows ? ".exe" : "")
        def directorCmd = new CommandLine(javaPath)
        ant.echo (message: "Will install " + featureId);
        ant.delete (dir: eclipseDir)
        ant.copy(todir: eclipseDir) {fileset(dir: originalEclipseDir)}
        def launcherPath = FileUtils.listFiles(new File(eclipseDir, "plugins"), new WildcardFileFilter("org.eclipse.equinox.launcher_*.jar"), FalseFileFilter.FALSE).get(0).absolutePath
        directorCmd.addArgument("-jar").addArgument(launcherPath)
        directorCmd.addArgument("-application").addArgument("org.eclipse.equinox.p2.director")
        directorCmd.addArgument("-profile").addArgument(profile)
        directorCmd.addArgument("-repository").addArgument(url)
        directorCmd.addArgument("-installIU").addArgument(featureId)
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
