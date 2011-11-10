import org.apache.commons.lang.SystemUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.io.FilenameUtils;

def filter = 'system-image,doc,source,platform-tool,android-10,sample-10'
def url = "http://dl.google.com/android/android-sdk_r15-windows.zip"
if (SystemUtils.IS_OS_LINUX ) {
	url = "http://dl.google.com/android/android-sdk_r15-linux.tgz"
}

if (System.getenv("ANDROID_HOME") == null && project.properties["android.sdk.path"] == null) {

	println "=================================================================="
	println "No value found for ANDROID_HOME environment variable. Will install latest ANDROID SDK from Internet."

	def filename = FilenameUtils.getName(url)
	def name = FilenameUtils.getBaseName(url)
	def workdir = new File(System.getProperty("java.io.tmpdir"), name)


	ant.mkdir(dir:workdir)

	String[] files = workdir.list( new AndFileFilter(new WildcardFileFilter("android-sdk*"), new DirectoryFileFilter()));
	File sdkDir;
	if (files.length > 0) {
		sdkDir = new File(workdir, files[0])
		println sdkDir
	} else {

		ant.get(src:url, dest: new File(workdir, filename), verbose:"yes", usetimestamp:"true")

		ant.unzip(dest:workdir,overwrite:"false"){
			fileset(dir:workdir){
				include(name:"*.zip")
			}
		}

		files = workdir.list( new AndFileFilter(new WildcardFileFilter("android-sdk*"), new DirectoryFileFilter()));

		sdkDir = new File(workdir, files[0])
		println sdkDir

		ant.untar(dest:workdir, compression:"gzip", overwrite:"false"){
			fileset(dir:workdir){
				include(name:"*.tgz")
			}
		}
		if (SystemUtils.IS_OS_WINDOWS ) {
			ant.exec(dir:sdkDir, executable:"cmd.exe"){
				arg(line:"/c tools\\android.bat update sdk --no-ui --filter " + filter)
			}
		}

		if (SystemUtils.IS_OS_LINUX ) {
			ant.exec(dir:sdkDir, executable:"bash"){
				arg(line:" tools/android.sh update sdk --no-ui --filter " + filter)
			}
		}
	}

	project.properties["android.sdk.path"] = sdkDir.absolutePath

	println "Set android.sdk.path=${sdkDir.absolutePath}"
}
