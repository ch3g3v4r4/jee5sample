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
					line.contains('Android Support') ||
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

	public createBaseProjectForPhoneGap(String projectName, String targetID, File path, String packageName, String activityName) {
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


		// create installDebug.bat script - build.xml
		new File(path, "installDebug.bat").text =
			'call ant debug\r\n' +
			'"' + adbCmd + '"  install -r bin\\' + projectName + '-debug.apk\r\n' +
			'"' + adbCmd + '"  kill-server'

		// ADT Eclipse Plugin support - .project
		String projectText = getClass().getResourceAsStream("/resources/project").text
		new File(path, ".project").text = projectText.replaceAll("\\\$\\{projectName\\}", projectName)
		String classpathText = getClass().getResourceAsStream("/resources/classpath").text
		new File(path, ".classpath").text = classpathText.replaceAll("\\\$\\{projectName\\}", projectName)

	}
	public createProject(String projectName, File path, String packageName, String activityName) {
		installSDK()

		File toolsDir = new File(sdkDir, "tools")
		String androidCmd = 'cmd.exe /c android.bat'
		String adbCmd = new File(sdkDir, "platform-tools/adb.exe").absolutePath
		if (!System.properties['os.name'].toLowerCase().contains('windows')) {
			androidCmd = '/bin/sh -c android'
			adbCmd = new File(sdkDir, "platform-tools/adb").absolutePath
		}

		// Find latest API level
		int maxApiLevel = 0
		String targetID;
		// Create Android project
		String cmd = androidCmd + ' list targets'
		ant.echo(message: 'Executing ' + cmd)
		Process p = cmd.execute(null, toolsDir)
		def out = new StringBuilder()
		def err = new StringBuilder()
		p.waitForProcessOutput(out, err)
		ant.echo(message: err.toString())
		ant.echo(message: out.toString())
		Pattern pattern = Pattern.compile("android-([0-9]+)")
		Matcher matcher = pattern.matcher(out.toString())
		while (matcher.find()) {
			String levelStr = matcher.group(1)
			if (Integer.parseInt(levelStr) > maxApiLevel) maxApiLevel = Integer.parseInt(levelStr)
		}
		targetID = "android-" + maxApiLevel

		// Create Android project
		File projectPath = new File(path, projectName)
		projectPath.mkdirs()
		cmd = androidCmd + ' create project --name "' + projectName + '" --target "' + targetID + '" --path "' + projectPath.absolutePath + '" --package "' +  packageName + '" --activity "' +  activityName + '"'
		ant.echo(message: 'Executing ' + cmd)
		p = cmd.execute(null, toolsDir)
		out = new StringBuilder()
		err = new StringBuilder()
		p.waitForProcessOutput(out, err)
		ant.echo(message: err.toString())
		ant.echo(message: out.toString())
		ant.delete{
			fileset(dir: projectPath, includes: "ant.properties, build.xml")
		}

		// add <uses-sdk android:minSdkVersion="7" android:targetSdkVersion="15" /> to manifest file before <application> tag if it is missing
		def manifest = new File(projectPath, "AndroidManifest.xml").text
		if (manifest.indexOf('uses-sdk') == -1) {
			new File(projectPath, "AndroidManifest.xml").text = manifest.replaceAll("<application", '<uses-sdk android:minSdkVersion="7" android:targetSdkVersion="' + maxApiLevel + '" />\r\n    <application')
		}

		// ADD Maven support - pom.xml
		String pomText = getClass().getResourceAsStream("/resources/pom.xml").text
		new File(projectPath, "pom.xml").text = pomText.replaceAll("\\\$\\{projectName\\}", projectName).replaceAll("\\\$\\{packageName\\}", packageName).replaceAll("\\\$\\{androidAPINumber\\}", Integer.toString(maxApiLevel))
		//String androidJarVer = '4.0.1.2' // TODO: 4.0.1.2 is for platform=android-14 but platform=android-15 is not available on maven repo, how to fix it?
		//.replaceAll("\\\$\\{androidJarVer\\}", androidJarVer)
		new File(projectPath, "eclipse.bat").text = 'call mvn eclipse:eclipse groovy:execute'
		new File(projectPath, "build.bat").text = 'call mvn package'

		// modify Activity class to use SherlockActivity and modify Activity declaration to use android:theme="@style/Theme.Sherlock.Light"
		if (new File(projectPath, "pom.xml").text.indexOf('actionbarsherlock') != -1 && new File(projectPath, "AndroidManifest.xml").text.indexOf('SherlockActivity') == -1) {
			ant.replaceregexp(match: "import.+Activity;", replace: "import com.actionbarsherlock.app.SherlockActivity;", byline: "true") {
				fileset(dir:projectPath) {
					include(name: '**/' + activityName + ".java")
				}
			}
			ant.replaceregexp(match: "extends\\s+Activity", replace: "extends SherlockActivity", byline: "true") {
				fileset(dir:projectPath) {
					include(name: '**/' + activityName + ".java")
				}
			}
			// add android:theme="@style/Theme.Sherlock.Light" to Activity declaration
			ant.replaceregexp(match: "<activity ", replace: '<activity android:theme="@style/Theme.Sherlock.Light" ', byline: "true") {
				fileset(dir:projectPath) {
					include(name: 'AndroidManifest.xml')
				}
			}
		}

	}

	public static void main(String[] args) {
		AndroidSDKManager main = new AndroidSDKManager()
		main.sdkDir = new File('d:\\programs\\android_sdk')
		main.createProject('RSSReaderDemo', new File("d:\\projects\\android2\\RSSReaderDemo"), 'com.btloc.rssreaderdemo', 'RSSReaderDemoActivity')
	}
}
