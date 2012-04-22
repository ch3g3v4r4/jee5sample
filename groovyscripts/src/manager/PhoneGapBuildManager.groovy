package manager

import groovy.util.AntBuilder;

import java.io.File;

class PhoneGapBuildManager {
	AntBuilder ant = new AntBuilder()
	AndroidSDKManager androidSDK = new AndroidSDKManager()
	PhoneGapSDKManager phonegap = new PhoneGapSDKManager()

	public createAndroidBasedPhoneGapProject(String projectName, String targetID, File path, String packageName, String activityName, File wwwDir) {
		if (!path.exists() || path.isDirectory() && path.listFiles().length == 0) {
			androidSDK.createProject(projectName, targetID, path, packageName, activityName)

			// Configure Android project with PhoneGap
			if (!phonegap.sdkDir.exists() || phonegap.sdkDir.isDirectory() && phonegap.sdkDir.listFiles().length == 0) {
				phonegap.install()
			}

			// copy phonegap asssets
			ant.copy(todir: new File(path, "libs")){
				fileset(dir: new File(phonegap.sdkDir, 'lib/android'), includes: "*.jar")
			}
			ant.copy(todir: new File(path, "res/xml")){
				fileset(dir: new File(phonegap.sdkDir, 'lib/android/xml'))
			}
			ant.copy(todir: new File(path, "assets/www")){
				fileset(dir: new File(phonegap.sdkDir, 'lib/android'), includes: "*.js")
			}
			ant.copy(todir: new File(path, "assets/www")){
				fileset(dir: new File(phonegap.sdkDir, 'lib/android'), includes: "cordova-*.js")
				globmapper(from:"*", to:"cordova.js")
			}
			ant.copy(todir: new File(path, "assets/www")){
				fileset(dir: new File(phonegap.sdkDir, 'lib/android'), includes: "cordova-*.js")
				globmapper(from:"*", to:"phonegap.js")
			}

			// modify Activity class to load index.html
			ant.replaceregexp(match: "import.+Activity;", replace: "import org.apache.cordova.*;", byline: "true") {
				fileset(dir:path) {
					include(name: '**/' + activityName + ".java")
				}
			}
			ant.replaceregexp(match: "extends\\s+Activity", replace: "extends DroidGap", byline: "true") {
				fileset(dir:path) {
					include(name: '**/' + activityName + ".java")
				}
			}
			ant.replaceregexp(match: "setContentView.+", replace: "super.loadUrl(\"file:///android_asset/www/index.html\");", byline: "true") {
				fileset(dir:path) {
					include(name: '**/' + activityName + ".java")
				}
			}

			// modify AndroidManifest.xml
			// add <supports-screens + <uses-permission
			String permissions = readPermissionsFromExample(new File(phonegap.sdkDir, 'lib/android/example/AndroidManifest.xml'))
			String applicationLine = ''
			new File(path, 'AndroidManifest.xml').eachLine { line ->
				if (line.matches('\\s*<application.+')) applicationLine = line
			}
			ant.replace(token: applicationLine, value: permissions + '\n' + applicationLine) {
				fileset(dir:path) {
					include(name: 'AndroidManifest.xml')
				}
			}
			// add android:configChanges="orientation|keyboardHidden">
			ant.replaceregexp(match: "<activity ", replace: '<activity android:configChanges="orientation|keyboardHidden" ', byline: "true") {
				fileset(dir:path) {
					include(name: 'AndroidManifest.xml')
				}
			}
			// add second <activity
			String secondActivity = '        <activity android:name="org.apache.cordova.DroidGap" android:label="@string/app_name" android:configChanges="orientation|keyboardHidden"><intent-filter></intent-filter></activity>'
			ant.replace(token: '</activity>', value: '</activity>\n' + secondActivity) {
				fileset(dir:path) {
					include(name: 'AndroidManifest.xml')
				}
			}

			// copy developer www assets
			if (wwwDir != null && wwwDir.exists()) {
				ant.copy(todir: new File(path, "assets/www")){
					fileset(dir: wwwDir)
				}
			}

			// create index.html if it is not available exist from developer assets
			if (!new File(path, "assets/www/index.html").exists()) {
				new File(path, "assets/www/index.html").text =
					'<!DOCTYPE html>\n<html>\n<head>\n<title>PhoneGap</title>\n<script type="text/javascript" charset="utf-8" src="phonegap.js"></script>\n</head>\n<body>\n<h1>Hello World</h1>\n</body>\n</html>'
			}

			// create installDebug.bat script
			String adbCmd = new File(androidSDK.sdkDir, "platform-tools/adb.exe").absolutePath
			if (!System.properties['os.name'].toLowerCase().contains('windows')) {
				adbCmd = new File(androidSDK.sdkDir, "platform-tools/adb").absolutePath
			}
			new File(path, "installDebug.bat").text =
				'call ant debug\r\n' +
				'"' + adbCmd + '"  install -r bin\\' + projectName + '-debug.apk\r\n' +
				'"' + adbCmd + '"  kill-server'
		} else {
			ant.echo(message: 'Will not create new project. Found existing project at specified path :' + path.absolutePath)
		}

	}

	public String readPermissionsFromExample(File androidManifest) {
		String text = androidManifest.text
		int manifestStart = text.indexOf('<manifest')
		int manifestEnd = text.indexOf('>', manifestStart)
		int nextLineAfterManifestEnd = manifestEnd
		while (text.charAt(nextLineAfterManifestEnd) == '\r' || text.charAt(nextLineAfterManifestEnd) == '\n') nextLineAfterManifestEnd++
		int lastPermissionStart = text.lastIndexOf('<uses-permission')
		int lastPermissionEnd = text.indexOf('/>', lastPermissionStart) + 1
		String result = text.substring(nextLineAfterManifestEnd, lastPermissionEnd + 1)
		return result
	}

	public static void main(String[] args) {
		PhoneGapBuildManager main = new PhoneGapBuildManager()
		main.createAndroidBasedPhoneGapProject('myapp', 'android-10', new File("D:\\myapp"), 'com.freejava.myapp', 'MyActivity', null)
	}
}
