package manager;

import java.io.File;
import java.util.regex.Matcher
import java.util.regex.Pattern

public class AndroidProductCreator {

	private AndroidSDKManager sdk = new AndroidSDKManager()

	public void createBaseProjectForPhoneGap(String projectName, String targetID, File path, String packageName, String activityName) {

		sdk.installSDK()

		File toolsDir = new File(sdk.sdkDir, "tools")
		String androidCmd = 'cmd.exe /c android.bat'
		String adbCmd = new File(sdk.sdkDir, "platform-tools/adb.exe").absolutePath
		if (!System.properties['os.name'].toLowerCase().contains('windows')) {
			androidCmd = '/bin/sh -c android'
			adbCmd = new File(sdk.sdkDir, "platform-tools/adb").absolutePath
		}

		// Create Android project
		String cmd = androidCmd + ' create project --name "' + projectName + '" --target "' + targetID + '" --path "' + path.absolutePath + '" --package "' +  packageName + '" --activity "' +  activityName + '"'
		sdk.ant.echo(message: 'Executing ' + cmd)
		Process p = cmd.execute(null, toolsDir)
		def out = new StringBuilder()
		def err = new StringBuilder()
		p.waitForProcessOutput(out, err)
		sdk.ant.echo(message: err.toString())
		sdk.ant.echo(message: out.toString())

		// ADT Eclipse Plugin support - .project
		String projectText = getClass().getResourceAsStream("/project").text
		new File(path, ".project").text = projectText.replaceAll("\\\$\\{projectName\\}", projectName)
		String classpathText = getClass().getResourceAsStream("/classpath").text
		new File(path, ".classpath").text = classpathText.replaceAll("\\\$\\{projectName\\}", projectName)

	}

	public createProject(String projectName, File path, String packageName, String activityName) {
		sdk.installSDK()

		File toolsDir = new File(sdk.sdkDir, "tools")
		String androidCmd = 'cmd.exe /c android.bat'
		String adbCmd = new File(sdk.sdkDir, "platform-tools/adb.exe").absolutePath
		if (!System.properties['os.name'].toLowerCase().contains('windows')) {
			androidCmd = '/bin/sh -c android'
			adbCmd = new File(sdk.sdkDir, "platform-tools/adb").absolutePath
		}

		// Find latest API level
		int maxApiLevel = 0
		String targetID;
		// Create Android project
		String cmd = androidCmd + ' list targets'
		sdk.ant.echo(message: 'Executing ' + cmd)
		Process p = cmd.execute(null, toolsDir)
		def out = new StringBuilder()
		def err = new StringBuilder()
		p.waitForProcessOutput(out, err)
		sdk.ant.echo(message: err.toString())
		sdk.ant.echo(message: out.toString())
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
		sdk.ant.echo(message: 'Executing ' + cmd)
		p = cmd.execute(null, toolsDir)
		out = new StringBuilder()
		err = new StringBuilder()
		p.waitForProcessOutput(out, err)
		sdk.ant.echo(message: err.toString())
		sdk.ant.echo(message: out.toString())
		sdk.ant.delete{
			fileset(dir: projectPath, includes: "ant.properties, build.xml")
		}

		// add <uses-sdk android:minSdkVersion="7" android:targetSdkVersion="15" /> to manifest file before <application> tag if it is missing
		def manifest = new File(projectPath, "AndroidManifest.xml").text
		if (manifest.indexOf('uses-sdk') == -1) {
			new File(projectPath, "AndroidManifest.xml").text = manifest.replaceAll("<application", '<uses-sdk android:minSdkVersion="7" android:targetSdkVersion="' + maxApiLevel + '" />\r\n    <application')
		}

		// ADD Maven support - pom.xml
		String pomText = getClass().getResourceAsStream("/pom.xml").text
		new File(projectPath, "pom.xml").text = pomText.replaceAll("\\\$\\{projectName\\}", projectName).replaceAll("\\\$\\{packageName\\}", packageName).replaceAll("\\\$\\{androidAPINumber\\}", Integer.toString(maxApiLevel))
		//String androidJarVer = '4.0.1.2' // TODO: 4.0.1.2 is for platform=android-14 but platform=android-15 is not available on maven repo, how to fix it?
		//.replaceAll("\\\$\\{androidJarVer\\}", androidJarVer)
		new File(projectPath, "eclipse.bat").text = 'call mvn eclipse:eclipse groovy:execute'
		new File(projectPath, "build.bat").text = 'call mvn package'

		// modify Activity class to use SherlockActivity and modify Activity declaration to use android:theme="@style/Theme.Sherlock.Light"
		if (new File(projectPath, "pom.xml").text.indexOf('actionbarsherlock') != -1 && new File(projectPath, "AndroidManifest.xml").text.indexOf('SherlockActivity') == -1) {
			sdk.ant.replaceregexp(match: "import.+Activity;", replace: "import com.actionbarsherlock.app.SherlockActivity;", byline: "true") {
				fileset(dir:projectPath) {
					include(name: '**/' + activityName + ".java")
				}
			}
			sdk.ant.replaceregexp(match: "extends\\s+Activity", replace: "extends SherlockActivity", byline: "true") {
				fileset(dir:projectPath) {
					include(name: '**/' + activityName + ".java")
				}
			}
			// add android:theme="@style/Theme.Sherlock.Light" to Activity declaration
			sdk.ant.replaceregexp(match: "<activity ", replace: '<activity android:theme="@style/Theme.Sherlock.Light" ', byline: "true") {
				fileset(dir:projectPath) {
					include(name: 'AndroidManifest.xml')
				}
			}
		}

	}

	public static void main(String[] args) {
		AndroidProductCreator main = new AndroidProductCreator()
		main.sdk.sdkDir = new File('d:\\programs\\android-sdk')
		main.createProject('myapp', new File("d:\\projects\\myapp\\"), 'com.myapp', 'MainController')
	}

}
