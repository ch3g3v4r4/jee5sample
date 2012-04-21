package manager

class AndroidSDKManager {

	URL downloadSDKUrl = new URL('http://dl.google.com/android/android-sdk_r18-windows.zip')
	File sdkDir = new File(System.getProperty("java.io.tmpdir"), 'android_sdk')
	String filter = 'system-image,platform-tool,android-10'

	AntBuilder ant = new AntBuilder()
	Downloader downloader = new Downloader()

	void install() {
		// Install SDK
		downloader.install(ant, downloadSDKUrl, sdkDir)

		// Download more components
		downloadAndroidSDKComponents()
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
		String filter = ''
		String cmd0 = androidCmd + ' list sdk'
		ant.echo(message: 'Executing ' + cmd0)
		Process p0 = cmd0.execute(null, toolsDir)
		Thread.start{
			def reader = new BufferedReader(new InputStreamReader(p0.in))
			String line
			while ((line = reader.readLine()) != null) {
				ant.echo(message: line)
				if (line.contains('Android SDK Tools') || line.contains('Android SDK Platform-tools') ||
					line.contains('SDK Platform Android') || line.contains('ARM EABI')) {
					if (!filter.equals('')) filter+= ','
					filter += line.split("\\-")[0].trim()
				}
			}
			ant.echo(message: 'DONE reading command output.')
		}
		p0.waitFor()

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
