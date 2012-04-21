package manager

import java.util.regex.Matcher
import java.util.regex.Pattern

class AndroidSDKManager {

	URL downloadSDKUrl = new URL('http://dl.google.com/android/android-sdk_r18-windows.zip')
	File sdkDir = new File(System.getProperty("java.io.tmpdir"), 'android_sdk')
	String optionalFilter

	AntBuilder ant = new AntBuilder()
	Downloader downloader = new Downloader()

	void install() {
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

			def compatAPI = '10' // Android 2.3.3, API 10

			reader = new BufferedReader(new StringReader(out.toString()))
			while ((line = reader.readLine()) != null) {
				if (line.contains('Android SDK Tools') ||
					line.contains('Android SDK Platform-tools') ||
					line.contains('SDK Platform Android') && (line.contains(maxAPI) || line.contains(compatAPI)) ||
					line.contains('ARM EABI') && (line.contains(maxAPI) || line.contains(compatAPI)) {
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

	public static void main(String[] args) {
		AndroidSDKManager main = new AndroidSDKManager()
		main.install()
	}
}
