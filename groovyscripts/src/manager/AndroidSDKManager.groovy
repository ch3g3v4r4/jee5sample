package manager

class AndroidSDKManager {

	URL downloadSDKUrl = new URL('http://dl.google.com/android/android-sdk_r18-windows.zip')
	File sdkDir = new File(System.getProperty("java.io.tmpdir"), 'android_sdk')
	String filter = 'system-image,platform-tool,android-10'

	AntBuilder ant = new AntBuilder()
	Downloader downloader = new Downloader()

	void install() {
		downloader.install(ant, downloadSDKUrl, sdkDir)
	}

	public static void main(String[] args) {
		AndroidSDKManager main = new AndroidSDKManager()
		main.install()
	}
}
