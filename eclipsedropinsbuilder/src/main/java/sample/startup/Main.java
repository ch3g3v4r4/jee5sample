package sample.startup;

import java.io.File;
import java.util.logging.LogManager;
import java.util.zip.ZipException;

import org.apache.commons.exec.ExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import sample.core.Eclipse;
import sample.core.EclipseDropInsBuilder;
import sample.core.Plugin;
import sample.core.Profile;

import com.thoughtworks.xstream.XStream;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Main() {
    }

    /**
     * MAIN ENTRY POINT.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        LogManager.getLogManager().reset(); // disable default java.util.logging.ConsoleHandler which logs to stdout
        SLF4JBridgeHandler.install(); // setup jul-to-slf4j to redirect all java.util.logging events to slf4j

        LOGGER.info("Launching application...");

        Resource config;
        if (args.length > 0) {
            if (new File(args[0]).exists()) {
                config = new FileSystemResource(args[0]);
            } else {
                config = new DefaultResourceLoader().getResource(args[0]);
            }
        } else {
            config = new ClassPathResource("/eclipse.xml");
        }
        XStream xstream = new XStream();
        xstream.alias("eclipse", Eclipse.class);
        xstream.alias("plugin", Plugin.class);
        xstream.alias("profile", Profile.class);
        xstream.addImplicitCollection(Plugin.class, "updateSites", "updateSite", String.class);
        xstream.addImplicitCollection(Plugin.class, "featureIds", "featureId", String.class);
        xstream.addImplicitCollection(Profile.class, "dropinsNames", "dropinsName", String.class);
        Eclipse e = (Eclipse) xstream.fromXML(config.getInputStream());
        int exitValue;
        do {
            exitValue = 0;
            try {
                EclipseDropInsBuilder builder = new EclipseDropInsBuilder();
                builder.build(e);
            } catch (ExecuteException ex) {
                LOGGER.debug("exception", ex);
                exitValue = ex.getExitValue();
            } catch (ZipException ex) {
                LOGGER.debug("exception", ex);
                exitValue = 14;
            }
        } while (exitValue == 13 || exitValue == 14);

        LOGGER.info("Exiting application...");
    }
}
