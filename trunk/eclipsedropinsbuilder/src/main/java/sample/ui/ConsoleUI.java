package sample.ui;

import java.io.File;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sample.core.DropInsBuilder;

public class ConsoleUI {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleUI.class.getName());

    private ConsoleUI() {
    }

    public static void main(String[] args) throws Exception {
        String currentDir = System.getProperty("user.dir");
        File scriptFile = new File(currentDir, "eclipseinstall.groovy");
        if (scriptFile.exists()) {
            LOGGER.info("Executing Eclipse installation script: " + scriptFile.getAbsolutePath());
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("groovy");
            engine.eval(FileUtils.readFileToString(scriptFile, "UTF-8"));
        } else {
            LOGGER.info("Generating Eclipse installation script: " + scriptFile.getAbsolutePath());
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");

            DropInsBuilder builder = context.getBean(DropInsBuilder.class);
            String script = builder.generateScript();
            FileUtils.write(scriptFile, script, "UTF-8");
            context.close();

            LOGGER.info("Generated Eclipse installation script: " + scriptFile.getAbsolutePath());
        }
    }
}
