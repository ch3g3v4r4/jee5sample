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

		// Update components
		File toolsDir = new File(sdkDir, "tools")
		if (System.properties['os.name'].toLowerCase().contains('windows')) {
			ant.exec(dir: toolsDir, executable: "cmd.exe") {
				arg(value: "/c")
				arg(value: "android.bat")
				arg(value: "update")
				arg(value: "sdk")
				arg(value: "--no-ui")
				arg(value: "--filter")
				arg(value: filter)
			}
		} else {
			ant.exec(dir: toolsDir, executable: "/bin/sh") {
				arg(value: "-c")
				arg(value: "android")
				arg(value: "update")
				arg(value: "sdk")
				arg(value: "--no-ui")
				arg(value: "--filter")
				arg(value: filter)
			}
		}
	}

	public static void main(String[] args) {
		AndroidSDKManager main = new AndroidSDKManager()
		main.install()
	}
}
