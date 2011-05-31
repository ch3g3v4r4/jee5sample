package sample.core;

import javax.annotation.PostConstruct;

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
        def targetDir = new File("/tmp").absolutePath

        // Download and unzip director app + JEE IDE app to working directory
        def ant = new AntBuilder()
        ant.mkdir (dir: targetDir)
        ant.get (src: platformUrl, dest: targetDir, usetimestamp: true, verbose: true)
        ant.unzip (dest: targetDir) { fileset(dir: targetDir){ include (name: "*.zip")} }


        // Build director command
        ant.copy(todir: targetDir + "/director") {fileset(dir:targetDir + "/eclipse")}
        def isWindows = (System.getProperty("os.name").indexOf("Windows") != -1);
        def directorCmd = new CommandLine(targetDir + "/director/eclipse" + (isWindows ? "c.exe" : ""))
        def javaPath = System.getProperty("java.home") + "/bin/java" + (isWindows ? "w.exe" : "")
        directorCmd.addArgument("-vm").addArgument(javaPath)
        directorCmd.addArgument("-application").addArgument("org.eclipse.equinox.p2.director")
        directorCmd.addArgument("-destination").addArgument(targetDir + "/target")
        directorCmd.addArgument("-profile").addArgument("epp.package.jee")


        def url = "http://andrei.gmxhome.de/eclipse/"
        def featureId = "AnyEditTools.feature.group"
        def cmd = new CommandLine(directorCmd)
        ant.delete (dir: targetDir + "/target")
        ant.copy(todir: targetDir + "/target") {fileset(dir:targetDir + "/eclipse")}
        cmd.addArgument("-repository").addArgument(url)
        cmd.addArgument("-installIU").addArgument(featureId)
        def executor = new DefaultExecutor();
        executor.setExitValue(0);
        def exitValue = executor.execute(cmd);

    }
}
