package sample.startup;

import java.io.File;
import java.util.logging.LogManager;

import org.apache.commons.codec.digest.DigestUtils;
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
        xstream.addImplicitCollection(Plugin.class, "updateSites", "updateSite", String.class);
        xstream.addImplicitCollection(Plugin.class, "featureIds", "featureId", String.class);
        Eclipse e = (Eclipse) xstream.fromXML(config.getInputStream());
        String hash = DigestUtils.md5Hex(config.getInputStream());
        EclipseDropInsBuilder builder = new EclipseDropInsBuilder();
        builder.build(e, hash);

        LOGGER.info("Exiting application...");
    }
}
