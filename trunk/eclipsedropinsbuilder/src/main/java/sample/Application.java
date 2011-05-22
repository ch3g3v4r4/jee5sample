package sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.EventQueue;

import javax.swing.JFrame;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class.getName());

    private JFrame frame;
    private JTextField textField;
    private JTextField textField1;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Application window = new Application();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    LOGGER.error("err", e);
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Application() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("left:default"),
                ColumnSpec.decode("right:default:grow")},
            new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.PARAGRAPH_GAP_ROWSPEC}));

        JLabel lblName = new JLabel("Name");
        frame.getContentPane().add(lblName, "1, 1, right, default");

        textField = new JTextField();
        frame.getContentPane().add(textField, "2, 1, fill, default");
        textField.setColumns(10);

        JLabel lblAge = new JLabel("Age");
        frame.getContentPane().add(lblAge, "1, 2, right, default");

        textField1 = new JTextField();
        frame.getContentPane().add(textField1, "2, 2, fill, default");
        textField1.setColumns(10);

        JButton button = new JButton("New button");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        frame.getContentPane().add(button, "2, 4");
    }

}
