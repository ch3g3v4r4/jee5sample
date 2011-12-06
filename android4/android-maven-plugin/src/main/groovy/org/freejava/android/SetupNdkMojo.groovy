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
import org.apache.maven.project.MavenProject
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.jfrog.maven.annomojo.annotations.MojoGoal;
import org.jfrog.maven.annomojo.annotations.MojoParameter;
import org.jfrog.maven.annomojo.annotations.MojoPhase;

@MojoGoal("setupndk")
@MojoPhase("initialize")
class SetupNdkMojo extends GroovyMojo {

	@MojoParameter( expression = '${project}' )
	public MavenProject project

	@MojoParameter(required=true, defaultValue="http://dl.google.com/android/ndk/android-ndk-r7-windows.zip")
	public String url

	void execute() {

		if (System.getenv("ANDROID_NDK_HOME") == null && project.properties["android.ndk.path"] == null) {

			log.info("==================================================================")
			log.info("No value found for ANDROID_NDK_HOME environment variable. Will install latest ANDROID NDK from Internet.")

			def filename = FilenameUtils.getName(url)
			def name = FilenameUtils.getBaseName(url)
			def tempdir = System.getProperty("java.io.tmpdir")
			def workdir = new File(System.getProperty("java.io.tmpdir"), name)


			ant.mkdir(dir:workdir)

			String[] files = workdir.list( new AndFileFilter(new WildcardFileFilter("android-ndk*"), new DirectoryFileFilter()))
			File sdkDir
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

				files = workdir.list( new AndFileFilter(new WildcardFileFilter("android-ndk*"), new DirectoryFileFilter()))

				sdkDir = new File(workdir, files[0])
				println sdkDir
			}

			project.properties["android.ndk.path"] = sdkDir.absolutePath

			log.info("Set android.ndk.path=${sdkDir.absolutePath}")
		}
	}
}
