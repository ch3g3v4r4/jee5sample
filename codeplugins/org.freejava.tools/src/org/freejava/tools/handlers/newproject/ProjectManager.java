package org.freejava.tools.handlers.newproject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class ProjectManager {

    public boolean createProject(NewProjectModel model) {

        boolean result;

        File targetDir = new File(model.getTargetDirectory());
        File projectDir = new File(targetDir, model.getArtifactId());
        if (projectDir.exists() || !projectDir.mkdir()) {
            // Never overwrite an existing folder
            result = false;
        } else {
            // create a project in projectDir
            result = createProject(projectDir, model);
        }

        return result;
    }

    private boolean createProject(File projectDir, NewProjectModel model) {

        boolean result;

        try {
            // Create directory structure
            // src/main/java
            // src/main/resources
            // src/test/java
            File srcDir = new File(projectDir, "src");
            srcDir.mkdir();
            File mainDir = new File(srcDir, "main");
            mainDir.mkdir();
            File javaDir = new File(mainDir, "java");
            javaDir.mkdir();
            File resourcesDir = new File(mainDir, "resources");
            resourcesDir.mkdir();
            File testDir = new File(srcDir, "test");
            testDir.mkdir();
            File testJavaDir = new File(testDir, "java");
            testJavaDir.mkdir();

            // Create pom.xml file
            POMXMLTemplate pomTemplate = new POMXMLTemplate();
			String pomFileContent = pomTemplate.generate(model);
			writeFile(new File(projectDir, "pom.xml"), pomFileContent);


            // For web projects, create src/main/webapp/WEB-INF/web.xml
            if (model.isWebProject()) {
                File webappDir = new File(mainDir, "webapp");
                webappDir.mkdir();
                File webInfDir = new File(webappDir, "WEB-INF");
                webInfDir.mkdir();
                WEBXMLTemplate template = new WEBXMLTemplate();
				String fileContent = template.generate(model);
				writeFile(new File(webInfDir, "web.xml"), fileContent);
            }

            // Create .checkstyle and src/main/config/checkstyle/checkstyle.xml
            if (model.isCheckstyleSupport()) {
            	// .checkstyle
            	CHECKSTYLEPREFSTemplate template = new CHECKSTYLEPREFSTemplate();
                String fileContent = template.generate(model);
                writeFile(new File(projectDir, ".checkstyle"), fileContent);

            	// checkstyle.xml
                File configDir = new File(mainDir, "config");
                configDir.mkdir();
                File checkstyleDir = new File(configDir, "checkstyle");
                checkstyleDir.mkdir();
                CHECKSTYLEXMLTemplate template1 = new CHECKSTYLEXMLTemplate();
				String fileContent1 = template1.generate(model);
				writeFile(new File(checkstyleDir, "checkstyle.xml"), fileContent1);
            }

            // Create .fbprefs and findbugs-exclude.xml
            if (model.isFindBugsSupport()) {
            	// .fbprefs
            	FBPREFSTemplate template = new FBPREFSTemplate();
                String fileContent = template.generate(model);
                writeFile(new File(projectDir, ".fbprefs"), fileContent);

            	// findbugs-exclude.xml
            	FINDBUGSEXCLUDEXMLTemplate template1 = new FINDBUGSEXCLUDEXMLTemplate();
                String fileContent1 = template1.generate(model);
                writeFile(new File(projectDir, "findbugs-exclude.xml"), fileContent1);
            }

            // Create .springBeans and src/main/resources/applicationContext.xml
            if (model.isSpringSupport()) {
            	// .springBeans
            	SPRINGBEANSXMLTemplate template = new SPRINGBEANSXMLTemplate();
                String fileContent = template.generate(model);
                writeFile(new File(projectDir, ".springBeans"), fileContent);

            	// applicationContext.xml
                APPLICATIONCONTEXTXMLTemplate template1 = new APPLICATIONCONTEXTXMLTemplate();
				String fileContent1 = template1.generate(model);
				writeFile(new File(resourcesDir, "applicationContext.xml"), fileContent1);
            }

            // Create .settings/org.hibernate.eclipse.console.prefs
            if (model.isHibernateSupport()) {
            	// org.hibernate.eclipse.console.prefs
                File settingsDir = new File(projectDir, ".settings");
                settingsDir.mkdir();
                HIBERNATEPREFSTemplate template = new HIBERNATEPREFSTemplate();
                String fileContent = template.generate(model);
                writeFile(new File(settingsDir, "org.hibernate.eclipse.console.prefs"), fileContent);
            }

            // Create README.txt file
            READMETXTTemplate template1 = new READMETXTTemplate();
			String fileContent1 = template1.generate(model);
			writeFile(new File(projectDir, "README.txt"), fileContent1);

            result = true;
        } catch (Exception e) {
            // If an exception happen, rollback by removing projectDir
            deleteDir(projectDir);
            result = false;
        }

        return result;
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private void writeFile(File file, String fileContent) throws IOException {
        Writer writer = new FileWriter(file);
        try {
            writer.write(fileContent);
        } finally {
            writer.close();
        }
    }
}
