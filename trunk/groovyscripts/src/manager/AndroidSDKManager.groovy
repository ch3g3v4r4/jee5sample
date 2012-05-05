package manager

import java.util.regex.Matcher
import java.util.regex.Pattern

class AndroidSDKManager {

	URL downloadSDKUrl = new URL('http://dl.google.com/android/android-sdk_r18-windows.zip')
	File sdkDir = new File(System.getProperty("java.io.tmpdir"), 'android_sdk')
	String optionalFilter

	AntBuilder ant = new AntBuilder()
	Downloader downloader = new Downloader()

	void installSDK() {
		if (!sdkDir.exists() || sdkDir.isDirectory() && sdkDir.listFiles().length == 0) {
			// Install SDK
			downloader.install(ant, downloadSDKUrl, sdkDir)
			// Download more components
			downloadAndroidSDKComponents()
		}
	}

	public void downloadAndroidSDKComponents() {
		File toolsDir = new File(sdkDir, "tools")
		String androidCmd = 'cmd.exe /c android.bat'
		String adbCmd = new File(sdkDir, "platform-tools/adb.exe").absolutePath
		if (!System.properties['os.name'].toLowerCase().contains('windows')) {
			androidCmd = '/bin/sh -c android'
			adbCmd = new File(sdkDir, "platform-tools/adb").absolutePath
		}

		// Create filter
		String filter = this.optionalFilter
		if (filter == null || filter.equals('')) {
			ant.echo(message: 'Will build Android SDK component filter.')

			// execute command android list sdk to get list of components
			String cmd0 = androidCmd + ' list sdk'
			ant.echo(message: 'Executing ' + cmd0)
			Process p0 = cmd0.execute(null, toolsDir)
			def out = new StringBuilder()
			def err = new StringBuilder()
			p0.waitForProcessOutput(out, err)
			ant.echo(message: out.toString())

			// parse output to find necessary numbers
			ant.echo(message: 'Parsing output.')

			def maxAPI = '0'
			Reader reader = new BufferedReader(new StringReader(out.toString()))
			String line
			while ((line = reader.readLine()) != null) {
				Matcher matcher = Pattern.compile('Android .+ API ([0-9]+)').matcher(line)
				if (matcher.find()) {
					if (Integer.parseInt(matcher.group(1)) > Integer.parseInt(maxAPI)) maxAPI = matcher.group(1)
				}
			}
			maxAPI = 'API ' + maxAPI
			def compatAPI = 'API 10' // Android 2.3.3, API 10

			reader = new BufferedReader(new StringReader(out.toString()))
			while ((line = reader.readLine()) != null) {
				if (line.contains('Android SDK Tools') ||
					line.contains('Android SDK Platform-tools') ||
					line.contains('SDK Platform Android') && (line.contains(maxAPI) || line.contains(compatAPI)) ||
					line.contains('ARM EABI') && (line.contains(maxAPI) || line.contains(compatAPI))) {
					if (filter != null && !filter.equals('')) filter += ',' else filter = ''
					filter += line.split("\\-")[0].trim()
				}
			}
			ant.echo(message: 'Selected filter=' + filter)
		}

		// Download components
		String cmd1 = androidCmd + ' update sdk --no-ui --filter ' + filter
		ant.echo(message: 'Executing ' + cmd1)
		Process p1 = cmd1.execute(null, toolsDir)

		// workaround issue http://code.google.com/p/android/issues/detail?id=18868 to fix issue process hangs
		Thread.start{
			def reader = new BufferedReader(new InputStreamReader(p1.in))
			String line
			while ((line = reader.readLine()) != null) {
				ant.echo(message: line)
				if (line.trim().startsWith("Done.") && line.trim().endsWith("installed.")) {
					println 'GOTCHA!'
					String cmd2 = '"' + adbCmd + '" kill-server'
					ant.echo(message: 'Executing ' + cmd2)
					cmd2.execute(null, new File(sdkDir, "platform-tools")).waitFor()
					println 'KILLED SERVER!'
					break
				}
			}
		}
		p1.waitFor()
	}

	public createProject(String projectName, String targetID, File path, String packageName, String activityName) {
		installSDK()

		File toolsDir = new File(sdkDir, "tools")
		String androidCmd = 'cmd.exe /c android.bat'
		String adbCmd = new File(sdkDir, "platform-tools/adb.exe").absolutePath
		if (!System.properties['os.name'].toLowerCase().contains('windows')) {
			androidCmd = '/bin/sh -c android'
			adbCmd = new File(sdkDir, "platform-tools/adb").absolutePath
		}

		// Create Android project
		String cmd = androidCmd + ' create project --name "' + projectName + '" --target "' + targetID + '" --path "' + path.absolutePath + '" --package "' +  packageName + '" --activity "' +  activityName + '"'
		ant.echo(message: 'Executing ' + cmd)
		Process p = cmd.execute(null, toolsDir)
		def out = new StringBuilder()
		def err = new StringBuilder()
		p.waitForProcessOutput(out, err)
		ant.echo(message: err.toString())
		ant.echo(message: out.toString())

		// REMOVE ANT BUILD FILES
		// ADT Eclipse Plugin support - .project
		//String projectText = getClass().getResourceAsStream("/resources/project").text
		//new File(path, ".project").text = projectText.replaceAll("\\\$\\{projectName\\}", projectName)
		//String classpathText = getClass().getResourceAsStream("/resources/classpath").text
		//new File(path, ".classpath").text = classpathText.replaceAll("\\\$\\{projectName\\}", projectName)
		// create installDebug.bat script - build.xml
		//new File(path, "installDebug.bat").text =
		//	'call ant debug\r\n' +
		//	'"' + adbCmd + '"  install -r bin\\' + projectName + '-debug.apk\r\n' +
		//	'"' + adbCmd + '"  kill-server'
		//ant.delete{
		//	fileset(dir: path, includes: "local.properties, ant.properties, build.xml")
		//}

		// ADD Maven support - pom.xml
		String androidJarVer = '4.0.1.2' // TODO: 4.0.1.2 is for platform=android-14 but platform=android-15 is not available on maven repo, how to fix it?
		String androidAPINumber = targetID.split('-')[1]
		String pomText = getClass().getResourceAsStream("/resources/pom.xml").text
		new File(path, "pom.xml").text = pomText.replaceAll("\\\$\\{projectName\\}", projectName).replaceAll("\\\$\\{packageName\\}", packageName).replaceAll("\\\$\\{androidJarVer\\}", androidJarVer).replaceAll("\\\$\\{androidAPINumber\\}", androidAPINumber)
		new File(path, "env.bat").text = 'if "%ANDROID_HOME%"=="" goto setDefaultAndroidHome\r\ngoto done\r\n:setDefaultAndroidHome\r\nset ANDROID_HOME=' + sdkDir.absolutePath + '\r\n:done'
		new File(path, "eclipse.bat").text = 'call env.bat\r\ncall mvn eclipse:eclipse'
		new File(path, "build.bat").text = 'call env.bat\r\ncall mvn package'

	}

	public static void main(String[] args) {
		AndroidSDKManager main = new AndroidSDKManager()
		main.sdkDir = new File('d:\\programs\\android_sdk')
		main.createProject('AndroidPodcasterx', 'android-15', new File("d:\\projects\\jee5sample\\AndroidPodcasterx"), 'com.freejava.podcast', 'AndroidPodcasterx')
	}
}
