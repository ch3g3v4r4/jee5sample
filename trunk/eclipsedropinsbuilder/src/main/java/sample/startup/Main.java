package sample.startup;

import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import sample.ui.ConsoleUI;

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
        //SingleFrameApplication.launch(SingleFrameUI.class, args);
        ConsoleUI.main(args);
    }
}
