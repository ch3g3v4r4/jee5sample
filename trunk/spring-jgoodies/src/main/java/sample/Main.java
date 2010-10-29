package sample;

import java.util.logging.LogManager;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main extends SingleFrameApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getName());

    private ApplicationContext context;

    @Override
    protected void initialize(String[] args) {
        LOGGER.info("BEGIN - Initializing BACK END.");

        this.context = new ClassPathXmlApplicationContext("/applicationContext.xml");

        LOGGER.info("END - Initializing BACK END.");
    }

    @Override
    protected void startup() {
        LOGGER.info("BEGIN - Initializing FRONT END.");

        SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
        getMainFrame().setJMenuBar(createMenuBar());

        // Create a label and use properties label.* from Main.properties
        JLabel label = new JLabel();
        label.setName("mylabel");

        show(label);

        LOGGER.info("END - Initializing FRONT END.");
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        String[] viewMenuActionNames = {
            "showWindow0",//@Action method
            "---",
            "quit"//@Action method
        };
        menuBar.add(createMenu("viewMenu", viewMenuActionNames));
        return menuBar;
    }

    private JMenu createMenu(String menuName, String[] actionNames) {
        JMenu menu = new JMenu();
        menu.setName(menuName);
        for (String actionName : actionNames) {
            if (actionName.equals("---")) {
                menu.add(new JSeparator());
            }
            else {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(getContext().getActionMap().get(actionName));
                menu.add(menuItem);
            }
        }
        return menu;
    }


    @Action
    public void showWindow0() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000000; i++) System.out.println(111);
                System.out.println("DONE");
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (Exception ignore) {  }
        }
    }

    @Override
    protected void ready() {
        super.ready();
        LOGGER.info("Application is READY.");
    }

    @Override
    protected void shutdown() {
        LOGGER.info("BEGIN - Shutting down.");

        super.shutdown();

        LOGGER.info("END - Shutting down.");
    }

    public static void main(String[] args) throws Exception {

        LogManager.getLogManager().reset(); // disable default java.util.logging.ConsoleHandler which logs to stdout
        SLF4JBridgeHandler.install(); // setup jul-to-slf4j to redirect all java.util.logging events to slf4j

        LOGGER.info("Launching application...");
        launch(Main.class, args);
    }
}
