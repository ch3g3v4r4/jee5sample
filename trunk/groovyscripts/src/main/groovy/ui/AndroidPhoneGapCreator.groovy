package ui

public class AndroidPhoneGapCreator {
	public static void main(String[] args) {
		File web = new File('D:/projects/jee5sample/android-reader-phonegap/WebContent')
		File target = new File('target')

		AntBuilder ant = new AntBuilder()
		File project = new File(target, 'project')
		ant.mkdir(dir: project)
	}
}