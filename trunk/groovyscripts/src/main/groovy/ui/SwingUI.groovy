package ui
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.swing.*

public class SwingUI {
	public static void main(String[] args) {
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