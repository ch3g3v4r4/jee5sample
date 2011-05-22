package sample.ui;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SingleFrameUI extends SingleFrameApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleFrameUI.class.getName());

    private ClassPathXmlApplicationContext context;

    @Override
    protected void initialize(String[] args) {
        LOGGER.info("BEGIN - Initializing Core - BEFORE GUI.");
        this.context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        LOGGER.info("END - Initializing Core - BEFORE GUI.");
    }

    @Override
    protected void startup() {
        LOGGER.info("BEGIN - Initializing GUI.");

        SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());

        // Create menu
        getMainView().setMenuBar(createMenuBar());

        // Create components
        getMainView().setComponent(createComponent());

        // Display the application window
        show(getMainView());

        LOGGER.info("END - Initializing GUI.");
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu();
        menu.setName("viewMenu");
        String[] actionNames = {
                "action1", //@Action method
                "---",
                "quit"//@Action method
            };
        for (String actionName : actionNames) {
            if (actionName.equals("---")) {
                menu.add(new JSeparator());
            } else {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(getContext().getActionMap().get(actionName));
                menu.add(menuItem);
            }
        }
        menuBar.add(menu);

        return menuBar;
    }

    private JComponent createComponent() {
        // Create a label and use properties label.* from Main.properties
        //JLabel label = new JLabel();
        //label.setName("mylabel");
        //return label;
    	JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setRows(20);
        textArea.setColumns(50);
        textArea.append("AA");
        return new JScrollPane(textArea);
    }

    @Action
    public void action1() {
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
        LOGGER.info("GUI is READY.");
    }

    @Override
    protected void shutdown() {
        LOGGER.info("BEGIN - Shutting down GUI.");
        super.shutdown();
        LOGGER.info("END - Shutting down GUI.");

        LOGGER.info("BEGIN - Shutting down Core.");
        this.context.close();
        LOGGER.info("END - Shutting down Core.");

    }

}
