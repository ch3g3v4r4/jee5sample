@Grab('com.jgoodies:looks:2.2.2')
@Grab('com.jgoodies:forms:1.2.1')
@Grab('com.jgoodies:validation:2.0.1')
@Grab('com.jgoodies:binding:2.0.6')


import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.*
import com.jgoodies.looks.plastic.PlasticLookAndFeel
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel
import com.jgoodies.looks.windows.WindowsLookAndFeel

public class SwingUI {
	public static void main(String[] args) {

		UIManager.setLookAndFeel(new WindowsLookAndFeel())

		def count = 0
		new SwingBuilder().edt {
			frame(title:'Frame', size:[300, 300], show: true, defaultCloseOperation: javax.swing.JFrame.EXIT_ON_CLOSE) {
				borderLayout()
				textlabel = label(text:"Click the button!", constraints: BL.NORTH)
				button(text:'Click Me',
						actionPerformed: {count++; textlabel.text = "Clicked ${count} time(s)."; println "clicked"},
						constraints:BL.SOUTH)
			}
		}
	}
}