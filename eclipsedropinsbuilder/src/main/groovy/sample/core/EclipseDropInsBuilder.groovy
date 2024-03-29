package sample.core;

import java.security.MessageDigest;
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.Comparator

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

    public void build(Eclipse config) throws Exception {
        def ant = new AntBuilder()

        def workDir = new File(config.workDir)
        ant.mkdir (dir: workDir)

        def platformUrl = config.url
        def profile = config.profile

        def zipFileName = platformUrl.substring(platformUrl.lastIndexOf('/') + 1)
        def zipFileNameNoExt = zipFileName + "_unzipped"
        def platformEclipseDir = new File(workDir, zipFileNameNoExt + "/eclipse")
        if (!platformEclipseDir.exists()) {
            ant.get (src: platformUrl, dest: workDir, usetimestamp: false, skipexisting: true, verbose: true)
            try {

                FileInputStream fin = new FileInputStream(new File(workDir, zipFileName))
                byte[] bytes = new byte[2]
                fin.read(bytes)
                fin.close()

                if (bytes[0] == 0x50 && bytes[1] == 0x4b) {
                    // 'PK' : zip
                    ant.unzip (dest: new File(workDir, zipFileNameNoExt), overwrite:"false") { fileset(dir: workDir){ include (name: zipFileName) } }
                } else {
                    ant.untar(dest:new File(workDir, zipFileNameNoExt), compression:"gzip", overwrite:"false") { fileset(dir: workDir){ include (name: zipFileName) } }
                }
                new File(workDir, zipFileNameNoExt).eachFileRecurse() { file ->
                    if (file.name.startsWith("eclipsec")) {
                        platformEclipseDir = file.parentFile
                    }
                }
            } catch (Exception e) {
                ant.delete(file: new File(workDir, zipFileName))
                throw e;
            }
        }
        def javaDir =  config.javaDir

        def eclipseDir = new File(workDir, "eclipse")
        ant.delete (dir: eclipseDir)
        ant.copy(todir: eclipseDir) {fileset(dir: platformEclipseDir)}

        if (config.jdkUrl != null) {
            File jdkDir = new JdkDownloader().installJDK(workDir, config.jdkUrl, config.jdkSrcUrl)
            ant.copy(todir: new File(eclipseDir, "jre")) {fileset(dir: jdkDir)}
            if (javaDir == null) {
                javaDir = new File(jdkDir, "bin/javaw.exe").absolutePath
            }
        }

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
            id = plugin.name + "_" + id.substring(0, 5)
            File cachedPlugin = new File(new File(workDir,"cached"), id);
            println "Install plugin ${plugin.name} into ${id}"
            cachedPlugins.put(plugin, cachedPlugin)
            if (cachedPlugin.exists()) {
                // 1. create a snapshot
                ant.copy(todir: snapshotDir) {fileset(dir: eclipseDir)}
                // find cached files for plugin, use them
                ant.copy(todir: eclipseDir, overwrite: true) {fileset(dir: cachedPlugin)}
            } else {
                // 1. create a snapshot
                ant.copy(todir: snapshotDir) {fileset(dir: eclipseDir)}
                // 2. install
                if (plugin.url == 'http://downloads.zend.com/pdt/') {
                    installPHPFromUrl(eclipseDir, workDir, ant, profile, plugin.url, plugin.featureIds)
                } else if (plugin.url != null) {
                    installFromUrl(javaDir, eclipseDir, workDir, ant, profile, plugin.url, plugin.updateSites, plugin.featureIds)
                } else {
                    installFromUpdateSite(javaDir, eclipseDir, ant, profile, plugin.updateSites, plugin.featureIds)
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
                ant.copy(todir: new File(cachedPlugin, "configuration")) {
                    fileset(dir: new File(eclipseDir, "configuration"), includes: "org.eclipse.equinox.simpleconfigurator/**, org.eclipse.equinox.source/**, org.eclipse.update/**, config.ini")
                }
                // 4. test new jar/zip files broken or not
                try {
                    ant.delete (dir: new File(workDir, "unzipped"))
                    ant.unzip(dest: new File(workDir, "unzipped")) {
                        fileset(dir: cachedPlugin, includes: "**/*.jar **/*.zip")
                    }
                } catch (Exception e) {
                    ant.delete (dir: cachedPlugin)
                    throw e;
                }
            }
        }

        ant.delete (dir: eclipseDir)
        ant.copy(todir: eclipseDir) {fileset(dir: platformEclipseDir)}
        if (config.jdkUrl != null) {
            File jdkDir = new JdkDownloader().installJDK(workDir, config.jdkUrl, config.jdkSrcUrl)
            ant.copy(todir: new File(eclipseDir, "jre")) {fileset(dir: jdkDir)}
        }

        for (Plugin plugin : config.plugins) {
            File cachedPlugin = cachedPlugins.get(plugin);
            if (!plugin.isEmbeded()) {
                ant.copy(todir: new File(eclipseDir, "dropins/" + plugin.name)) {fileset(dir: cachedPlugin, excludes: "configuration/**")}
            } else {
                ant.copy(todir: eclipseDir) {fileset(dir: cachedPlugin, excludes: "configuration/**")}
            }
            ant.copy(todir: eclipseDir, overwrite: true) {fileset(dir: cachedPlugin, includes: "configuration/**/config.ini configuration/**/bundles.info configuration/**/source.info configuration/**/platform.xml")}
        }

        // 4. Increase memory settings
        ant.replaceregexp (match:"^\\-Xmx[0-9]+m", replace:"-Xmx800m", byline:"true"){ fileset(dir:eclipseDir, includes:"**/eclipse.ini") }
        ant.replaceregexp (match:"^[0-9]+m", replace:"400m", byline:"true") { fileset(dir:eclipseDir, includes:"**/eclipse.ini") }
        ant.replaceregexp (match:"^[0-9]+M", replace:"400M", byline:"true") { fileset(dir:eclipseDir, includes:"**/eclipse.ini") }

        // 5. Remove conflicting key binding from Aptana plugin (if any)
        def files = FileUtils.listFiles(new File(eclipseDir, "dropins"), new WildcardFileFilter("com.aptana.editor.common_*.jar"), TrueFileFilter.INSTANCE);
        if (!files.isEmpty()) {
            ant.delete(file: new File(workDir, "plugin.xml"))
            ant.unzip (src: files.get(0), dest: workDir){
                patternset {include (name:"plugin.xml")}
            }
            ant.replaceregexp (file: new File(workDir, "plugin.xml"),  match:"<key[^<]+CTRL\\+SHIFT\\+R[^<]+</key>", replace:"", flags:"s");
            ant.jar(destfile:files.get(0), basedir:workDir,includes:"plugin.xml",update:true)
        }
        files = FileUtils.listFiles(new File(eclipseDir, "dropins"), new WildcardFileFilter("com.aptana.syncing.ui_*.jar"), TrueFileFilter.INSTANCE);
        if (!files.isEmpty()) {
            ant.delete(file: new File(workDir, "plugin.xml"))
            ant.unzip (src: files.get(0), dest: workDir){
                patternset {include (name:"plugin.xml")}
            }
            ant.replaceregexp (file: new File(workDir, "plugin.xml"),  match:"<key[^<]+M1\\+M2\\+U[^<]+</key>", replace:"", flags:"s");
            ant.jar(destfile:files.get(0), basedir:workDir,includes:"plugin.xml",update:true)
        }

        // 6. set execution permission for eclipse binaries
        ant.chmod(perm:"uog+x") { fileset(dir:eclipseDir, includes:"**/eclipse, **/eclipse.exe, **/eclipsec.exe, **/eclipsec")}

    }

    void installFromUpdateSite(javaDir, eclipseDir, ant, profile, updateSites, featureIds) {
        def isWindows = (System.getProperty("os.name").indexOf("Windows") != -1);
        def javaPath = System.getProperty("java.home") + "/bin/java" + (isWindows ? ".exe" : "")
        if (javaDir != null && !javaDir.equals("")) javaPath = javaDir

        def directorCmd = new CommandLine(javaPath)

        def launcherPath = FileUtils.listFiles(new File(eclipseDir, "plugins"), new WildcardFileFilter("org.eclipse.equinox.launcher_*.jar"), FalseFileFilter.FALSE).get(0).absolutePath
        directorCmd.addArgument("-jar").addArgument(launcherPath)
        directorCmd.addArgument("-application").addArgument("org.eclipse.equinox.p2.director")
        directorCmd.addArgument("-clean")
        directorCmd.addArgument("-profile").addArgument(profile)
        for (String updateSite : updateSites) {
            directorCmd.addArgument("-repository").addArgument(updateSite)
        }

        for (String featureId : featureIds) {
            ant.echo (message: "Will install " + featureId);
            directorCmd.addArgument("-installIU").addArgument(featureId)
        }
        directorCmd.addArgument("-destination")
        directorCmd.addArgument("\"" + eclipseDir.absolutePath + "\"")
        directorCmd.addArgument("-consoleLog")

        // Must be last args
        directorCmd.addArgument("-vmargs")
        directorCmd.addArgument("-Declipse.p2.mirrors=false")

        def executor = new DefaultExecutor();
        executor.setExitValue(0);
        println directorCmd
        def exitValue = executor.execute(directorCmd);

    }

    void installFromUrl(javaDir, eclipseDir, workDir, ant, profile, url, updateSites, featureIds) {
        def fileName = url.substring(url.lastIndexOf('/') + 1)
        def downloadedFile = new File(workDir, fileName);
        if (!downloadedFile.exists()) {
            ant.get (src: url, dest: downloadedFile, usetimestamp: true, verbose: true)
        }
        List<String> names = new ArrayList<String>();
        ZipFile zf;
        try {
            zf = new ZipFile(downloadedFile);
            for (Enumeration entries = zf.entries(); entries.hasMoreElements();) {
                String zipEntryName = ((ZipEntry)entries.nextElement()).getName();
                names.add(zipEntryName);
            }
        } catch (Exception e) {
            ant.delete(file: downloadedFile);
            throw e;
        }
        println "zip file content: " + names
        if (names.contains("plugin.xml") || names.contains("META-INF/")) {
            // is simple jar contains plugin
            ant.copy (file: downloadedFile, todir: new File(eclipseDir, "plugins"))
        } else if (names.contains("site.xml") || names.contains("content.jar") || names.contains("artifacts.jar")) {
            // is archive update site
            def updateSites2 = new ArrayList<String>();
            updateSites2.add("jar:" + downloadedFile.toURI().toURL().toString() + "!/")
            if (updateSites != null) updateSites2.addAll(updateSites)
            installFromUpdateSite(javaDir, eclipseDir, ant, profile, updateSites2, featureIds)
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
                        def fileList = []
                        fileList.addAll(files);
                        Collections.sort(fileList, new Comparator<File>(){
                                    public int compare(File f1, File f2) {
                                        return f1.getAbsolutePath().length() - f2.getAbsolutePath().length();
                                    }

                                });
                        File file = fileList.iterator().next();
                        if (file.getParentFile().getParentFile().getName().equals("plugins")) {
                            ant.copy(todir: eclipseDir){
                                fileset(dir: file.getParentFile().getParentFile().getParentFile())
                            }
                        } else {
                            ant.copy(todir: new File(eclipseDir, "plugins")){
                                fileset(dir: file.getParentFile().getParentFile())
                            }
                        }
                    }
                }
            } finally {
                ant.delete(dir: tempDir)
            }
        }
    }
    void installPHPFromUrl(eclipseDir, workDir, ant, profile, url, featureIds) {

        def downloadedFile = new File(workDir, "org.zend.php.debug_feature_5.3.18.v20110322.jar");
        if (!downloadedFile.exists()) {
            ant.get (src: "http://downloads.zend.com/pdt/features/org.zend.php.debug_feature_5.3.18.v20110322.jar",
            dest: downloadedFile, usetimestamp: true, verbose: true)
        }
        ant.unzip (src: downloadedFile, dest: new File(eclipseDir, "features/" + downloadedFile.name))
        downloadedFile = new File(workDir, "org.zend.php.debug.debugger.win32.x86_5.3.18.v20110322.jar");
        if (!downloadedFile.exists()) {
            ant.get (src: "http://downloads.zend.com/pdt/plugins/org.zend.php.debug.debugger.win32.x86_5.3.18.v20110322.jar",
            dest: downloadedFile, usetimestamp: true, verbose: true)
        }
        ant.unzip (src: downloadedFile, dest: new File(eclipseDir, "plugins/" + downloadedFile.name))
        downloadedFile = new File(workDir, "org.zend.php.debug.debugger_5.3.18.v20110322.jar");
        if (!downloadedFile.exists()) {
            ant.get (src: "http://downloads.zend.com/pdt/plugins/org.zend.php.debug.debugger_5.3.18.v20110322.jar",
            dest: downloadedFile, usetimestamp: true, verbose: true)
        }
        ant.unzip (src: downloadedFile, dest: new File(eclipseDir, "plugins/" + downloadedFile.name))
    }
}
