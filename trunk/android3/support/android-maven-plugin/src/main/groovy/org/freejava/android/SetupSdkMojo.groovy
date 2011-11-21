package org.freejava.android

import org.codehaus.gmaven.mojo.GroovyMojo
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.io.FilenameUtils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;

@MojoGoal("setupsdk")
class SetupSdkMojo extends GroovyMojo {

	public String message

	void execute() {
		println "${message}"

		int r = new POSIX().setenv("ANDROID_HOME", "1", 0)
println System.getenv("ANDROID_HOME")

		def filter = 'system-image,platform-tool,android-10'
		def url = "http://dl.google.com/android/android-sdk_r15-windows.zip"
		if (SystemUtils.IS_OS_LINUX ) {
			url = "http://dl.google.com/android/android-sdk_r15-linux.tgz"
		}

		if (System.getenv("ANDROID_HOME") == null && project.properties["android.sdk.path"] == null) {

			println "=================================================================="
			println "No value found for ANDROID_HOME environment variable. Will install latest ANDROID SDK from Internet."

			def filename = FilenameUtils.getName(url)
			def name = FilenameUtils.getBaseName(url)
			def tempdir = System.getProperty("java.io.tmpdir")
			def workdir = new File(System.getProperty("java.io.tmpdir"), name)


			ant.mkdir(dir:workdir)

			String[] files = workdir.list( new AndFileFilter(new WildcardFileFilter("android-sdk*"), new DirectoryFileFilter()));
			File sdkDir;
			if (files.length > 0) {
				sdkDir = new File(workdir, files[0])
				println sdkDir
			} else {

				ant.get(src:url, dest: new File(tempdir, filename), verbose:"yes", usetimestamp:"true")

				if (filename.endsWith(".zip")) {
					ant.unzip(dest:workdir,overwrite:"false"){
						fileset(dir:tempdir){
							include(name:filename)
						}
					}
				} else {
					ant.untar(dest:workdir, compression:"gzip", overwrite:"false"){
						fileset(dir:tempdir){
							include(name:filename)
						}
					}
				}

				files = workdir.list( new AndFileFilter(new WildcardFileFilter("android-sdk*"), new DirectoryFileFilter()));

				sdkDir = new File(workdir, files[0])
				println sdkDir

				// workaround for http://code.google.com/p/android/issues/detail?id=18868
				CommandLine adbCmdLine = new CommandLine(new File(sdkDir, "platform-tools/adb.exe"));
				adbCmdLine.addArgument("kill-server");
				DefaultExecutor adbExecutor = new DefaultExecutor();
				adbExecutor.setWorkingDirectory(new File(sdkDir, "platform-tools"));

				CommandLine cmdLine = new CommandLine("cmd.exe");
				cmdLine.addArgument("/c");
				cmdLine.addArgument("tools\\android.bat");
				cmdLine.addArgument("update");
				cmdLine.addArgument("sdk");
				cmdLine.addArgument("--no-ui");
				cmdLine.addArgument("--filter");
				cmdLine.addArgument(filter);

				DefaultExecutor executor = new DefaultExecutor();

				StopAdbOutputStream tos = new StopAdbOutputStream(System.out, adbCmdLine, adbExecutor);
				PumpStreamHandler streamHandler = new PumpStreamHandler(tos, System.err);

				executor.setStreamHandler(streamHandler);
				executor.setWorkingDirectory(sdkDir);

				int exitValue = executor.execute(cmdLine);

			}

			project.properties["android.sdk.path"] = sdkDir.absolutePath

			println "Set android.sdk.path=${sdkDir.absolutePath}"
		}
	}
}
