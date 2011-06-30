
package sample.core;

import java.security.MessageDigest;
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FalseFileFilter
import org.apache.commons.io.filefilter.NameFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import org.apache.commons.io.filefilter.WildcardFileFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


class EclipseDropInsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(EclipseDropInsBuilder.class);

    public void build(Eclipse config) {
        def ant = new AntBuilder()

        def workDir = new File(config.workDir)
        ant.mkdir (dir: workDir)

        def platformUrl = config.url
        def profile = config.profile

        def zipFileName = platformUrl.substring(platformUrl.lastIndexOf('/') + 1)
        def zipFileNameNoExt = zipFileName.substring(0, zipFileName.lastIndexOf('.'))
        def platformEclipseDir = new File(workDir, zipFileNameNoExt + "/eclipse")
        if (!platformEclipseDir.exists()) {
             ant.get (src: platformUrl, dest: workDir, usetimestamp: true, verbose: true)
             ant.unzip (dest: new File(workDir, zipFileNameNoExt)) { fileset(dir: workDir){ include (name: platformUrl.substring(platformUrl.lastIndexOf('/') + 1))} }
        }

        def eclipseDir = new File(workDir, "eclipse")
        ant.delete (dir: eclipseDir)
        ant.copy(todir: eclipseDir) {fileset(dir: platformEclipseDir)}
        def snapshotDir = new File(workDir, "snapshot")
        ant.delete (dir: snapshotDir)
        Map<Plugin, File> cachedPlugins = new Hashtable<Plugin, File>();
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(platformUrl.getBytes("UTF-8"))
        for (Plugin plugin : config.plugins) {
            // try the cache first
            List<String> values = new ArrayList<String>();
            values.add(plugin.url)
            for (String s : plugin.updateSites) values.add(s)
            for (String s : plugin.featureIds) values.add(s)
            for (String s : values) {
                if (s != null) md.update(s.getBytes("UTF-8"))
            }
            String id = Hex.encodeHexString(md.clone().digest())
            File cachedPlugin = new File(workDir, id);

            cachedPlugins.put(plugin, cachedPlugin)
            if (cachedPlugin.exists()) {
                // 1. create a snapshot
                ant.copy(todir: snapshotDir) {fileset(dir: eclipseDir)}
                // find cached files for plugin, use them
                ant.copy(todir: eclipseDir) {fileset(dir: cachedPlugin)}
            } else {
                // 1. create a snapshot
                ant.copy(todir: snapshotDir) {fileset(dir: eclipseDir)}
                // 2. install
                if (plugin.url != null) {
                    installFromUrl(eclipseDir, workDir, ant, profile, plugin.url, plugin.featureIds)
                } else {
                    installFromUpdateSite(eclipseDir, ant, profile, plugin.updateSites, plugin.featureIds)
                }

                // 3. compare with the snapshot and save new files to cachedPlugin folder
                ant.copy(todir: new File(cachedPlugin, "features")) {
                        fileset(dir: new File(eclipseDir, "features"), includes: "**/*") {
                            present (present: "srconly", targetdir: new File(snapshotDir, "features"))
                        }
                }
                ant.copy(todir: new File(cachedPlugin, "plugins")) {
                    fileset(dir: new File(eclipseDir, "plugins"), includes: "**/*") {
                        present (present: "srconly", targetdir: new File(snapshotDir, "plugins"))
                    }
                }
            }
        }

        ant.delete (dir: eclipseDir)
        ant.copy(todir: eclipseDir) {fileset(dir: platformEclipseDir)}
        for (Plugin plugin : config.plugins) {
            File cachedPlugin = cachedPlugins.get(plugin);
            if (plugin.dropinsName != null) {
                ant.copy(todir: new File(eclipseDir, "dropins/" + plugin.dropinsName)) {fileset(dir: cachedPlugin)}
            } else {
                ant.copy(todir: eclipseDir) {fileset(dir: cachedPlugin)}
            }
        }

        // 4. Increase memory settings
        ant.replaceregexp (file: new File(eclipseDir, "eclipse.ini"),  match:"^\\-Xmx[0-9]+m", replace:"-Xmx800m", byline:"true");
        ant.replaceregexp (file: new File(eclipseDir, "eclipse.ini"),  match:"^[0-9]+m", replace:"400m", byline:"true");
        ant.replaceregexp (file: new File(eclipseDir, "eclipse.ini"),  match:"^[0-9]+M", replace:"400M", byline:"true");

        println "Congratulations! Your Eclipse IDE is ready. Location: " + eclipseDir.absolutePath

    }
    void installFromUpdateSite(eclipseDir, ant, profile, updateSites, featureIds) {
        def isWindows = (System.getProperty("os.name").indexOf("Windows") != -1);
        def javaPath = System.getProperty("java.home") + "/bin/java" + (isWindows ? ".exe" : "")
        def directorCmd = new CommandLine(javaPath)

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

    }

    void installFromUrl(eclipseDir, workDir, ant, profile, url, featureIds) {
        def fileName = url.substring(url.lastIndexOf('/') + 1)
        def downloadedFile = new File(workDir, fileName);
        if (!downloadedFile.exists()) {
            ant.get (src: url, dest: downloadedFile, usetimestamp: true, verbose: true)
        }
        List<String> names = new ArrayList<String>();
        ZipFile zf = new ZipFile(downloadedFile);
        for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
            String zipEntryName = ((ZipEntry)entries.nextElement()).getName();
            names.add(zipEntryName);
        }
        println "zip file content: " + names
        if (names.contains("plugin.xml") || names.contains("META-INF/")) {
            // is simple jar contains plugin
            ant.copy (file: downloadedFile, todir: new File(eclipseDir, "plugins"))
        } else if (names.contains("site.xml") || names.contains("content.jar") || names.contains("artifacts.jar")) {
            // is archive update site
            installFromUpdateSite(eclipseDir, ant, profile, ["jar:" + downloadedFile.toURI().toURL().toString() + "!/"], featureIds)
        } else {
            // is zipped plugins
            def tempDir = new File(workDir, downloadedFile.name + new Date().getTime())
            ant.unzip (src: downloadedFile, dest: tempDir)
            try {
                if (names.contains("eclipse/") || names.contains("plugins/")) {
                    ant.copy(todir: eclipseDir){
                        fileset(dir: names.contains("eclipse/") ? new File(tempDir, "eclipse") : tempDir)
                    }
                } else {
                    Collection files = FileUtils.listFiles(tempDir, new NameFileFilter("plugin.xml"), TrueFileFilter.INSTANCE);
                    if (!files.isEmpty()) {
                        File file = files.iterator().next();
                        ant.copy(todir: new File(eclipseDir, "plugins")){
                            fileset(dir: file.getParentFile().getParentFile())
                        }
                    }
                }
            } finally {
                ant.delete(dir: tempDir)
            }
        }
    }
}
