package sample;

import java.util.Date;
import java.util.logging.LogManager;

import javax.swing.JLabel;

import org.jdesktop.application.SingleFrameApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Main extends SingleFrameApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

    @Override
    protected void startup() {
        // Create a label and use properties label.* from Main.properties
        JLabel label = new JLabel();
        label.setName("mylabel");
        show(label);
    }

    public static void main(String[] args) throws Exception {

        LogManager.getLogManager().reset(); // disable default java.util.logging.ConsoleHandler which logs to stdout
        SLF4JBridgeHandler.install(); // setup jul-to-slf4j to redirect all java.util.logging events to slf4j

        LOGGER.info("Launching application at " + new Date());
        launch(Main.class, args);
    }
}
