import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell

def scriptdir = properties['scriptdir']
println scriptdir

Display display = new Display()
Shell shell = new Shell(display)
shell.setText("Hello World")
shell.setSize(200, 100)
shell.open()
while (!shell.isDisposed()) {
	if (!display.readAndDispatch())
		display.sleep()
}
display.dispose()