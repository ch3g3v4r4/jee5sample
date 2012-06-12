
class AndroidSampleProjectCreator {

	static main(args) {
		def dir = new File('d:/programs/android_sdk/samples/android-15')
		dir.eachDir {
			File projectDir = it
			gen(projectDir)
		}
	}

	static gen(File projectDir) {
		if (new File(projectDir, "AndroidManifest.xml").exists()) {
			def projectName = projectDir.name
			String projectText = AndroidSampleProjectCreator.class.getResourceAsStream("/resources/project").text
			new File(projectDir, ".project").text = projectText.replaceAll("\\\$\\{projectName\\}", projectName)

			String classpathText = AndroidSampleProjectCreator.class.getResourceAsStream("/resources/classpath").text
			new File(projectDir, ".classpath").text = classpathText.replaceAll("\\\$\\{projectName\\}", projectName)

			String projectPropsText = AndroidSampleProjectCreator.class.getResourceAsStream("/resources/project.properties").text
			new File(projectDir, "project.properties").text = projectPropsText.replaceAll("\\\$\\{projectName\\}", projectName)
		} else {
			projectDir.eachDir {
				File projectDir2 = it
				gen(projectDir2)
			}
		}
	}

}
