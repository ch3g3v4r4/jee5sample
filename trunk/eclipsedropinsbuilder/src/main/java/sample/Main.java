package sample;

import java.util.logging.LogManager;

import org.jdesktop.application.SingleFrameApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sample.ui.SingleFrameUI;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

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

        ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        SingleFrameUI.setContext(context);

        SingleFrameApplication.launch(SingleFrameUI.class, args);
        LOGGER.info("Exiting application.");
    }
}
