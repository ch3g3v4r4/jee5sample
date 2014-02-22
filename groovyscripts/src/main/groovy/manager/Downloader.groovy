package manager

import groovy.util.AntBuilder;

import java.io.File;
import java.net.URL;

class Downloader {

	void install(URL downloadSDKUrl, File sdkDir) {
		AntBuilder ant = new AntBuilder()

		if (!sdkDir.exists() || sdkDir.isDirectory() && sdkDir.listFiles().length == 0) {

			def tempDir = System.getProperty("java.io.tmpdir")
			def fileName = downloadSDKUrl.toString().substring(downloadSDKUrl.toString().lastIndexOf('/') + 1)
			ant.get(src:downloadSDKUrl, dest: new File(tempDir, fileName), verbose:"yes", skipexisting:"true")

			ant.mkdir(dir: sdkDir)

			FileInputStream fin = new FileInputStream(new File(tempDir, fileName))
			byte[] bytes = new byte[2]
			fin.read(bytes)
			fin.close()

			if (bytes[0] == 0x50 && bytes[1] == 0x4b) { // 'PK' : zip
				ant.unzip(dest:sdkDir,overwrite:"false"){
					fileset(dir:tempDir){
						include(name:fileName)
					}
				}
			} else {
				ant.untar(dest:sdkDir, compression:"gzip", overwrite:"false"){
					fileset(dir:tempDir){
						include(name:fileName)
					}
				}
			}
			if (sdkDir.listFiles().length == 1 && sdkDir.listFiles()[0].isDirectory()) {
				File fromDir = sdkDir.listFiles()[0]
				ant.move(todir: sdkDir) {
					fileset(dir:fromDir, defaultexcludes: 'no')
				}
			}
		}
	}
}
