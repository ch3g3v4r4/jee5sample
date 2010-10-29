package sample;

import javax.swing.JLabel;

import org.jdesktop.application.SingleFrameApplication;

public class Main extends SingleFrameApplication {

    @Override protected void startup() {

        // Create a label and use properties label.* from Main.properties
        JLabel label = new JLabel();
        label.setName("mylabel");

        show(label);
    }
    public static void main(String[] args) throws Exception {
        launch(Main.class, args);
    }
/*
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static void main(final String[] args) throws Exception {

        // Initialize Look and Feel
        UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");

        // Launch the application
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main application = new Main();
                try {
                    application.doMain(args);
                } catch (Exception e) {
                    String message = "Failed to launch " + Main.class;
                    LOGGER.log(Level.SEVERE, message, e);
                    throw new Error(message, e);
                }
            }
        });

    }

    private void doMain(String[] args) {
//      ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
//      ctx.getBean(Main.class).doMain(args);

        // Build Panel
        JFrame frame = new JFrame("Forms Tutorial :: Quick Start");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(buildPanel());

        // Pack, Set Location and Show
        frame.pack();
        Dimension paneSize = frame.getSize();
        Dimension screenSize = frame.getToolkit().getScreenSize();
        frame.setLocation((screenSize.width - paneSize.width) / 2, (int) ((screenSize.height - paneSize.height) * 0.45));
        frame.setVisible(true);
    }


    // Building *************************************************************
    private JTextField companyField;
    private JTextField contactField;
    private JTextField ptiField;
    private JTextField powerField;
    private JTextField radiusField;
    private JTextField diameterField;

    public JComponent buildPanel() {
        // Separating the component initialization and configuration
        // from the layout code makes both parts easier to read.
        companyField  = new JTextField();
        contactField  = new JTextField();
        ptiField      = new JTextField(6);
        powerField    = new JTextField(10);
        radiusField   = new JTextField(8);
        diameterField = new JTextField(8);

        // Create a FormLayout instance on the given column and row specs.
        // For almost all forms you specify the columns; sometimes rows are
        // created dynamically. In this case the labels are right aligned.
        FormLayout layout = new FormLayout(
                "right:pref, 3dlu, pref, 7dlu, right:pref, 3dlu, pref", // cols
                "p, 3dlu, p, 3dlu, p, 9dlu, p, 3dlu, p, 3dlu, p");      // rows

        // Specify that columns 1 & 5 as well as 3 & 7 have equal widths.
        layout.setColumnGroups(new int[][]{{1, 5}, {3, 7}});

        // Create a builder that assists in adding components to the container.
        // Wrap the panel with a standardized border.
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();

        // Obtain a reusable constraints object to place components in the grid.
        CellConstraints cc = new CellConstraints();

        // Fill the grid with components; the builder offers to create
        // frequently used components, e.g. separators and labels.

        // Add a titled separator to cell (1, 1) that spans 7 columns.
        builder.addSeparator("General",   cc.xyw(1,  1, 7));
        builder.addLabel("Company",       cc.xy (1,  3));
        builder.add(companyField,         cc.xyw(3,  3, 5));
        builder.addLabel("Contact",       cc.xy (1,  5));
        builder.add(contactField,         cc.xyw(3,  5, 5));

        builder.addSeparator("Propeller", cc.xyw(1,  7, 7));
        builder.addLabel("PTI/kW",        cc.xy (1,  9));
        builder.add(ptiField,             cc.xy (3,  9));
        builder.addLabel("Power/kW",      cc.xy (5,  9));
        builder.add(powerField,           cc.xy (7,  9));
        builder.addLabel("R/mm",          cc.xy (1, 11));
        builder.add(radiusField,          cc.xy (3, 11));
        builder.addLabel("D/mm",          cc.xy (5, 11));
        builder.add(diameterField,        cc.xy (7, 11));

        // The builder holds the layout container that we now return.
        return builder.getPanel();
    }
*/
}
