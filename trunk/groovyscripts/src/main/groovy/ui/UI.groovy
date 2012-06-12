package ui
@GrabResolver(name='eclipse', root='http://maven.eclipse.org/nexus/content/repositories/testing/', m2Compatible='true')
//@Grab(group='org.eclipse.swt', module='org.eclipse.swt.win32.win32.x86', version='3.6.2')
@Grab(group='org.eclipse.swt', module='org.eclipse.swt.win32.win32.x86_64', version='3.6.2')

//@Grab(group='org.eclipse.swt.win32.win32', module='x86', version='3.3.0-v3346')

//@Grab(group='org.codehaus.groovy.modules.scriptom', module='scriptom', version='1.6.0')
//@Grab(group='net.sf.jacob-project', module='jacob', version='1.14.3')
//@Grab(group='net.sf.jacob-project', module='jacob', version='1.14.3', classifier='x86', type='dll')

import groovy.grape.Grape
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
//import org.codehaus.groovy.scriptom.*

public class Main {
    public static void main(String[] args) {

		//def scriptControl = new ActiveXObject("ScriptControl")
		//scriptControl.Language = "JScript"
		//println scriptControl.Eval('2.0 + 2.0;')

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Hello World");
        shell.setSize(200, 100);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}