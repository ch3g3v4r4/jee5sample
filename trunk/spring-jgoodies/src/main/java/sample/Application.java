package sample;

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

    private JFrame frame;
    private JTextField textField;
    private JTextField textField_1;

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
                    e.printStackTrace();
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
                ColumnSpec.decode("right:default:grow"),},
            new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.PARAGRAPH_GAP_ROWSPEC,}));
        
        JLabel lblName = new JLabel("Name");
        frame.getContentPane().add(lblName, "1, 1, right, default");
        
        textField = new JTextField();
        frame.getContentPane().add(textField, "2, 1, fill, default");
        textField.setColumns(10);
        
        JLabel lblAge = new JLabel("Age");
        frame.getContentPane().add(lblAge, "1, 2, right, default");
        
        textField_1 = new JTextField();
        frame.getContentPane().add(textField_1, "2, 2, fill, default");
        textField_1.setColumns(10);
        
        JButton button = new JButton("New button");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        frame.getContentPane().add(button, "2, 4");
    }

}
