package sample.core;

import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sample.startup.Main;


class EclipseDropInsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(EclipseDropInsBuilder.class);

    public void build(Eclipse config) {

        def workDir = new File(config.workDir)
        def platformUrl = config.url
        def profile = config.profile

        def ant = new AntBuilder()
        ant.mkdir (dir: workDir)
        ant.get (src: platformUrl, dest: workDir, usetimestamp: true, verbose: true)
        ant.unzip (dest: new File(workDir, "original")) { fileset(dir: workDir){ include (name: platformUrl.substring(platformUrl.lastIndexOf('/') + 1))} }

        // Define variables
        def originalEclipseDir = new File(workDir, "original/eclipse")
        def eclipseDir = new File(workDir, "eclipse")
        def pluginsHomeDir =  new File(workDir, "dropins")

        // First install plugins which can be put into dropins/ (i.e has plugin.dropinsName != null )
        for (Plugin plugin : config.plugins) {
            if (plugin.dropinsName != null) {
                def pluginTargetDir = new File(pluginsHomeDir, plugin.dropinsName)
                if (plugin.updateSites != null && !plugin.updateSites.empty) {
                    copyPluginFromUpdateSite(ant, profile, plugin.updateSites, plugin.featureIds, originalEclipseDir, eclipseDir, pluginTargetDir)
                } else {
                    copyPluginFromUrl(workDir, ant, profile, plugin.url, plugin.featureIds, originalEclipseDir, eclipseDir, pluginTargetDir)
                }
            }
        }

        // Build eclipse with dropins
        ant.delete (dir: eclipseDir)
        ant.copy(todir: eclipseDir) {fileset(dir: originalEclipseDir)}
        ant.copy(todir: new File(eclipseDir, "dropins")) {fileset(dir: pluginsHomeDir)}

        // Second install remaining plugins which must be put into core eclipse (i.e has plugin.dropinsName == null )
        for (Plugin plugin : config.plugins) {
            if (plugin.dropinsName == null) {
                def pluginTargetDir = eclipseDir
                if (plugin.updateSites != null && !plugin.updateSites.empty) {
                    copyPluginFromUpdateSite(ant, profile, plugin.updateSites, plugin.featureIds, originalEclipseDir, eclipseDir, pluginTargetDir)
                } else {
                    copyPluginFromUrl(workDir, ant, profile, plugin.url, plugin.featureIds, originalEclipseDir, eclipseDir, pluginTargetDir)
                }
            }
        }

        println "Congratulations! Your Eclipse IDE is ready. Location: " + eclipseDir.absolutePath

    }

    void copyPluginFromUrl(workDir, ant, profile, url, featureIds, originalEclipseDir, eclipseDir, pluginTargetDir) {
        if (!pluginTargetDir.exists() || pluginTargetDir.equals(eclipseDir)) {
            def fileName = url.substring(url.lastIndexOf('/') + 1)
            def downloadedFile = new File(workDir, fileName);
            ant.get (src: url, dest: downloadedFile, usetimestamp: true, verbose: true)
            List<String> names = new ArrayList<String>();
            ZipFile zf = new ZipFile(downloadedFile);
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
                String zipEntryName = ((ZipEntry)entries.nextElement()).getName();
                names.add(zipEntryName);
            }
            println "zip file content: " + names
            if (names.contains("plugin.xml")) {
                // is simple jar contains plugin
                ant.copy (file: downloadedFile, todir: new File(pluginTargetDir, "plugins"))
            } else if (names.contains("site.xml")) {
                // is archive update site
                copyPluginFromUpdateSite(ant, profile, ["jar:" + downloadedFile.toURI().toURL().toString() + "!"], featureIds, originalEclipseDir, eclipseDir, pluginTargetDir)
            } else if (names.contains("eclipse/")) {
                // is zipped plugins
                def tempDir = new File(workDir, downloadedFile.name + new Date().getTime())
                ant.unzip (src: downloadedFile, dest: tempDir)
                ant.copy(todir: pluginTargetDir){
                    fileset(dir: new File(tempDir, "eclipse"))
                }
                ant.delete(dir: tempDir)

            }
        }
    }

    void copyPluginFromUpdateSite(ant, profile, updateSites, featureIds, originalEclipseDir, eclipseDir, pluginTargetDir) {
        if (!pluginTargetDir.exists() || pluginTargetDir.equals(eclipseDir)) {
            def isWindows = (System.getProperty("os.name").indexOf("Windows") != -1);
            def javaPath = System.getProperty("java.home") + "/bin/java" + (isWindows ? ".exe" : "")
            def directorCmd = new CommandLine(javaPath)
            if (!pluginTargetDir.equals(eclipseDir)) {
                ant.delete (dir: eclipseDir)
                ant.copy(todir: eclipseDir) {fileset(dir: originalEclipseDir)}
            }
            def launcherPath = FileUtils.listFiles(new File(eclipseDir, "plugins"), new WildcardFileFilter("org.eclipse.equinox.launcher_*.jar"), FalseFileFilter.FALSE).get(0).absolutePath
            directorCmd.addArgument("-jar").addArgument(launcherPath)
            directorCmd.addArgument("-application").addArgument("org.eclipse.equinox.p2.director")
            directorCmd.addArgument("-profile").addArgument(profile)
            for (String updateSite : updateSites) {
                directorCmd.addArgument("-repository").addArgument(updateSite)
            }

            for (String featureId : featureIds) {
                ant.echo (message: "Will install " + featureId);
                directorCmd.addArgument("-installIU").addArgument(featureId)
            }
            directorCmd.addArgument("-consoleLog")
            def executor = new DefaultExecutor();
            executor.setExitValue(0);
            println directorCmd
            def exitValue = executor.execute(directorCmd);
            if (!pluginTargetDir.equals(eclipseDir)) {
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
}
